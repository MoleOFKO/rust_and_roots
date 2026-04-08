package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Properties;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public final class SmtpOtpSender {
    private static final String DEFAULT_SENDER_EMAIL = "rustandrootsgame@gmail.com";
    private static volatile String lastError = "";
    private static volatile Properties fileSettings;
    private static volatile boolean fileSettingsLoaded;

    private SmtpOtpSender() {}

    public static boolean sendVerificationCode(String toEmail, String code) {
        String subject = "Rust & Roots verification code";
        String body = "Your verification code is: " + code + "\r\n"
            + "This code expires in 10 minutes.\r\n"
            + "If you did not request this, you can ignore this email.\r\n";
        return sendPlainTextEmail(toEmail, subject, body);
    }

    public static boolean sendAccountCreatedSuccess(String toEmail) {
        String subject = "Account created successfully!";
        String body = "Account created successfully! Rust&Roots\r\n"
            + "Your Rust&Roots account is ready.\r\n";
        return sendPlainTextEmail(toEmail, subject, body);
    }

    public static boolean sendPlainTextEmail(String toEmail, String subject, String body) {
        lastError = "";
        String host = readSetting("RNR_SMTP_HOST", "smtp.gmail.com");
        int port = parsePort(readSetting("RNR_SMTP_PORT", "465"));
        String user = readSetting("RNR_SMTP_USER", DEFAULT_SENDER_EMAIL);
        String pass = readSetting("RNR_SMTP_PASS", null);
        String from = readSetting("RNR_SMTP_FROM", DEFAULT_SENDER_EMAIL);

        if (isBlank(host) || port <= 0 || isBlank(user) || isBlank(pass) || isBlank(from) || isBlank(toEmail)) {
            lastError = "Missing SMTP settings (RNR_SMTP_PASS is required).";
            return false;
        }

        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            expectReplyStartsWith(in, "220");
            sendLine(out, "EHLO rust-and-roots");
            expectReplyStartsWith(in, "250");

            sendLine(out, "AUTH LOGIN");
            expectReplyStartsWith(in, "334");

            sendLine(out, Base64.getEncoder().encodeToString(user.getBytes(StandardCharsets.UTF_8)));
            expectReplyStartsWith(in, "334");

            sendLine(out, Base64.getEncoder().encodeToString(pass.getBytes(StandardCharsets.UTF_8)));
            expectReplyStartsWith(in, "235");

            sendLine(out, "MAIL FROM:<" + from + ">");
            expectReplyStartsWith(in, "250");

            sendLine(out, "RCPT TO:<" + toEmail + ">");
            expectReplyStartsWith(in, "250");

            sendLine(out, "DATA");
            expectReplyStartsWith(in, "354");

            sendLine(out, "From: " + from);
            sendLine(out, "To: " + toEmail);
            sendLine(out, "Subject: " + subject);
            sendLine(out, "Date: " + ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
            sendLine(out, "Content-Type: text/plain; charset=UTF-8");
            sendLine(out, "");
            sendLine(out, body);
            sendLine(out, ".");
            expectReplyStartsWith(in, "250");

            sendLine(out, "QUIT");
            expectReplyStartsWith(in, "221");
            return true;
        } catch (Exception ex) {
            String msg = ex.getMessage();
            lastError = ex.getClass().getSimpleName() + (isBlank(msg) ? "" : (": " + msg));
            return false;
        }
    }

    public static String getLastError() {
        return lastError;
    }

    private static void sendLine(BufferedWriter out, String line) throws IOException {
        out.write(line);
        out.write("\r\n");
        out.flush();
    }

    private static void expectReplyStartsWith(BufferedReader in, String expectedCode) throws IOException {
        String line = in.readLine();
        if (line == null) throw new IOException("SMTP closed connection");
        String first = line;

        while (line.length() >= 4 && line.charAt(3) == '-') {
            line = in.readLine();
            if (line == null) break;
        }

        if (!first.startsWith(expectedCode)) {
            throw new IOException("Unexpected SMTP reply: " + first);
        }
    }

    private static String readSetting(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (!isBlank(sys)) return sys.trim();
        String env = System.getenv(key);
        if (!isBlank(env)) return env.trim();
        String fileValue = readSettingFromFile(key);
        if (!isBlank(fileValue)) return fileValue.trim();
        return defaultValue;
    }

    private static String readSettingFromFile(String key) {
        loadFileSettingsOnce();
        if (fileSettings == null) return null;
        return fileSettings.getProperty(key);
    }

    private static synchronized void loadFileSettingsOnce() {
        if (fileSettingsLoaded) return;
        fileSettingsLoaded = true;

        Properties loaded = new Properties();
        Path[] candidates = new Path[] {
            Path.of(System.getProperty("user.dir"), "smtp.properties"),
            Path.of(System.getProperty("user.dir"), "config", "smtp.properties"),
            resolveJarSibling("smtp.properties"),
            resolveJarSibling("config" + File.separator + "smtp.properties")
        };

        for (Path path : candidates) {
            if (path == null) continue;
            if (!Files.exists(path)) continue;
            try (InputStream in = Files.newInputStream(path)) {
                loaded.load(in);
                fileSettings = loaded;
                return;
            } catch (IOException ignored) {
            }
        }
    }

    private static Path resolveJarSibling(String fileName) {
        try {
            File codeSource = new File(
                SmtpOtpSender.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );

            File baseDir = codeSource.isFile() ? codeSource.getParentFile() : codeSource;
            if (baseDir == null) return null;
            return baseDir.toPath().resolve(fileName);
        } catch (Exception ex) {
            return null;
        }
    }

    private static int parsePort(String value) {
        if (isBlank(value)) return -1;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
