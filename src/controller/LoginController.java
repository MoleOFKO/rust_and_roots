/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.dbcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import game.GameState;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Hashtable;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
    Connection con;
    ResultSet rs;
    PreparedStatement pst;
    
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
    private String pendingOtpCode;
    private LocalDateTime pendingOtpExpiresAt;
    private String pendingOtpEmail;
    private String pendingOtpUsername;
    private String pendingOtpPassword;
    @FXML
    private TextField otpcode;
    @FXML
    private Button btnotpsend;
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
        // TODO
        dbcon db=new dbcon();
        try{
            con=db.getConnection();
        }catch(ClassNotFoundException ex){
            System.out.println("Connection not found.....");
            ex.printStackTrace();
        }
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);

        if (txtlpasswordvisible != null && txtlpassword != null) {
            txtlpasswordvisible.textProperty().bindBidirectional(txtlpassword.textProperty());
            applyLoginPasswordMask(true);
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
        String sql = """
    SELECT a.account_id, a.account_name, a.passwords,
           r.role_name, s.status_name
    FROM accounts a
    JOIN user_roles r ON a.role_id = r.role_id
    JOIN account_statuses s ON a.status_id = s.status_id
    WHERE a.account_name = ? OR a.email = ?
    LIMIT 1
    """;
               
        pst = con.prepareStatement(sql);
        pst.setString(1, loginInput);
        pst.setString(2, loginInput);
        rs = pst.executeQuery();
        if (!rs.next()) {
            AppDialogs.showMessageDialog(null, "No account found with that username.");
            return;
        }
        String dbPassword = rs.getString("passwords");
        if (!dbPassword.equals(password)) {
            AppDialogs.showMessageDialog(null, "Incorrect password.");
            return;
        }
        txtname = rs.getString("account_name");
        int accountId = rs.getInt("account_id");
        String dbstatus = rs.getString("status_name");
        String temp = rs.getString("role_name");
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

                String charSql = "SELECT character_id, player_name FROM characters WHERE account_id=? ORDER BY character_id LIMIT 1";
                try (PreparedStatement charPst = con.prepareStatement(charSql)) {
                    charPst.setInt(1, accountId);

                    try (ResultSet charRs = charPst.executeQuery()) {
                        if (charRs.next()) {
                            int characterId = charRs.getInt("character_id");
                            String playerName = charRs.getString("player_name");

                            gs.setCurrentCharacterId(characterId);
                            gs.setCurrentPlayerName(playerName);

                            String saveSql = "SELECT saved_id FROM save_game WHERE character_id=? ORDER BY saved_id LIMIT 1";
                            try (PreparedStatement savePst = con.prepareStatement(saveSql)) {
                                savePst.setInt(1, characterId);
                                try (ResultSet saveRs = savePst.executeQuery()) {
                                    if (saveRs.next()) {
                                        gs.setCurrentSaveId(saveRs.getInt("saved_id"));
                                    } else {
                                        gs.setCurrentSaveId(-1);
                                    }
                                }      
                                
                            }
                        }            
                        
                    }
                    util.SceneManager.switchScene("/view/main_menu.fxml");
                }
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
        String pwdRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

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
        if (!pwd.matches(pwdRegex)) {
            AppDialogs.showMessageDialog(null, """
                                                Password must contain:
                                                - At least 8 characters
                                                - 1 uppercase letter
                                                - 1 lowercase letter
                                                - 1 number""");
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

        AppDialogs.showMessageDialog(null, "Account created successfully.");

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
    
    
    private int findFirstSaveIdForCharacter(int characterId) throws SQLException {
        String saveSql = "SELECT saved_id FROM save_game WHERE character_id=? ORDER BY saved_id LIMIT 1";
        try (PreparedStatement savePst = con.prepareStatement(saveSql)) {
            savePst.setInt(1, characterId);
            try (ResultSet saveRs = savePst.executeQuery()) {
                if (saveRs.next()) {
                    return saveRs.getInt("saved_id");
                }
            }
        }
        return -1;
    }

    private boolean isPendingOtpForCurrentInput(String email, String username, String password) {
        if (pendingOtpCode == null || pendingOtpCode.isBlank()) return false;
        if (pendingOtpEmail == null || pendingOtpUsername == null || pendingOtpPassword == null) return false;
        return pendingOtpEmail.equals(email)
            && pendingOtpUsername.equals(username)
            && pendingOtpPassword.equals(password);
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
        String emailExistsSql = "SELECT 1 FROM accounts WHERE email=? LIMIT 1";
        try (PreparedStatement emailPst = con.prepareStatement(emailExistsSql)) {
            emailPst.setString(1, email);
            try (ResultSet emailRs = emailPst.executeQuery()) {
                return emailRs.next();
            }
        }
    }

    private boolean usernameExists(String username) throws SQLException {
        String usernameExistsSql = "SELECT 1 FROM accounts WHERE account_name=? LIMIT 1";
        try (PreparedStatement usernamePst = con.prepareStatement(usernameExistsSql)) {
            usernamePst.setString(1, username);
            try (ResultSet usernameRs = usernamePst.executeQuery()) {
                return usernameRs.next();
            }
        }
    }

    private int createAccount(String email, String username, String password) throws SQLException {
        String sql = "INSERT INTO accounts (email, account_name, passwords, role_id, status_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement accPst = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            accPst.setString(1, email);
            accPst.setString(2, username);
            accPst.setString(3, password);
            accPst.setInt(4, 3);
            accPst.setInt(5, 1);
            accPst.executeUpdate();

            try (ResultSet accKeys = accPst.getGeneratedKeys()) {
                if (accKeys.next()) {
                    return accKeys.getInt(1);
                }
            }
        }
        return -1;
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
        String pwdRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

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
        if (!pwd.matches(pwdRegex)) {
            AppDialogs.showMessageDialog(null, """
                                                Password must contain:
                                                - At least 8 characters
                                                - 1 uppercase letter
                                                - 1 lowercase letter
                                                - 1 number""");
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
    }

    @FXML
    private void handleforgetpwdaction(ActionEvent event) {
        util.SceneManager.switchScene("/view/OTP.fxml");
    }

    @FXML
    private void handleopenpasswordaction(MouseEvent event) {
        applyLoginPasswordMask(false);
    }

    @FXML
    private void handlecolsedpasswordaction(MouseEvent event) {
        applyLoginPasswordMask(true);
    }

    private void applyLoginPasswordMask(boolean masked) {
        txtlpassword.setVisible(masked);
        txtlpassword.setManaged(masked);
        txtlpasswordvisible.setVisible(!masked);
        txtlpasswordvisible.setManaged(!masked);
        openlpassword.setVisible(masked);
        closedlpassword.setVisible(!masked);
    }
}
