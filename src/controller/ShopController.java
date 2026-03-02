/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author larry
 */
public class ShopController implements Initializable {

    @FXML
    private Label lblcoin;
    @FXML
    private ImageView inyimg1;
    @FXML
    private Button invbtn1;
    @FXML
    private Label invlbl1;
    @FXML
    private ImageView inyimg2;
    @FXML
    private Button invbtn2;
    @FXML
    private Label invlbl2;
    @FXML
    private ImageView inyimg3;
    @FXML
    private Button invbtn3;
    @FXML
    private Label invlbl3;
    @FXML
    private ImageView inyimg4;
    @FXML
    private Button invbtn4;
    @FXML
    private Label invlbl4;
    @FXML
    private ImageView inyimg5;
    @FXML
    private Button invbtn5;
    @FXML
    private Label invlbl5;
    @FXML
    private ImageView inyimg6;
    @FXML
    private Button invbtn6;
    @FXML
    private Label invlbl6;
    @FXML
    private ImageView inyimg7;
    @FXML
    private Button invbtn7;
    @FXML
    private Label invlbl7;
    @FXML
    private ImageView inyimg8;
    @FXML
    private Button invbtn8;
    @FXML
    private Label invlbl8;
    @FXML
    private ImageView inyimg9;
    @FXML
    private Button invbtn9;
    @FXML
    private Label invlbl9;
    @FXML
    private ImageView inyimg10;
    @FXML
    private Button invbtn10;
    @FXML
    private Label invlbl10;
    @FXML
    private ImageView inyimg12;
    @FXML
    private Button invbtn11;
    @FXML
    private Label invlbl11;
    @FXML
    private ImageView inyimg13;
    @FXML
    private Button invbtn12;
    @FXML
    private Label invlbl12;
    @FXML
    private ImageView inyimg14;
    @FXML
    private Button invbtn13;
    @FXML
    private Label invlbl13;
    @FXML
    private ImageView inyimg15;
    @FXML
    private Button invbtn14;
    @FXML
    private Label invlbl14;
    @FXML
    private Button invbtn15;
    @FXML
    private Label invlbl15;
    @FXML
    private ImageView inyimg16;
    @FXML
    private Button invbtn16;
    @FXML
    private Label invlbl16;
    @FXML
    private ImageView inyimg17;
    @FXML
    private Button invbtn17;
    @FXML
    private Label invlbl17;
    @FXML
    private ImageView inyimg18;
    @FXML
    private Button invbtn18;
    @FXML
    private Label invlbl18;
    @FXML
    private ImageView inyimg19;
    @FXML
    private Button invbtn19;
    @FXML
    private Label invlbl19;
    @FXML
    private ImageView inyimg20;
    @FXML
    private Button invbtn20;
    @FXML
    private Label invlbl20;
    @FXML
    private Pane buypane;
    @FXML
    private ImageView imgbuy1;
    @FXML
    private Button btnbuy1;
    @FXML
    private Label lblbuy1;
    @FXML
    private ImageView imgbuy2;
    @FXML
    private Button btnbuy2;
    @FXML
    private Label lblbuy2;
    @FXML
    private ImageView imgbuy3;
    @FXML
    private Button btnbuy3;
    @FXML
    private Label lblbuy3;
    @FXML
    private ImageView imgbuy4;
    @FXML
    private Button btnbuy4;
    @FXML
    private Label lblbuy4;
    @FXML
    private ImageView imgbuy5;
    @FXML
    private Button btnbuy5;
    @FXML
    private Label lblbuy5;
    @FXML
    private ImageView imgbuy6;
    @FXML
    private Button btnbuy6;
    @FXML
    private Label lblbuy6;
    @FXML
    private ImageView imgbuy7;
    @FXML
    private Button btnbuy7;
    @FXML
    private Label lblbuy7;
    @FXML
    private ImageView imgbuy8;
    @FXML
    private Button btnbuy8;
    @FXML
    private Label lblbuy8;
    @FXML
    private ImageView imgbuy9;
    @FXML
    private Button btnbuy9;
    @FXML
    private Label lblbuy9;
    @FXML
    private ImageView imgbuy10;
    @FXML
    private Button btnbuy10;
    @FXML
    private Label lblbuy10;
    @FXML
    private ImageView imgbuy11;
    @FXML
    private Button btnbuy11;
    @FXML
    private Label lblbuy11;
    @FXML
    private ImageView imgbuy12;
    @FXML
    private Button btnbuy12;
    @FXML
    private Label lblbuy12;
    @FXML
    private Pane sellpane;
    @FXML
    private ImageView imgsell1;
    @FXML
    private Button btnsell1;
    @FXML
    private Label lblsell1;
    @FXML
    private ImageView imgsell2;
    @FXML
    private Button btnsell2;
    @FXML
    private Label lblsell2;
    @FXML
    private ImageView imgsell3;
    @FXML
    private Button btnsell3;
    @FXML
    private Label lblsell3;
    @FXML
    private ImageView imgsell4;
    @FXML
    private Button btnsell4;
    @FXML
    private Label lblsell4;
    @FXML
    private ImageView imgsell5;
    @FXML
    private Button btnsell5;
    @FXML
    private Label lblsell5;
    @FXML
    private ImageView imgsell6;
    @FXML
    private Button btnsell6;
    @FXML
    private Label lblsell6;
    @FXML
    private ImageView imgsell7;
    @FXML
    private Button btnsell7;
    @FXML
    private Label lblsell7;
    @FXML
    private ImageView imgsell8;
    @FXML
    private Button btnsell8;
    @FXML
    private Label lblsell8;
    @FXML
    private ImageView imgsell9;
    @FXML
    private Button btnsell9;
    @FXML
    private Label lblsell9;
    @FXML
    private ImageView imgsell10;
    @FXML
    private Button btnsell10;
    @FXML
    private Label lblsell10;
    @FXML
    private ImageView imgsell11;
    @FXML
    private Button btnsell11;
    @FXML
    private Label lblsell11;
    @FXML
    private ImageView imgsell12;
    @FXML
    private Button btnsell12;
    @FXML
    private Label lblsell12;
    @FXML
    private Button btnbuy;
    @FXML
    private Button btnsell;
    @FXML
    private Button btnback;
    @FXML
    private Label lbltotal;
    @FXML
    private TableView<?> tableitemdetail;
    @FXML
    private TableColumn<?, ?> tableimg;
    @FXML
    private TableColumn<?, ?> tablequantity;
    @FXML
    private TableColumn<?, ?> tablecost;
    @FXML
    private Button colbuy;
    @FXML
    private Button colsell;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handlebuyaction(ActionEvent event) {
    }

    @FXML
    private void handlesellaction(ActionEvent event) {
    }

    @FXML
    private void handlebackaction(ActionEvent event) {
    }

    @FXML
    private void handlecolbuyaction(ActionEvent event) {
    }

    @FXML
    private void handlecolsellaction(ActionEvent event) {
    }
    
}
