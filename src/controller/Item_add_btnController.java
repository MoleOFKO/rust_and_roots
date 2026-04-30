/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.UiSqlService;
import db.dbcon;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import util.AppDialogs;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author Shane Khant Ko
 */
public class Item_add_btnController implements Initializable {

    @FXML
    private Label lblaname;
    @FXML
    private TextField txtmname;
    @FXML
    private TextField txtstr;
    @FXML
    private TextField txtagi;
    @FXML
    private TextField txtdex;
    @FXML
    private TextField txtvid;
    @FXML
    private TextField txthp;
    @FXML
    private TextField txtstamina;
    @FXML
    private Button btnupload;
    @FXML
    private Button btnsave;
    @FXML
    private Button btnback;
    @FXML
    private TextField txtmobstype;
    @FXML
    private ImageView itemimage;
    @FXML
    private StackPane view;
    @FXML
    private Group uiRoot;
    @FXML
    private ComboBox<ItemEditorSupport.ItemTypeOption> cbitemtype;

    private String img_name;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblaname.setText(LoginController.txtname);

        try (Connection con = new dbcon().getConnection()) {
            ItemEditorSupport.loadItemTypes(con, cbitemtype);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);
    }

    @FXML
    private void handlebtnuploadaction(ActionEvent event) throws IOException {
        String selectedImage = ItemEditorSupport.choosePreviewAndCopyItemImage(
            itemimage.getScene().getWindow(),
            itemimage
        );
        if (selectedImage != null) {
            img_name = selectedImage;
        }
    }

    @FXML
    private void handlebtnsaveaction(ActionEvent event) throws SQLException, IOException {
        if (txtmname.getText().isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter Item Name");
            return;
        }
        if (txtmobstype.getText().isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter Price");
            return;
        }
        if (cbitemtype.getSelectionModel().getSelectedItem() == null) {
            AppDialogs.showMessageDialog(null, "Please Select Item Type");
            return;
        }
        if (txtstr.getText().isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter STR");
            return;
        }
        if (txtagi.getText().isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter AGI");
            return;
        }
        if (txtdex.getText().isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter DEX");
            return;
        }
        if (txtvid.getText().isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter VIT");
            return;
        }
        if (txthp.getText().isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter HP");
            return;
        }
        if (txtstamina.getText().isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter Stamina");
            return;
        }
        if (itemimage.getImage() == null) {
            AppDialogs.showMessageDialog(null, "Please Upload Image");
            return;
        }

        try {
            int itemTypeId = cbitemtype.getSelectionModel().getSelectedItem().id();
            int price = ItemEditorSupport.parseRequiredInt(txtmobstype, "Price");
            int str = ItemEditorSupport.parseRequiredInt(txtstr, "STR");
            int agi = ItemEditorSupport.parseRequiredInt(txtagi, "AGI");
            int dex = ItemEditorSupport.parseRequiredInt(txtdex, "DEX");
            int vit = ItemEditorSupport.parseRequiredInt(txtvid, "VIT");
            int hp = ItemEditorSupport.parseRequiredInt(txthp, "HP");
            int stamina = ItemEditorSupport.parseRequiredInt(txtstamina, "Stamina");

            boolean inserted = UiSqlService.insertItemWithStats(
                txtmname.getText().trim(),
                price,
                itemTypeId,
                str,
                agi,
                dex,
                vit,
                hp,
                stamina,
                img_name
            );

            if (inserted) {
                AppDialogs.showMessageDialog(null, "Inserted Successfully!");
                util.SceneManager.switchScene("/view/edit_item.fxml");
            } else {
                AppDialogs.showMessageDialog(null, "Insert failed.");
            }
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        } catch (NumberFormatException e) {
            AppDialogs.showMessageDialog(null, e.getMessage());
        }
    }

    @FXML
    private void handlebtnbackaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/edit_item.fxml");
    }

    @FXML
    private void handlecbitemboxaction(ActionEvent event) {
    }
}

