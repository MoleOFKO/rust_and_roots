package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import util.SpawnTag;
/**
 * FXML Controller class
 *
 * @author MITUSER-1
 */
public class Left_map_noone_of_oneController extends BaseMapController {

    @FXML
    private StackPane view;
    @FXML
    private Group world;
    @FXML
    private Group walls;
    @FXML
    private Rectangle wall;
    @FXML
    private Rectangle wall1;
    @FXML
    private Rectangle wall2;
    @FXML
    private Rectangle wall3;
    @FXML
    private Rectangle exit_up;
    @FXML
    private Rectangle exit_right;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_up;
    @FXML
    private Rectangle spawn_from_right;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;


    @Override
    protected String mapKey() {
        return "left_map_noone_of_one";
    }

    @Override
    protected void configureExits() {
        registerExit(exit_right, "/view/left_map_noone.fxml", SpawnTag.FROM_LEFT);
        registerExit(exit_up, "/view/left_map_notwo_of_one.fxml", SpawnTag.FROM_DOWN);
    }

    @FXML
    private void handleusepointaction(MouseEvent event) {
        onSharedUsePoint();
    }

    @FXML
    private void handleconfirmaction(MouseEvent event) {
        onSharedConfirm();
    }

    @FXML
    private void handlebtnskillacion(MouseEvent event) {
        setSkillsOpen(true);
        setStatusOpen(false);
    }

    @FXML
    private void handleHPminusaction(MouseEvent event) {
        onStatusHpMinus();
    }

    @FXML
    private void handleHPplusaction(MouseEvent event) {
        onStatusHpPlus();
    }

    @FXML
    private void handleSPminusaction(MouseEvent event) {
        onStatusSpMinus();
    }

    @FXML
    private void handleSTRminusaction(MouseEvent event) {
        onStatusStrMinus();
    }

    @FXML
    private void handleVITminusaction(MouseEvent event) {
        onStatusVitMinus();
    }

    @FXML
    private void handleDEXminusaction(MouseEvent event) {
        onStatusDexMinus();
    }

    @FXML
    private void handleAGIminusaction(MouseEvent event) {
        onStatusAgiMinus();
    }

    @FXML
    private void handleSPplusaction(MouseEvent event) {
        onStatusSpPlus();
    }

    @FXML
    private void handleSTRplusaction(MouseEvent event) {
        onStatusStrPlus();
    }

    @FXML
    private void handleVITplusaction(MouseEvent event) {
        onStatusVitPlus();
    }

    @FXML
    private void handleDEXplusaction(MouseEvent event) {
        onStatusDexPlus();
    }

    @FXML
    private void handleAGIplusaction(MouseEvent event) {
        onStatusAgiPlus();
    }

    @FXML
    private void handlebtnstatusaction(MouseEvent event) {
        setStatusOpen(true);
        setSkillsOpen(false);
    }

    @FXML
    private void handlefarmingminusaction(MouseEvent event) {
        onSkillFarmingMinus();
    }

    @FXML
    private void handlefarmingplusaction(MouseEvent event) {
        onSkillFarmingPlus();
    }

    @FXML
    private void handlegatheringminusaction(MouseEvent event) {
        onSkillGatheringMinus();
    }

    @FXML
    private void handlecraftingminusaction(MouseEvent event) {
        onSkillCraftingMinus();
    }

    @FXML
    private void handlefishingminusaction(MouseEvent event) {
        onSkillFishingMinus();
    }

    @FXML
    private void handlecookingminusaction(MouseEvent event) {
        onSkillCookingMinus();
    }

    @FXML
    private void handlegatheringplusaction(MouseEvent event) {
        onSkillGatheringPlus();
    }

    @FXML
    private void handlecraftingplusaction(MouseEvent event) {
        onSkillCraftingPlus();
    }

    @FXML
    private void handlefishingplusaction(MouseEvent event) {
        onSkillFishingPlus();
    }

    @FXML
    private void handlecookingplusaction(MouseEvent event) {
        onSkillCookingPlus();
    }

    @FXML
    private void handlesaveexitaction(ActionEvent event) {
    }

    @FXML
    private void handlesave(ActionEvent event) {
    }

    @FXML
    private void handleload(ActionEvent event) {
    }

    @FXML
    private void handleopenInventoryaction(ActionEvent event) {
        openInventory();
    }

    @FXML
    private void handlecloseaction(MouseEvent event) {
        closeMenuScreen();
    }

    @FXML
    private void handleopenstatusaction(ActionEvent event) {
        openStatus();
    }

    @FXML
    private void handleopensettingaction(ActionEvent event) {
        openSetting();
    }
    
}
