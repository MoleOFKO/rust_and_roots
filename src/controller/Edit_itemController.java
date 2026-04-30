/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.UiSqlService;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
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
import util.AppDialogs;
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
    @FXML
    private TableColumn<?, ?> colitemtype;

    public static ObservableList<item> itemList;
    public static int item_id;
    public static String itemname;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtaname.setText(LoginController.txtname);
        initColumn();
        loadItems(null);

        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }

    @FXML
    private void handlebtnecharacteraction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/edit_character.fxml");
    }

    @FXML
    private void handlebtneitemaction(ActionEvent event) {
        // already on this screen
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
        if (txtsearch.getText().isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter search name");
            return;
        }
        loadItems(txtsearch.getText());
    }

    private void loadItems(String namePrefix) {
        try {
            itemList = FXCollections.observableArrayList();
            for (UiSqlService.ItemRow row : UiSqlService.fetchItems(namePrefix)) {
                item_id = row.itemId();
                String imgName = row.imageFile();

                Image image = ItemEditorSupport.loadItemImage(imgName);
                ImageView imgView = new ImageView();
                imgView.setImage(image);
                imgView.setFitHeight(50);
                imgView.setFitWidth(50);
                imgView.setPreserveRatio(true);

                itemList.add(new item(
                    row.itemId(),
                    row.itemName(),
                    row.itemTypeName(),
                    row.price(),
                    row.str(),
                    row.agi(),
                    row.vit(),
                    row.dex(),
                    row.hp(),
                    row.stamina(),
                    imgView
                ));
            }
            item_view.setItems(itemList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initColumn() {
        colaccid.setCellValueFactory(new PropertyValueFactory("item_id"));
        colaccname.setCellValueFactory(new PropertyValueFactory("item_name"));
        colitemtype.setCellValueFactory(new PropertyValueFactory("item_type_name"));
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
            AppDialogs.showMessageDialog(null, "Please select a item first!");
            return;
        }
        int num = AppDialogs.showConfirmDialog(null, "Are you Sure?");
        if (num != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (UiSqlService.deleteItemAndStats(i.getItem_id())) {
                AppDialogs.showMessageDialog(null, "Deleted Successfully!");
            } else {
                AppDialogs.showMessageDialog(null, "No item was deleted.");
            }
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        }

        util.SceneManager.switchScene("/view/edit_item.fxml");
    }

    @FXML
    void handlebtneditaction(ActionEvent event) throws IOException {
        item i = item_view.getSelectionModel().getSelectedItem();
        if (i == null) {
            AppDialogs.showMessageDialog(null, "Please Select one Product You Want to Edit!");
            return;
        }

        item_id = i.getItem_id();
        itemname = i.getItem_name();
        Stage stage = Rust_and_roots.stage;
        util.SceneManager.switchScene("/view/item_edit_btn.fxml");
    }
}

