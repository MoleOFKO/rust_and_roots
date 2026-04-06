package controller;

import db.dbcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import util.AppDialogs;
import util.SmtpOtpSender;

public class OTPController implements Initializable {
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

    private Connection con;
    private String pendingOtpCode;
    private LocalDateTime pendingOtpExpiresAt;
    private String verifiedEmail;

    @FXML
    private Group world;
    @FXML
    private Pane otppane;
    @FXML
    private TextField txtenteremail;
    @FXML
    private Button btnsentotp;
    @FXML
    private TextField txtenterotp;
    @FXML
    private Button btnotpconfirm;
    @FXML
    private Hyperlink hpresendcode;
    @FXML
    private Pane newpasswordpane;
    @FXML
    private PasswordField txtenternewpassword;
    @FXML
    private PasswordField txtconfirmpassword;
    @FXML
    private Button btnnewpasswordconfirm;
    @FXML
    private FontAwesomeIcon openpassword;
    @FXML
    private FontAwesomeIcon closedpassword;
    @FXML
    private TextField txtconfirmpasswordvisible;
    @FXML
    private TextField txtenternewpasswordvisible;
    @FXML
    private FontAwesomeIcon openpassword1;
    @FXML
    private FontAwesomeIcon closedpassword1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbcon db = new dbcon();
        try {
            con = db.getConnection();
        } catch (ClassNotFoundException ex) {
            AppDialogs.showMessageDialog(null, "Database driver not found.");
        }

        otppane.setVisible(true);
        newpasswordpane.setVisible(false);
        openpassword.setVisible(true);
        closedpassword.setVisible(false);
        openpassword1.setVisible(true);
        closedpassword1.setVisible(false);
        txtconfirmpasswordvisible.textProperty().bindBidirectional(txtconfirmpassword.textProperty());
        txtenternewpasswordvisible.textProperty().bindBidirectional(txtenternewpassword.textProperty());
        applyConfirmPasswordMask(true);
        applyNewPasswordMask(true);
    }

    @FXML
    private void handlesentotpaction(ActionEvent event) {
        sendOtp();
    }

    @FXML
    private void handleotpconfirmaction(ActionEvent event) {
        String email = safeTrim(txtenteremail.getText());
        String enteredOtp = safeTrim(txtenterotp.getText());

        if (email.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter your email.");
            return;
        }
        if (enteredOtp.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter the OTP code.");
            return;
        }
        if (pendingOtpCode == null || pendingOtpExpiresAt == null || !email.equalsIgnoreCase(verifiedEmail)) {
            AppDialogs.showMessageDialog(null, "No OTP found for this email. Please send code first.");
            return;
        }
        if (LocalDateTime.now().isAfter(pendingOtpExpiresAt)) {
            clearOtpState();
            AppDialogs.showMessageDialog(null, "OTP has expired. Please resend code.");
            return;
        }
        if (!pendingOtpCode.equals(enteredOtp)) {
            AppDialogs.showMessageDialog(null, "Incorrect OTP code.");
            return;
        }

        otppane.setVisible(false);
        newpasswordpane.setVisible(true);
        txtenternewpassword.setText("");
        txtconfirmpassword.setText("");
        AppDialogs.showMessageDialog(null, "OTP verified. Please enter your new password.");
    }

    @FXML
    private void handleresendcodeaction(ActionEvent event) {
        sendOtp();
    }

    @FXML
    private void handlenewpasswordconfirmaction(ActionEvent event) {
        String newPassword = txtenternewpassword.getText();
        String confirmPassword = txtconfirmpassword.getText();

        if (verifiedEmail == null || verifiedEmail.isBlank()) {
            AppDialogs.showMessageDialog(null, "Session expired. Please restart the forgot password flow.");
            otppane.setVisible(true);
            newpasswordpane.setVisible(false);
            return;
        }
        if (safeTrim(newPassword).isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter your new password.");
            return;
        }
        if (safeTrim(confirmPassword).isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please confirm your new password.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            AppDialogs.showMessageDialog(null, "Passwords do not match.");
            return;
        }
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            AppDialogs.showMessageDialog(
                null,
                "Password must contain:\n"
                + "- At least 8 characters\n"
                + "- 1 uppercase letter\n"
                + "- 1 lowercase letter\n"
                + "- 1 number"
            );
            return;
        }

        if (!updatePasswordByEmail(verifiedEmail, newPassword)) {
            return;
        }

        AppDialogs.showMessageDialog(null, "Password updated successfully. Please login.");
        clearOtpState();
        try {
            util.SceneManager.switchScene("/view/login.fxml");
        } catch (Exception ex) {
            AppDialogs.showMessageDialog(null, "Password updated, but failed to return to login screen.");
        }
    }

    @FXML
    private void handleopenpasswordaction(MouseEvent event) {
        if (event.getSource() == openpassword) {
            applyConfirmPasswordMask(false);
        } else if (event.getSource() == openpassword1) {
            applyNewPasswordMask(false);
        }
    }

    @FXML
    private void handlecolsedpasswordaction(MouseEvent event) {
        if (event.getSource() == closedpassword) {
            applyConfirmPasswordMask(true);
        } else if (event.getSource() == closedpassword1) {
            applyNewPasswordMask(true);
        }
    }

    private void sendOtp() {
        String email = safeTrim(txtenteremail.getText());
        if (email.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please enter your email.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            AppDialogs.showMessageDialog(null, "Please enter a valid email address.");
            return;
        }
        if (!emailExists(email)) {
            AppDialogs.showMessageDialog(null, "No account found with this email.");
            return;
        }

        String otp = generateOtpCode(OTP_LENGTH);
        if (!SmtpOtpSender.sendVerificationCode(email, otp)) {
            String smtpReason = SmtpOtpSender.getLastError();
            AppDialogs.showMessageDialog(
                null,
                "Unable to send verification email.\n"
                + "Please configure SMTP and try again.\n\n"
                + "Reason: " + (smtpReason == null || smtpReason.isBlank() ? "Unknown SMTP error" : smtpReason)
            );
            return;
        }

        pendingOtpCode = otp;
        pendingOtpExpiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        verifiedEmail = email;
        txtenterotp.setText("");
        AppDialogs.showMessageDialog(null, "OTP sent to " + email + ". It expires in 10 minutes.");
    }

    private boolean emailExists(String email) {
        if (con == null) {
            AppDialogs.showMessageDialog(null, "Database connection is unavailable.");
            return false;
        }
        String sql = "SELECT 1 FROM accounts WHERE email=? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            AppDialogs.showMessageDialog(null, "Failed to check email.\n" + ex.getMessage());
            return false;
        }
    }

    private boolean updatePasswordByEmail(String email, String newPassword) {
        if (con == null) {
            AppDialogs.showMessageDialog(null, "Database connection is unavailable.");
            return false;
        }
        String sql = "UPDATE accounts SET passwords=? WHERE email=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            int updated = ps.executeUpdate();
            if (updated <= 0) {
                AppDialogs.showMessageDialog(null, "No account was updated for this email.");
                return false;
            }
            return true;
        } catch (SQLException ex) {
            AppDialogs.showMessageDialog(null, "Failed to update password.\n" + ex.getMessage());
            return false;
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

    private void clearOtpState() {
        pendingOtpCode = null;
        pendingOtpExpiresAt = null;
        verifiedEmail = null;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private void applyConfirmPasswordMask(boolean masked) {
        txtconfirmpassword.setVisible(masked);
        txtconfirmpassword.setManaged(masked);
        txtconfirmpasswordvisible.setVisible(!masked);
        txtconfirmpasswordvisible.setManaged(!masked);
        openpassword.setVisible(masked);
        closedpassword.setVisible(!masked);
    }

    private void applyNewPasswordMask(boolean masked) {
        txtenternewpassword.setVisible(masked);
        txtenternewpassword.setManaged(masked);
        txtenternewpasswordvisible.setVisible(!masked);
        txtenternewpasswordvisible.setManaged(!masked);
        openpassword1.setVisible(masked);
        closedpassword1.setVisible(!masked);
    }
}
