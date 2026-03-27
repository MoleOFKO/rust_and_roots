/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.dbcon;
import game.GameState;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javax.swing.JOptionPane;
import model.AccMg;
import static rust_and_roots.Rust_and_roots.stage;
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
    }   
 
    @FXML
    private void handlebtnloginaction(ActionEvent event) throws SQLException, IOException {
        String username = txtlname.getText().trim();
        String password = txtlpassword.getText();

        if (username.isEmpty() && password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid input!");
            return;
        }
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter Username!");
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter password!");
            return;
        }
        String sql = "SELECT account_id, account_name, passwords, user_type, account_stats " +
                     "FROM accounts WHERE account_name = ?";
        pst = con.prepareStatement(sql);
        pst.setString(1, username);
        rs = pst.executeQuery();
        if (!rs.next()) {
            JOptionPane.showMessageDialog(null, "Username does not exist!");
            return;
        }
        String dbPassword = rs.getString("passwords");
        if (!dbPassword.equals(password)) {
            JOptionPane.showMessageDialog(null, "Password is incorrect!");
            return;
        }
        txtname = rs.getString("account_name");
        int accountId = rs.getInt("account_id");
        String dbstatus = rs.getString("account_stats");
        String temp = rs.getString("user_type");
        if (!"active".equalsIgnoreCase(dbstatus)) {
            JOptionPane.showMessageDialog(null, "Account is Banned");
            return;
        }
        if (temp == null) {
            JOptionPane.showMessageDialog(null, "Invalid input!");
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
                }

                util.SceneManager.switchScene("/view/main_menu.fxml");
            }
            case "owner" -> {
                util.SceneManager.switchScene("/view/account_view.fxml");
            }
            default -> JOptionPane.showMessageDialog(null, "Invalid input!");
        }
    }
        
    

    @FXML
    private void handlebtnsigninaction(ActionEvent event) throws SQLException, IOException {

        String email = txtsgmail.getText();
        String gmailRegex = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
        String pwd = txtspassword.getText();
        String pwdRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

        if (txtsgmail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter email!");
            return;
        }
        if (txtsname.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter Username");
            return;
        }
        if (pwd.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter password!");
            return;
        }
        if (txtscpassword.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter confirm password!");
            return;
        }
        if (!email.matches(gmailRegex)) {
            JOptionPane.showMessageDialog(null, "Gamil Not Regonized");
            return;
        }
        if (!pwd.matches(pwdRegex)) {
            JOptionPane.showMessageDialog(null, """
                                                Password must contain:
                                                - At least 8 characters
                                                - 1 uppercase letter
                                                - 1 lowercase letter
                                                - 1 number""");
            return;
        }
        if (!txtspassword.getText().equals(txtscpassword.getText())) {
            JOptionPane.showMessageDialog(null, "Password don't match!");
            return;
        }

        String sql1 = "SELECT account_name FROM accounts WHERE email=? or account_name=?";
        pst = con.prepareStatement(sql1);
        pst.setString(1, txtsgmail.getText().trim());
        pst.setString(2, txtsname.getText());
        rs = pst.executeQuery();

        if (rs.next()) {
            JOptionPane.showMessageDialog(null, "User Already exist");
        } else {

            String sql = "INSERT INTO accounts (email,account_name,passwords,account_stats,user_type) VALUES (?,?,?,'active','user')";
            try (PreparedStatement accPst = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                accPst.setString(1, txtsgmail.getText().trim());
                accPst.setString(2, txtsname.getText().trim());
                accPst.setString(3, txtspassword.getText());
                accPst.executeUpdate();

                int accountId;
                try (ResultSet accKeys = accPst.getGeneratedKeys()) {
                    if (!accKeys.next()) {
                        JOptionPane.showMessageDialog(null, "Failed to create account.");
                        return;
                    }
                    accountId = accKeys.getInt(1);
                }

                GameState gs = GameState.get();
                gs.setCurrentAccountId(accountId);
                gs.setCurrentCharacterId(-1);
                gs.setCurrentSaveId(-1);
                gs.setCurrentPlayerName(txtsname.getText().trim());
            }

            JOptionPane.showMessageDialog(null, "Account created successfully");

            txtsname.setText("");
            txtspassword.setText("");
            txtscpassword.setText("");
            txtsgmail.setText("");

            util.SceneManager.switchScene("/view/main_menu.fxml");
        }
    }

    @FXML
    private void handlelghlaction(ActionEvent event) {
        login_pane.setVisible(true);
        sign_inpane.setVisible(false);
    }

    @FXML
    private void handlesihlaction(ActionEvent event) {
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
}
