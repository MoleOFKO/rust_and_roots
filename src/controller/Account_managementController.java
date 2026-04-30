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
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javax.swing.JOptionPane;
import model.Account;
import util.AppDialogs;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author Shane Khant Ko
 */
public class Account_managementController implements Initializable {

    @FXML
    private Button btnactive;
    @FXML
    private Label txtaname;
    @FXML
    private TableView<Account> account_view;
    @FXML
    private TableColumn<?, ?> colaccname;
    @FXML
    private TableColumn<?, ?> colemail;
    @FXML
    private TableColumn<?, ?> colstatus;
    @FXML
    private TableColumn<?, ?> colcreateddate;
    @FXML
    private TextField txtsearch;
    @FXML
    private Button btnedit;
    @FXML
    private Button btnban;
    @FXML
    private Button btnsearch;

    public static ObservableList<Account> accountList;
    public static String accname;

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
    private TableColumn<?, ?> user_type;
    @FXML
    private ImageView imgban;
    @FXML
    private ImageView imgactivate;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WorldScaler scaler = new WorldScaler(view, uiRoot);
        Platform.runLater(scaler::scale);

        txtaname.setText(LoginController.txtname);

        initColumn();
        initaccountList(null);

        btnactive.setVisible(false);
        btnban.setVisible(false);
        if (imgactivate != null) imgactivate.setVisible(false);
        if (imgban != null) imgban.setVisible(false);

        account_view.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel == null) {
                btnactive.setVisible(false);
                btnban.setVisible(false);
                if (imgactivate != null) imgactivate.setVisible(false);
                if (imgban != null) imgban.setVisible(false);
                return;
            }

            String status = newSel.getAccount_stats();

            if ("active".equalsIgnoreCase(status)) {
                btnactive.setVisible(false);
                if (imgactivate != null) imgactivate.setVisible(false);
                btnban.setVisible(true);
                if (imgban != null) imgban.setVisible(true);
            } else if ("ban".equalsIgnoreCase(status)) {
                btnactive.setVisible(true);
                if (imgactivate != null) imgactivate.setVisible(true);
                btnban.setVisible(false);
                if (imgban != null) imgban.setVisible(false);
            } else {
                btnactive.setVisible(false);
                btnban.setVisible(false);
                if (imgactivate != null) imgactivate.setVisible(false);
                if (imgban != null) imgban.setVisible(false);
            }
        });
    }

    @FXML
    private void handlebtnecharacteraction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/edit_character.fxml");
    }

    @FXML
    private void handlebtneitemaction(ActionEvent event) throws IOException {
        util.SceneManager.switchScene("/view/edit_item.fxml");
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
        util.SceneManager.switchScene("/view/Login.fxml");
    }

    private void initaccountList(String namePrefix) {
        try {
            accountList = FXCollections.observableArrayList(
                UiSqlService.fetchAccountsForManagement(LoginController.txtname, namePrefix)
            );
            account_view.setItems(accountList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initColumn() {
        colaccname.setCellValueFactory(new PropertyValueFactory("account_name"));
        colemail.setCellValueFactory(new PropertyValueFactory("email"));
        user_type.setCellValueFactory(new PropertyValueFactory("user_type"));
        colstatus.setCellValueFactory(new PropertyValueFactory("account_stats"));
        colcreateddate.setCellValueFactory(new PropertyValueFactory("created_date"));
    }

    @FXML
    private void handlesearchaction(ActionEvent event) {
        if (txtsearch.getText().isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter search name");
            return;
        }
        initaccountList(txtsearch.getText());
    }

    @FXML
    void handlebtneditaction(ActionEvent event) throws IOException {
        Account a = account_view.getSelectionModel().getSelectedItem();
        if (a == null) {
            AppDialogs.showMessageDialog(null, "Please Select one Product You Want to Edit!");
            return;
        }
        accname = a.getAccount_name();
        util.SceneManager.switchScene("/view/account_edit.fxml");
    }

    @FXML
    private void handlebanaction(ActionEvent event) throws SQLException, IOException {
        Account a = account_view.getSelectionModel().getSelectedItem();
        if (a == null) {
            AppDialogs.showMessageDialog(null, "Please select an account first!");
            return;
        }

        int num = AppDialogs.showConfirmDialog(null, "Are you Sure?");
        if (num != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (UiSqlService.updateAccountStatusByName(a.getAccount_name(), 2)) {
                AppDialogs.showMessageDialog(null, "Baned Successfully!");
            } else {
                AppDialogs.showMessageDialog(null, "No account was updated.");
            }
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        }

        util.SceneManager.switchScene("/view/account_management.fxml");
    }

    @FXML
    private void handleactiveaction(ActionEvent event) throws SQLException, IOException {
        Account a = account_view.getSelectionModel().getSelectedItem();
        if (a == null) {
            AppDialogs.showMessageDialog(null, "Please select an account first!");
            return;
        }

        int num = AppDialogs.showConfirmDialog(null, "Are you Sure?");
        if (num != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (UiSqlService.updateAccountStatusByName(a.getAccount_name(), 1)) {
                AppDialogs.showMessageDialog(null, "Actived Successfully!");
            } else {
                AppDialogs.showMessageDialog(null, "No account was updated.");
            }
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Database driver not found.", ex);
        }

        util.SceneManager.switchScene("/view/account_management.fxml");
    }
}

