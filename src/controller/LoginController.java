/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.dbcon;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javax.swing.JOptionPane;
import model.AccMg;
import static rust_and_roots.Rust_and_roots.stage;

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
    private Pane login_pane;
    @FXML
    private Button btnlogin;
    @FXML
    private TextField txtlname;
    @FXML
    private PasswordField txtlpassword;
    @FXML
    private Hyperlink sihl;
    @FXML
    private Pane sign_inpane;
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
        }
    }    

    @FXML
    private void handlebtnloginaction(ActionEvent event) throws SQLException, IOException {
       emailList = FXCollections.observableArrayList();
       String sql1 = "SELECT user_type FROM accounts WHERE account_name=? AND passwords=?";
                pst = con.prepareStatement(sql1);
                pst.setString(1, txtlname.getText());
                pst.setString(2, txtlpassword.getText());

                rs = pst.executeQuery();
       
                if (rs.next()) {
                    // login success
                    
                    String sql="select account_name, passwords, user_type,account_stats from accounts where account_name=? and passwords=?";
                    pst=con.prepareStatement(sql);
                    pst.setString(1, txtlname.getText());
                    pst.setString(2, txtlpassword.getText());
                    rs=pst.executeQuery();
                    
                    while(rs.next()){
                        txtname = rs.getString("account_name");
                        String status = "active";
                        String dbstatus = rs.getString("account_stats");
                        
                        if(!dbstatus.equals(status)){
                        JOptionPane.showMessageDialog(null, "Account is Banned");
                        return;
                        }   
                        
                        String temp = rs.getString("user_type");
                        if(null==temp)JOptionPane.showMessageDialog(null, "Invalid!");else switch (temp) {
                            case "admin" ->                         {
                                    Parent root = FXMLLoader.load(getClass().getResource("/view/account_view.fxml"));
                                    Scene scene = new Scene(root);
                                    stage.setScene(scene);
                                    stage.show();
                                }
                            case "user" ->                         {
                                    Parent root = FXMLLoader.load(getClass().getResource("/view/main_menu.fxml"));
                                    Scene scene = new Scene(root);
                                    stage.setScene(scene);
                                    stage.show();
                                    
                                }
                            case "owner" -> {
                                Parent root = FXMLLoader.load(getClass().getResource("/view/account_view.fxml"));
                                    Scene scene = new Scene(root);
                                    stage.setScene(scene);
                                    stage.show();
                            }
                            default -> JOptionPane.showMessageDialog(null, "Invalid!");
                        }

                    }
                    
                    
                        } else {
                            if( txtlname.getText().isEmpty())
                                JOptionPane.showMessageDialog(null, "Enter Email!");
                            else if( txtlpassword.getText().isEmpty())
                                JOptionPane.showMessageDialog(null, "Enter password!");
                            else{
                                JOptionPane.showMessageDialog(null, "User Does Not Exist");
                            }
                        }
            }
        
    

    @FXML
    private void handlebtnsigninaction(ActionEvent event) throws SQLException, IOException {
        
        

        
            String email = txtsgmail.getText();
            String gmailRegex = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
            String pwd = txtspassword.getText();
            String pwdRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
            
            if( txtsgmail.getText().isEmpty()){
                JOptionPane.showMessageDialog(null, "Enter email!");
                return;
            }
            if (txtsname.getText().isEmpty())  {
                JOptionPane.showMessageDialog(null, "Enter Username");
                return;
            }
            if(pwd.isEmpty()){
                JOptionPane.showMessageDialog(null, "Enter password!");
                return;
            }
            if(txtscpassword.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Enter confirm password!");
                    return;
            }
            if(!email.matches(gmailRegex)){
                    JOptionPane.showMessageDialog(null, "Gamil Not Regonized");
                    return;
                }  
            if(!pwd.matches(pwdRegex)){
                JOptionPane.showMessageDialog(null, """
                                                    Password must contain:
                                                    - At least 8 characters
                                                    - 1 uppercase letter
                                                    - 1 lowercase letter
                                                    - 1 number""");
                    return;
            }
            if(!txtspassword.getText().equals(txtscpassword.getText())){
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
                
            }else{
                     
            String sql="Insert into accounts (email,account_name,passwords,account_stats,user_type) values (?,?,?,'active','user')";
            pst=con.prepareStatement(sql);
            pst.setString(1, txtsgmail.getText());
            pst.setString(2, txtsname.getText());
            pst.setString(3, txtspassword.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Account created successfully");
            
            txtsname.setText("");
            txtspassword.setText("");
            txtscpassword.setText("");
            
            Parent root = FXMLLoader.load(getClass().getResource("/view/main_menu.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
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
    
}
