/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.UiSqlService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import game.GameState;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ResourceBundle;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import util.AppDialogs;
import model.AccMg;
import static rust_and_roots.Rust_and_roots.stage;
import util.SmtpOtpSender;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author larry
 */
public class LoginController implements Initializable {
    
    @FXML
    private Group login_pane;
    @FXML
    private Button btnlogin;
    @FXML
    private TextField txtlname;
    @FXML
    private PasswordField txtlpassword;
    @FXML
    private Hyperlink sihl;
    @FXML
    private Group sign_inpane;
    @FXML
    private Button btnsignin;
    @FXML
    private TextField txtsname;
    @FXML
    private PasswordField txtspassword;
    @FXML
    private Hyperlink lghl;
    @FXML
    private PasswordField txtscpassword;
    @FXML
    private Button btnclose;
    @FXML
    private TextField txtsgmail;
    @FXML 
    private StackPane view;
    @FXML 
    private Group uiRoot;
    
    ObservableList<AccMg> emailList;
    public static String txtname;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int OTP_RESEND_COOLDOWN_SECONDS = 180;
    private static final int MAX_PASSWORD_RETRY_COUNT = 3;
    private static final long INITIAL_PASSWORD_LOCKOUT_SECONDS = 180;
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
    private final Map<String, LoginRetryState> loginRetryStates = new HashMap<>();
    private String pendingOtpCode;
    private LocalDateTime pendingOtpExpiresAt;
    private String pendingOtpEmail;
    private String pendingOtpUsername;
    private String pendingOtpPassword;
    private LocalDateTime otpResendAvailableAt;
    private Timeline otpResendCooldownTimeline;
    private LoginRetryState activeLoginLockoutState;
    private Timeline loginLockoutCountdownTimeline;
    @FXML
    private TextField otpcode;
    @FXML
    private Button btnotpsend;
    @FXML
    private Label lblOtpCooldown;
    @FXML
    private Hyperlink forgotpwd;
    @FXML
    private FontAwesomeIcon openlpassword;
    @FXML
    private FontAwesomeIcon closedlpassword;
    @FXML
    private TextField txtlpasswordvisible;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);

        if (txtlpasswordvisible != null && txtlpassword != null) {
            txtlpasswordvisible.textProperty().bindBidirectional(txtlpassword.textProperty());
            applyLoginPasswordMask(true);
        }

        if (btnotpsend != null) {
            btnotpsend.setTooltip(new Tooltip("Send verification code"));
            updateSignupOtpSendAvailability();
        }
    }   
 
    @FXML
    private void handlebtnloginaction(ActionEvent event) throws SQLException, IOException {
        String loginInput = txtlname.getText().trim();
        String password = txtlpassword.getText();

        if (loginInput.isEmpty() && password.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter your username and password.");
            return;
        }
        if (loginInput.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter your username or email.");
            return;
        }
        if (password.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter a password.");
            return;
        }

        String retryKey = normalizeLoginRetryKey(loginInput);
        LoginRetryState retryState = loginRetryStates.computeIfAbsent(retryKey, key -> new LoginRetryState());
        if (retryState.isLocked()) {
            startLoginPasswordLockoutCountdown(retryState);
            AppDialogs.showMessageDialog(null, "Please try again later.");
            return;
        }

        UiSqlService.AuthRow auth;
        try {
            auth = UiSqlService.fetchAuthRow(loginInput);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        }

        if (auth == null) {
            AppDialogs.showMessageDialog(null, "No account found with that username.");
            return;
        }

        String dbPassword = auth.password();
        if (dbPassword == null || !dbPassword.equals(password)) {
            retryState.recordFailedPassword();
            if (retryState.isLocked()) {
                startLoginPasswordLockoutCountdown(retryState);
            }
            AppDialogs.showMessageDialog(null, "Password is incorrect.");
            return;
        }

        loginRetryStates.remove(retryKey);
        stopLoginPasswordLockoutCountdown();

        txtname = auth.accountName();
        int accountId = auth.accountId();
        String dbstatus = auth.statusName();
        String temp = auth.roleName();
        if (!"active".equalsIgnoreCase(dbstatus)) {
            AppDialogs.showMessageDialog(null, "This account is banned.");
            return;
        }
        if (temp == null) {
            AppDialogs.showMessageDialog(null, "Unable to determine account role.");
            return;
        }
        switch (temp) {
            case "admin" -> {
                util.SceneManager.switchScene("/view/account_view.fxml");
            }
            case "user" -> {
                GameState gs = GameState.get();
                gs.setCurrentAccountId(accountId);
                gs.setCurrentCharacterId(-1);
                gs.setCurrentSaveId(-1);
                gs.setCurrentPlayerName(txtname);

                Integer characterId;
                try {
                    characterId = UiSqlService.findFirstCharacterIdForAccount(accountId);
                } catch (ClassNotFoundException ex) {
                    throw new SQLException("Database driver not found.", ex);
                }

                if (characterId != null && characterId > 0) {
                    gs.setCurrentCharacterId(characterId);
                    try {
                        String playerName = UiSqlService.findPlayerNameForCharacter(characterId);
                        if (playerName != null && !playerName.isBlank()) {
                            gs.setCurrentPlayerName(playerName);
                        }

                        Integer saveId = UiSqlService.findFirstSaveIdForCharacter(characterId);
                        gs.setCurrentSaveId(saveId == null ? -1 : saveId);
                    } catch (ClassNotFoundException ex) {
                        throw new SQLException("Database driver not found.", ex);
                    }
                }

                util.SceneManager.switchScene("/view/main_menu.fxml");
            }
            case "owner" -> {
                util.SceneManager.switchScene("/view/account_view.fxml");
            }
            default -> AppDialogs.showMessageDialog(null, "Unable to determine account role.");
        }
    }
        
    

    @FXML
    private void handlebtnsigninaction(ActionEvent event) throws SQLException, IOException {
        String email = txtsgmail.getText().trim();
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        String username = txtsname.getText().trim();
        String pwd = txtspassword.getText();
        String confirmPwd = txtscpassword.getText();
        String enteredOtp = otpcode == null ? "" : otpcode.getText().trim();

        if (email.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter your email.");
            return;
        }
        if (username.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter a username.");
            return;
        }
        if (pwd.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter a password.");
            return;
        }
        if (confirmPwd.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please confirm your password.");
            return;
        }
        if (!email.matches(emailRegex)) {
            AppDialogs.showMessageDialog(null, "Please enter a valid email address.");
            return;
        }
        if (!emailDomainHasMxRecord(email)) {
            AppDialogs.showMessageDialog(null, "This email domain has no valid MX record.");
            return;
        }
        if (!pwd.matches(PASSWORD_REGEX)) {
            AppDialogs.showMessageDialog(null, """
                                                Password must contain:
                                                - At least 8 characters
                                                - 1 uppercase letter
                                                - 1 lowercase letter
                                                - 1 number
                                                - 1 special character""");
            return;
        }
        if (!pwd.equals(confirmPwd)) {
            AppDialogs.showMessageDialog(null, "Passwords do not match.");
            return;
        }

        if (emailExists(email)) {
            AppDialogs.showMessageDialog(null, "This email is already registered.");
            return;
        }
        if (usernameExists(username)) {
            AppDialogs.showMessageDialog(null, "This username is already taken.");
            return;
        }

        if (enteredOtp.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter the OTP code. Press Send Code first.");
            return;
        }

        if (!isPendingOtpForCurrentInput(email, username, pwd)) {
            AppDialogs.showMessageDialog(
                null,
                "No active verification code for this sign-up data.\n"
                + "Press Send Code to receive a new OTP."
            );
            return;
        }
        if (pendingOtpExpiresAt == null || LocalDateTime.now().isAfter(pendingOtpExpiresAt)) {
            clearPendingOtpState();
            AppDialogs.showMessageDialog(null, "Verification code expired. Press Send Code to request a new one.");
            return;
        }
        if (!pendingOtpCode.equals(enteredOtp)) {
            AppDialogs.showMessageDialog(null, "Incorrect verification code.");
            return;
        }

        if (emailExists(email)) {
            AppDialogs.showMessageDialog(null, "This email is already registered.");
            return;
        }
        if (usernameExists(username)) {
            AppDialogs.showMessageDialog(null, "This username is already taken.");
            return;
        }

        int accountId = createAccount(email, username, pwd);
        if (accountId <= 0) {
            AppDialogs.showMessageDialog(null, "Failed to create account.");
            return;
        }
        clearPendingOtpState();

        GameState gs = GameState.get();
        gs.setCurrentAccountId(accountId);
        gs.setCurrentCharacterId(-1);
        gs.setCurrentSaveId(-1);
        gs.setCurrentPlayerName(username);

        boolean confirmationSent = SmtpOtpSender.sendAccountCreatedSuccess(email);
        if (confirmationSent) {
            AppDialogs.showMessageDialog(null, "Account created successfully! Rust&Roots");
        } else {
            String smtpReason = SmtpOtpSender.getLastError();
            AppDialogs.showMessageDialog(
                null,
                "Account created successfully!\n"
                + "Confirmation email could not be sent.\n"
                + "Reason: " + (smtpReason == null || smtpReason.isBlank() ? "Unknown SMTP error" : smtpReason)
            );
        }

        resetSignupForm();

        util.SceneManager.switchScene("/view/main_menu.fxml");
    }

    @FXML
    private void handlelghlaction(ActionEvent event) {
        clearPendingOtpState();
        if (otpcode != null) otpcode.setText("");
        login_pane.setVisible(true);
        sign_inpane.setVisible(false);
    }

    @FXML
    private void handlesihlaction(ActionEvent event) {
        clearPendingOtpState();
        if (otpcode != null) otpcode.setText("");
        login_pane.setVisible(false);
        sign_inpane.setVisible(true);
    }

    @FXML
    private void handlebtncloseaction(ActionEvent event) {
        Platform.exit();
    }
    
    
    private boolean isPendingOtpForCurrentInput(String email, String username, String password) {
        if (pendingOtpCode == null || pendingOtpCode.isBlank()) return false;
        if (pendingOtpEmail == null || pendingOtpUsername == null || pendingOtpPassword == null) return false;
        return pendingOtpEmail.equals(email)
            && pendingOtpUsername.equals(username)
            && pendingOtpPassword.equals(password);
    }

    private String normalizeLoginRetryKey(String loginInput) {
        return loginInput == null ? "" : loginInput.trim().toLowerCase();
    }

    private static final class LoginRetryState {
        private int failedPasswordAttempts = 0;
        private long nextLockoutSeconds = INITIAL_PASSWORD_LOCKOUT_SECONDS;
        private LocalDateTime lockedUntil;

        boolean isLocked() {
            if (lockedUntil == null) {
                return false;
            }
            if (LocalDateTime.now().isBefore(lockedUntil)) {
                return true;
            }
            lockedUntil = null;
            failedPasswordAttempts = 0;
            return false;
        }

        void recordFailedPassword() {
            if (isLocked()) {
                return;
            }

            failedPasswordAttempts++;
            if (failedPasswordAttempts >= MAX_PASSWORD_RETRY_COUNT) {
                lockedUntil = LocalDateTime.now().plusSeconds(nextLockoutSeconds);
                nextLockoutSeconds = Math.max(nextLockoutSeconds, nextLockoutSeconds * 2);
                failedPasswordAttempts = 0;
            }
        }

        long getLockoutSecondsRemaining() {
            if (lockedUntil == null) {
                return 0;
            }
            long remainingSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), lockedUntil);
            return Math.max(0, remainingSeconds);
        }
    }

    private void clearPendingOtpState() {
        pendingOtpCode = null;
        pendingOtpExpiresAt = null;
        pendingOtpEmail = null;
        pendingOtpUsername = null;
        pendingOtpPassword = null;
    }

    private void resetSignupForm() {
        txtsname.setText("");
        txtspassword.setText("");
        txtscpassword.setText("");
        txtsgmail.setText("");
        if (otpcode != null) otpcode.setText("");
    }

    private boolean emailExists(String email) throws SQLException {
        try {
            return UiSqlService.emailExists(email);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        }
    }

    private boolean usernameExists(String username) throws SQLException {
        try {
            return UiSqlService.usernameExists(username);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        }
    }

    private int createAccount(String email, String username, String password) throws SQLException {
        try {
            return UiSqlService.insertAccount(email, username, password, 3, 1);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        }
    }

    private String generateOtpCode(int length) {
        if (length <= 0) return "";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }

    private boolean emailDomainHasMxRecord(String email) {
        int at = email.lastIndexOf('@');
        if (at < 0 || at >= email.length() - 1) {
            return false;
        }

        String domain = email.substring(at + 1);
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

        try {
            DirContext context = new InitialDirContext(env);
            Attributes attrs = context.getAttributes(domain, new String[] {"MX"});
            Attribute mxAttr = attrs.get("MX");
            return mxAttr != null && mxAttr.size() > 0;
        } catch (NamingException ex) {
            return false;
        }
    }

    @FXML
    private void handlebtnotpsendaction(ActionEvent event) throws SQLException {
        String email = txtsgmail.getText().trim();
        String username = txtsname.getText().trim();
        String pwd = txtspassword.getText();
        String confirmPwd = txtscpassword.getText();
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (email.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter your email.");
            return;
        }
        if (username.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter a username.");
            return;
        }
        if (pwd.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter a password.");
            return;
        }
        if (confirmPwd.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please confirm your password.");
            return;
        }
        if (!email.matches(emailRegex)) {
            AppDialogs.showMessageDialog(null, "Please enter a valid email address.");
            return;
        }
        if (!emailDomainHasMxRecord(email)) {
            AppDialogs.showMessageDialog(null, "This email domain has no valid MX record.");
            return;
        }
        if (!pwd.matches(PASSWORD_REGEX)) {
            AppDialogs.showMessageDialog(null, """
                                                Password must contain:
                                                - At least 8 characters
                                                - 1 uppercase letter
                                                - 1 lowercase letter
                                                - 1 number
                                                - 1 special character""");
            return;
        }
        if (!pwd.equals(confirmPwd)) {
            AppDialogs.showMessageDialog(null, "Passwords do not match.");
            return;
        }
        if (emailExists(email)) {
            AppDialogs.showMessageDialog(null, "This email is already registered.");
            return;
        }
        if (usernameExists(username)) {
            AppDialogs.showMessageDialog(null, "This username is already taken.");
            return;
        }

        pendingOtpCode = generateOtpCode(OTP_LENGTH);
        pendingOtpExpiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        pendingOtpEmail = email;
        pendingOtpUsername = username;
        pendingOtpPassword = pwd;

        if (!SmtpOtpSender.sendVerificationCode(email, pendingOtpCode)) {
            clearPendingOtpState();
            String smtpReason = SmtpOtpSender.getLastError();
            AppDialogs.showMessageDialog(
                null,
                "Unable to send verification email.\n"
                + "Please configure SMTP and try again.\n\n"
                + "Reason: " + (smtpReason == null || smtpReason.isBlank() ? "Unknown SMTP error" : smtpReason) + "\n\n"
                + "Required settings:\n"
                + "RNR_SMTP_PASS\n"
                + "Optional overrides: RNR_SMTP_HOST, RNR_SMTP_PORT, RNR_SMTP_USER, RNR_SMTP_FROM"
            );
            return;
        }

        if (otpcode != null) otpcode.setText("");
        AppDialogs.showMessageDialog(
            null,
            "Verification code sent to " + email + ".\nEnter the code and press Sign Up."
        );
        startSignupOtpCooldown();
    }

    @FXML
    private void handleforgetpwdaction(ActionEvent event) {
        util.SceneManager.switchScene("/view/OTP.fxml");
    }

    @FXML
    private void handleopenpasswordaction(MouseEvent event) {
        if (isLoginPasswordLockoutActive()) return;
        applyLoginPasswordMask(false);
    }

    @FXML
    private void handlecolsedpasswordaction(MouseEvent event) {
        if (isLoginPasswordLockoutActive()) return;
        applyLoginPasswordMask(true);
    }

    private void applyLoginPasswordMask(boolean masked) {
        if (isLoginPasswordLockoutActive()) {
            masked = false;
        }
        txtlpassword.setVisible(masked);
        txtlpassword.setManaged(masked);
        txtlpasswordvisible.setVisible(!masked);
        txtlpasswordvisible.setManaged(!masked);
        openlpassword.setVisible(masked);
        closedlpassword.setVisible(!masked);
    }

    private void startLoginPasswordLockoutCountdown(LoginRetryState retryState) {
        activeLoginLockoutState = retryState;
        ensureLoginLockoutCountdownTimeline();
        updateLoginPasswordLockoutDisplay();
        if (loginLockoutCountdownTimeline != null) {
            loginLockoutCountdownTimeline.playFromStart();
        }
    }

    private void ensureLoginLockoutCountdownTimeline() {
        if (loginLockoutCountdownTimeline != null) {
            return;
        }
        loginLockoutCountdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateLoginPasswordLockoutDisplay()));
        loginLockoutCountdownTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void updateLoginPasswordLockoutDisplay() {
        if (activeLoginLockoutState == null || !activeLoginLockoutState.isLocked()) {
            stopLoginPasswordLockoutCountdown();
            return;
        }

        long remainingSeconds = activeLoginLockoutState.getLockoutSecondsRemaining();
        String cooldownText = "Try again in " + formatCooldown(remainingSeconds);
        applyLoginPasswordMask(false);
        if (txtlpassword != null) {
            txtlpassword.setText(cooldownText);
            txtlpassword.setDisable(true);
        }
        if (txtlpasswordvisible != null) {
            txtlpasswordvisible.setText(cooldownText);
            txtlpasswordvisible.setDisable(true);
        }
        if (openlpassword != null) openlpassword.setDisable(true);
        if (closedlpassword != null) closedlpassword.setDisable(true);
    }

    private void stopLoginPasswordLockoutCountdown() {
        if (loginLockoutCountdownTimeline != null) {
            loginLockoutCountdownTimeline.stop();
        }
        activeLoginLockoutState = null;
        if (txtlpassword != null) {
            txtlpassword.setDisable(false);
            txtlpassword.setText("");
        }
        if (txtlpasswordvisible != null) {
            txtlpasswordvisible.setDisable(false);
            txtlpasswordvisible.setText("");
        }
        if (openlpassword != null) openlpassword.setDisable(false);
        if (closedlpassword != null) closedlpassword.setDisable(false);
        applyLoginPasswordMask(true);
    }

    private boolean isLoginPasswordLockoutActive() {
        return activeLoginLockoutState != null && activeLoginLockoutState.isLocked();
    }

    private void startSignupOtpCooldown() {
        otpResendAvailableAt = LocalDateTime.now().plusSeconds(OTP_RESEND_COOLDOWN_SECONDS);
        ensureSignupOtpCooldownTimeline();
        updateSignupOtpSendAvailability();
        if (otpResendCooldownTimeline != null) {
            otpResendCooldownTimeline.playFromStart();
        }
    }

    private void ensureSignupOtpCooldownTimeline() {
        if (otpResendCooldownTimeline != null) {
            return;
        }
        otpResendCooldownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateSignupOtpSendAvailability()));
        otpResendCooldownTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void updateSignupOtpSendAvailability() {
        if (btnotpsend == null && lblOtpCooldown == null) {
            return;
        }

        long remainingSeconds = getSignupOtpCooldownSecondsRemaining();
        boolean cooldownActive = remainingSeconds > 0;
        String cooldownText = cooldownActive ? formatCooldown(remainingSeconds) : "";

        if (btnotpsend != null) {
            btnotpsend.setDisable(cooldownActive);
            btnotpsend.setVisible(!cooldownActive);
            btnotpsend.setManaged(!cooldownActive);
        }

        if (lblOtpCooldown != null) {
            lblOtpCooldown.setText(cooldownText);
            lblOtpCooldown.setVisible(cooldownActive);
            lblOtpCooldown.setManaged(cooldownActive);
        }

        if (btnotpsend != null) {
            Tooltip tooltip = btnotpsend.getTooltip();
            if (tooltip == null) {
                tooltip = new Tooltip();
                btnotpsend.setTooltip(tooltip);
            }
            tooltip.setText(cooldownActive
                ? "You can send OTP again in " + cooldownText
                : "Send verification code");
        }

        if (!cooldownActive && otpResendCooldownTimeline != null) {
            otpResendCooldownTimeline.stop();
        }
    }

    private long getSignupOtpCooldownSecondsRemaining() {
        if (otpResendAvailableAt == null) {
            return 0;
        }
        long remainingSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), otpResendAvailableAt);
        if (remainingSeconds <= 0) {
            otpResendAvailableAt = null;
            return 0;
        }
        return remainingSeconds;
    }

    private String formatCooldown(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
