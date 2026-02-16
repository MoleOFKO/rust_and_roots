/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.dbcon;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import model.mobs;
import rust_and_roots.Rust_and_roots;
import static rust_and_roots.Rust_and_roots.stage;

/**
 * FXML Controller class
 *
 * @author Shane Khant Ko
 */
public class Edit_characterController implements Initializable {

    @FXML
    private Label txtaname;
    @FXML
    private Button btnecharacter;
    @FXML
    private Button btneitem;
    @FXML
    private Button btnaccmanagement;
    @FXML
    private Button btnvplayer;
    @FXML
    private Button btnlogout;
    @FXML
    private TableView<mobs> mob_view;
    @FXML
    private TableColumn<?, ?> colmobid;
    @FXML
    private TableColumn<?, ?> colmobname;
    @FXML
    private TableColumn<?, ?> colmobtype;
    @FXML
    private TableColumn<?, ?> colstr;
    @FXML
    private TableColumn<?, ?> colagi;
    @FXML
    private TableColumn<?, ?> colvit;
    @FXML
    private TableColumn<?, ?> coldex;
    @FXML
    private TableColumn<?, ?> colhp;
    @FXML
    private TableColumn<?, ?> colstimana;
    @FXML
    private TableColumn<?, ?> mobimage;
    @FXML
    private TextField txtsearch;
    @FXML
    private Button btnsearch;
    @FXML
    private Button btnedit;
    @FXML
    private Button btnadd;
    @FXML
    private Button btndelete;
    
    
    Connection con;
    ResultSet rs;
    PreparedStatement pst;
    Statement st;
    Parent root;
    public static ObservableList<mobs> mobsList;
    public static int mid;
    public static int mobs_id;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbcon db = new dbcon();
        try{
            con = db.getConnection();
            System.out.println("Ready...");
            txtaname.setText(LoginController.txtname);
            initColumn();
            initmobstList();
            
            
        }catch (ClassNotFoundException e){
            System.out.println("Not Connected....");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    

    @FXML
    private void handlebtnecharacteraction(ActionEvent event) throws SQLException, IOException {
        root = FXMLLoader.load(getClass().getResource("/view/edit_character.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }

    @FXML
    private void handlebtneitemaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/edit_item.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }

    @FXML
    private void handlebtnaccmanagementaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/account_management.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }

    @FXML
    private void handlebtnvplayeraction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/account_view.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }

    @FXML
    private void handlebtnlogoutaction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }

    @FXML
    private void handlesearchaction(ActionEvent event) throws SQLException {
        if(txtsearch.getText().isEmpty())
            JOptionPane.showMessageDialog(null, "Please Enter search name");
        else{
            mobsList.clear();
            String sname=txtsearch.getText();
            String sql = "select m.mobs_id,m.mobs_name,m.mobs_type,s.str,s.agi,s.vid,s.dex,s.hp,s.stimana,m.image from mobs m JOIN stats s ON m.stats = s.stats_id where mobs_name like ?;";
            pst=con.prepareStatement(sql);
            pst.setString(1, txtsearch.getText()+"%");
            rs=pst.executeQuery();
           
            while(rs.next()){
            mobs_id = rs.getInt("mobs_id");
            String img_name = rs.getString("image");
            File file= new File("E:\\PJ\\rust_and_roots\\src\\image\\"+img_name );
            Image image = new Image(file.toURI().toString());
            ImageView imgView = new ImageView(image);
            imgView.setFitHeight(75);
            imgView.setFitWidth(75);
            imgView.setPreserveRatio(true);
            
            
            mobsList.add(new mobs(rs.getInt("mobs_id"), rs.getString("mobs_name"), rs.getString("mobs_type"), rs.getInt("str"), rs.getInt("agi"),rs.getInt("vid"),rs.getInt("dex"),rs.getInt("hp"),rs.getInt("stimana"),imgView));
         }
        }
    }
     @FXML
    void handlebtnaddaction(ActionEvent event) throws IOException {
         

    }

    @FXML
    void handlebtndeleteaction(ActionEvent event) throws SQLException, IOException {
        mobs m = mob_view.getSelectionModel().getSelectedItem();
        if (m == null) {
            JOptionPane.showMessageDialog(null, "Please select a mob first!");
            return;
        }
        int num = JOptionPane.showConfirmDialog(null, "Are you Sure?");

        if (num == JOptionPane.YES_OPTION) {
            String sql = "DELETE m, s FROM mobs m JOIN stats s ON s.stats_id = m.stats WHERE m.mobs_id = ?;";
            pst = con.prepareStatement(sql);
            pst.setInt(1, m.getMobs_id());   // use selected object
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Deleted Successfully!");
            root = FXMLLoader.load(getClass().getResource("/view/edit_character.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }                  
    }
    @FXML
    void handlebtneditaction(ActionEvent event) throws IOException {
        mobs m = mob_view.getSelectionModel().getSelectedItem();
        if(m==null)
            JOptionPane.showMessageDialog(null, "Please Select one Mobs You Want to Edit!");
        else{
            mid = m.getMobs_id();
            Stage stage = Rust_and_roots.stage;
            root = FXMLLoader.load(getClass().getResource("/view/char_edit_btn.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } 
    }
    private void initmobstList(   ) throws SQLException{
        mobsList = FXCollections.observableArrayList();
            String sql = "select m.mobs_id,m.mobs_name,m.mobs_type,s.str,s.agi,s.vid,s.dex,s.hp,s.stimana,m.image from mobs m JOIN stats s ON m.stats= s.stats_id;";
        st = con.createStatement();
        rs = st.executeQuery(sql);
        while(rs.next()){
            String img_name = rs.getString("image");
            File file= new File("E:\\PJ\\rust_and_roots\\src\\image\\"+img_name );
            Image image = new Image(file.toURI().toString());
            ImageView imgView = new ImageView(image);
            imgView.setFitHeight(50);
            imgView.setFitWidth(50);
            imgView.setPreserveRatio(true);
            
            
            mobsList.add(new mobs(rs.getInt("mobs_id"), rs.getString("mobs_name"), rs.getString("mobs_type"), rs.getInt("str"), rs.getInt("agi"),rs.getInt("vid"),rs.getInt("dex"),rs.getInt("hp"),rs.getInt("stimana"),imgView));
         }
        mob_view.setItems(mobsList);
    }
    
    public void initColumn(){
        
        colmobid.setCellValueFactory(new PropertyValueFactory("mobs_id"));
        colmobname.setCellValueFactory(new PropertyValueFactory("mobs_name"));
        colmobtype.setCellValueFactory(new PropertyValueFactory("mobs_type"));
        colstr.setCellValueFactory(new PropertyValueFactory("str"));
        colagi.setCellValueFactory(new PropertyValueFactory("agi"));
        colvit.setCellValueFactory(new PropertyValueFactory("vid"));
        coldex.setCellValueFactory(new PropertyValueFactory("dex"));
        colhp.setCellValueFactory(new PropertyValueFactory("hp"));
        colstimana.setCellValueFactory(new PropertyValueFactory("stimana"));
        mobimage.setCellValueFactory(new PropertyValueFactory("image"));
        
        
        
        
    }
    
    public class PopupExample extends Application {

    @Override
    public void start(Stage primaryStage) {

        Button openPopupBtn = new Button("Open Popup");

        openPopupBtn.setOnAction(e -> {
            // Create new window (Stage)
            Stage popupStage = new Stage();
            popupStage.setTitle("Popup Window");

            VBox layout = new VBox(10);
            layout.getChildren().add(new Button("Inside Popup"));

            Scene popupScene = new Scene(layout, 300, 200);
            popupStage.setScene(popupScene);

            popupStage.show(); // Show popup
        });

        VBox root = new VBox(openPopupBtn);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("Main Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    }
}
