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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import model.item;
import rust_and_roots.Rust_and_roots;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author MITUSER-1
 */
public class Edit_itemController implements Initializable {

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
    private StackPane view;
    @FXML 
    private Group uiRoot;
    
    @FXML
    private TableColumn<?, ?> colaccid;
    @FXML
    private TableColumn<?, ?> colaccname;
    private TableColumn<?, ?> colemail;
    private TableColumn<?, ?> colstatus;
    private TableColumn<?, ?> colcreateddate;
    @FXML
    private TextField txtsearch;
    @FXML
    private Button btnsearch;
    @FXML
    private TableView<item> item_view;
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
    private TableColumn<?, ?> colimage;
    @FXML
    private Button btnban;
    @FXML
    private Button btnedit;
    @FXML
    private Button btnadd;
    @FXML
    private TableColumn<?, ?> colprice;
    
    Connection con;
    ResultSet rs;
    PreparedStatement pst;
    Statement st;
    Parent root;
    public static ObservableList<item> itemList;
    String created_date;
    public static int item_id;
    public static String itemname;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbcon db = new dbcon();
        try{
            con = db.getConnection();
            System.out.println("Ready...");
            txtaname.setText(LoginController.txtname);
            initColumn();
            inititemList();
            
            
        }catch (ClassNotFoundException e){
            System.out.println("Not Connected....");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }    

    @FXML
    private void handlebtnecharacteraction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/edit_character.fxml");
    }

    @FXML
    private void handlebtneitemaction(ActionEvent event) {
        
        
    }

    @FXML
    private void handlebtnaccmanagementaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/account_management.fxml");
    }

    @FXML
    private void handlebtnvplayeraction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/account_view.fxml");
    }

    @FXML
    private void handlebtnlogoutaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/login.fxml");
    }

    @FXML
    private void handlesearchaction(ActionEvent event) throws SQLException {
        if(txtsearch.getText().isEmpty())
            JOptionPane.showMessageDialog(null, "Please Enter search name");
        else{
            itemList.clear();
            String sname=txtsearch.getText();
            String sql = "select i.item_id,i.item_name,i.price,s.str,s.agi,s.vit,s.dex,s.hp,s.stamina,i.image from items i JOIN stats s ON i.stats_id = s.stats_id where item_name like ?;";
            pst=con.prepareStatement(sql);
            pst.setString(1, txtsearch.getText()+"%");
            rs=pst.executeQuery();
           
            while(rs.next()){
            String img_name = rs.getString("image");
            File file= new File("E:\\PJ\\rust_and_roots\\src\\image\\items\\"+img_name );
            Image image = new Image(file.toURI().toString());
            ImageView imgView = new ImageView(image);
            imgView.setFitHeight(75);
            imgView.setFitWidth(75);
            imgView.setPreserveRatio(true);
            
            
            itemList.add(new item(rs.getInt("item_id"), rs.getString("item_name"), rs.getInt("price"), rs.getInt("str"), rs.getInt("agi"),rs.getInt("vit"),rs.getInt("dex"),rs.getInt("hp"),rs.getInt("stamina"),imgView));
         }
        }
    }
    
    private void inititemList(   ) throws SQLException{
        itemList = FXCollections.observableArrayList();
        String sql="SELECT i.item_id,i.item_name,i.price,s.str,s.agi,s.vit,s.dex,s.hp,s.stamina,i.image FROM items i JOIN stats s ON i.stats_id = s.stats_id;";
        st = con.createStatement();
        rs = st.executeQuery(sql);
        while(rs.next()){
            item_id = rs.getInt("item_id");
            String img_name = rs.getString("image");
            File file= new File("E:\\PJ\\rust_and_roots\\src\\image\\items\\"+img_name );
            Image image = new Image(file.toURI().toString());
            ImageView imgView = new ImageView(image);
            imgView.setFitHeight(50);
            imgView.setFitWidth(50);
            imgView.setPreserveRatio(true);
            
            
            itemList.add(new item(rs.getInt("item_id"), rs.getString("item_name"), rs.getInt("price"), rs.getInt("str"), rs.getInt("agi"),rs.getInt("vit"),rs.getInt("dex"),rs.getInt("hp"),rs.getInt("stamina") ,imgView));
         }
        item_view.setItems(itemList);
    }
    
    public void initColumn(){
        
        colaccid.setCellValueFactory(new PropertyValueFactory("item_id"));
        colaccname.setCellValueFactory(new PropertyValueFactory("item_name"));
        colprice.setCellValueFactory(new PropertyValueFactory("price"));
        colstr.setCellValueFactory(new PropertyValueFactory("str"));
        colagi.setCellValueFactory(new PropertyValueFactory("agi"));
        colvit.setCellValueFactory(new PropertyValueFactory("vid"));
        coldex.setCellValueFactory(new PropertyValueFactory("dex"));
        colhp.setCellValueFactory(new PropertyValueFactory("hp"));
        colstimana.setCellValueFactory(new PropertyValueFactory("stimana"));
        colimage.setCellValueFactory(new PropertyValueFactory("image"));
        
        
        
        
    }
    @FXML
    void handlebtnaddaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/item_add_btn.fxml");
    }

    @FXML
    void handlebtnbanaction(ActionEvent event) throws SQLException, IOException {
       item i = item_view.getSelectionModel().getSelectedItem();
        if (i == null) {
            JOptionPane.showMessageDialog(null, "Please select a item first!");
            return;
        }
        int num = JOptionPane.showConfirmDialog(null, "Are you Sure?");

        if (num == JOptionPane.YES_OPTION) {
            String sql = "DELETE i, s FROM items i JOIN stats s ON s.stats_id = i.stats_id WHERE i.item_id = ?;";
            pst = con.prepareStatement(sql);
            pst.setInt(1, i.getItem_id());   // use selected object
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Deleted Successfully!");
            util.SceneManager.switchScene("/view/edit_item.fxml");
        }   
    }
    @FXML
    void handlebtneditaction(ActionEvent event) throws IOException {
        item i= item_view.getSelectionModel().getSelectedItem();
        if(i==null)
            JOptionPane.showMessageDialog(null, "Please Select one Product You Want to Edit!");
        else{
            itemname = i.getItem_name();
            Stage stage = Rust_and_roots.stage;
            util.SceneManager.switchScene("/view/item_edit_btn.fxml");
        }    
    }
    
}
