package controller;

import db.SaveGameService;
import game.GameState;
import game.GameTimeManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import util.SceneManager;
import util.SpawnTag;
/**
 * FXML Controller class
 *
 * @author MITUSER-1
 */
public class Left_mapController extends BaseMapController {

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
    private Rectangle wall4;
    @FXML
    private Rectangle wall5;
    @FXML
    private Rectangle wall6;
    @FXML
    private Rectangle wall7;
    @FXML
    private Rectangle wall8;
    @FXML
    private Rectangle wall9;
    @FXML
    private Rectangle wall10;
    @FXML
    private Rectangle wall11;
    @FXML
    private Rectangle wall12;
    @FXML
    private Rectangle wall13;
    @FXML
    private Rectangle wall14;
    @FXML
    private Rectangle wall15;
    @FXML
    private Rectangle wall16;
    @FXML
    private Rectangle wall17;
    @FXML
    private Rectangle exit_left;
    @FXML
    private Rectangle exit_up;
    @FXML
    private Rectangle spawn_default;
    @FXML
    private Rectangle spawn_from_up;
    @FXML
    private Group human;
    @FXML
    private Rectangle human_hitbox;
    @FXML
    private ImageView human_img;
    @FXML
    private HBox hotbar;
    @FXML
    private Label hotbarguide;
    @FXML
    private StackPane Hslot;
    @FXML
    private StackPane Hslot1;
    @FXML
    private StackPane Hslot2;
    @FXML
    private StackPane Hslot3;
    @FXML
    private StackPane Hslot4;
    @FXML
    private ImageView slotImg;
    @FXML
    private ImageView slotImg1;
    @FXML
    private ImageView slotImg2;
    @FXML
    private ImageView slotImg3;
    @FXML
    private ImageView slotImg4;
    @FXML
    private Label slotLb;
    @FXML
    private Label slotLb1;
    @FXML
    private Label slotLb2;
    @FXML
    private Label slotLb3;
    @FXML
    private Label slotLb4;
    @FXML
    private ToggleButton slotBtn;
    @FXML
    private ToggleButton slotBtn1;
    @FXML
    private ToggleButton slotBtn2;
    @FXML
    private ToggleButton slotBtn3;
    @FXML
    private ToggleButton slotBtn4;
    @FXML
    private ToggleGroup selectedhotbar;
    @FXML
    private StackPane Hslot5;
    @FXML
    private Button slotBtn5;
    @FXML
    private Pane inventoryOverlay;
    @FXML
    private Group menuInventory;
    @FXML
    private Group player_inv_group;
    @FXML
    private ImageView Playerinvimg;
    @FXML
    private Button Playerinvbtn;
    @FXML
    private Label Playerinvlbl;
    @FXML
    private ImageView Playerinvimg1;
    @FXML
    private Button Playerinvbtn1;
    @FXML
    private Label Playerinvlbl1;
    @FXML
    private ImageView Playerinvimg2;
    @FXML
    private Button Playerinvbtn2;
    @FXML
    private Label Playerinvlbl2;
    @FXML
    private ImageView Playerinvimg3;
    @FXML
    private Button Playerinvbtn3;
    @FXML
    private Label Playerinvlbl3;
    @FXML
    private ImageView Playerinvimg4;
    @FXML
    private Button Playerinvbtn4;
    @FXML
    private Label Playerinvlbl4;
    @FXML
    private ImageView Playerinvimg5;
    @FXML
    private Button Playerinvbtn5;
    @FXML
    private Label Playerinvlbl5;
    @FXML
    private ImageView Playerinvimg6;
    @FXML
    private Button Playerinvbtn6;
    @FXML
    private Label Playerinvlbl6;
    @FXML
    private ImageView Playerinvimg7;
    @FXML
    private Button Playerinvbtn7;
    @FXML
    private Label Playerinvlbl7;
    @FXML
    private ImageView Playerinvimg8;
    @FXML
    private Button Playerinvbtn8;
    @FXML
    private Label Playerinvlbl8;
    @FXML
    private ImageView Playerinvimg9;
    @FXML
    private Button Playerinvbtn9;
    @FXML
    private Label Playerinvlbl9;
    @FXML
    private ImageView Playerinvimg10;
    @FXML
    private Button Playerinvbtn10;
    @FXML
    private Label Playerinvlbl10;
    @FXML
    private ImageView Playerinvimg11;
    @FXML
    private Button Playerinvbtn11;
    @FXML
    private Label Playerinvlbl11;
    @FXML
    private ImageView Playerinvimg12;
    @FXML
    private Button Playerinvbtn12;
    @FXML
    private Label Playerinvlbl12;
    @FXML
    private ImageView Playerinvimg13;
    @FXML
    private Button Playerinvbtn13;
    @FXML
    private Label Playerinvlbl13;
    @FXML
    private ImageView Playerinvimg14;
    @FXML
    private Button Playerinvbtn14;
    @FXML
    private Label Playerinvlbl14;
    @FXML
    private ImageView Playerinvimg15;
    @FXML
    private Button Playerinvbtn15;
    @FXML
    private Label Playerinvlbl15;
    @FXML
    private ImageView Playerinvimg16;
    @FXML
    private Button Playerinvbtn16;
    @FXML
    private Label Playerinvlbl16;
    @FXML
    private ImageView Playerinvimg17;
    @FXML
    private Button Playerinvbtn17;
    @FXML
    private Label Playerinvlbl17;
    @FXML
    private ImageView Playerinvimg18;
    @FXML
    private Button Playerinvbtn18;
    @FXML
    private Label Playerinvlbl18;
    @FXML
    private ImageView Playerinvimg19;
    @FXML
    private Button Playerinvbtn19;
    @FXML
    private Label Playerinvlbl19;
    @FXML
    private Group statusTab;
    @FXML
    private Label statusName;
    @FXML
    private Label statusEXP;
    @FXML
    private ImageView btnUsePoint;
    @FXML
    private ImageView btnConfirm;
    @FXML
    private Group statusgroup;
    @FXML
    private Label statusHP;
    @FXML
    private Label statusSP;
    @FXML
    private Label statusSTR;
    @FXML
    private Label statusVIT;
    @FXML
    private Label statusDEX;
    @FXML
    private Label statusAGI;
    @FXML
    private ImageView btnskill;
    @FXML
    private ImageView btnHPminus;
    @FXML
    private ImageView btnHPplus;
    @FXML
    private ImageView btnSPminus;
    @FXML
    private ImageView btnSTRminus;
    @FXML
    private ImageView btnVITminus;
    @FXML
    private ImageView btnDEXminus;
    @FXML
    private ImageView btnAGIminus;
    @FXML
    private ImageView btnSPplus;
    @FXML
    private ImageView btnSTRplus;
    @FXML
    private ImageView btnVITplus;
    @FXML
    private ImageView btnDEXplus;
    @FXML
    private ImageView btnAGIplus;
    @FXML
    private Group skillsgroup;
    @FXML
    private ImageView btnstatus;
    @FXML
    private Label farming;
    @FXML
    private Label gathering;
    @FXML
    private Label crafting;
    @FXML
    private Label fishing;
    @FXML
    private Label cooking;
    @FXML
    private ImageView btnfarmingminus1;
    @FXML
    private ImageView btnfarmingplus1;
    @FXML
    private ImageView btngatheringminus1;
    @FXML
    private ImageView btncraftingminus1;
    @FXML
    private ImageView btnfishingminus1;
    @FXML
    private ImageView btncookingminus1;
    @FXML
    private ImageView btngatheringplus1;
    @FXML
    private ImageView btncraftingplus1;
    @FXML
    private ImageView btnfishingplus1;
    @FXML
    private ImageView btncookingplus1;
    @FXML
    private Group settingsTab;
    @FXML
    private Slider timescaleslider;
    @FXML
    private Button btnsetday;
    @FXML
    private Button btnsetnight;
    @FXML
    private Button btnplus1h;
    @FXML
    private Button btnsaveandexit;
    @FXML
    private Label sliderscale;
    @FXML
    private Label lbldays;
    @FXML
    private Label lbltime;
    @FXML
    private Button btnsave;
    @FXML
    private Button btnload;
    @FXML
    private Button openInventory;
    @FXML
    private ImageView btnclose;
    @FXML
    private Button openstatus;
    @FXML
    private Button opensetting;
    @FXML
    private ImageView equip_img;
    @FXML
    private Rectangle spawn_from_right;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        bridgeLeftMapSharedUi();
        bindStatusLabels();
        bindSettingControls();
        refreshStatusAllocationUi();
        refreshMenuState();
    }

    private void bridgeLeftMapSharedUi() {
        List<StackPane> hotbarSlotNodes = List.of(Hslot, Hslot1, Hslot2, Hslot3, Hslot4);
        List<ImageView> hotbarImageNodes = List.of(slotImg, slotImg1, slotImg2, slotImg3, slotImg4);
        List<Label> hotbarLabelNodes = List.of(slotLb, slotLb1, slotLb2, slotLb3, slotLb4);
        List<ToggleButton> hotbarButtonNodes = List.of(slotBtn, slotBtn1, slotBtn2, slotBtn3, slotBtn4);

        bridgeSharedHotbarUi(
            hotbar,
            hotbarguide,
            hotbarSlotNodes,
            hotbarImageNodes,
            hotbarLabelNodes,
            hotbarButtonNodes
        );

        List<ImageView> playerInvImageNodes = List.of(
            Playerinvimg, Playerinvimg1, Playerinvimg2, Playerinvimg3, Playerinvimg4,
            Playerinvimg5, Playerinvimg6, Playerinvimg7, Playerinvimg8, Playerinvimg9,
            Playerinvimg10, Playerinvimg11, Playerinvimg12, Playerinvimg13, Playerinvimg14,
            Playerinvimg15, Playerinvimg16, Playerinvimg17, Playerinvimg18, Playerinvimg19
        );

        List<Label> playerInvLabelNodes = List.of(
            Playerinvlbl, Playerinvlbl1, Playerinvlbl2, Playerinvlbl3, Playerinvlbl4,
            Playerinvlbl5, Playerinvlbl6, Playerinvlbl7, Playerinvlbl8, Playerinvlbl9,
            Playerinvlbl10, Playerinvlbl11, Playerinvlbl12, Playerinvlbl13, Playerinvlbl14,
            Playerinvlbl15, Playerinvlbl16, Playerinvlbl17, Playerinvlbl18, Playerinvlbl19
        );

        List<Button> playerInvButtonNodes = List.of(
            Playerinvbtn, Playerinvbtn1, Playerinvbtn2, Playerinvbtn3, Playerinvbtn4,
            Playerinvbtn5, Playerinvbtn6, Playerinvbtn7, Playerinvbtn8, Playerinvbtn9,
            Playerinvbtn10, Playerinvbtn11, Playerinvbtn12, Playerinvbtn13, Playerinvbtn14,
            Playerinvbtn15, Playerinvbtn16, Playerinvbtn17, Playerinvbtn18, Playerinvbtn19
        );

        bridgeSharedInventoryOverlayUi(
            inventoryOverlay,
            menuInventory,
            statusTab,
            settingsTab,
            statusgroup,
            skillsgroup,
            player_inv_group,
            openInventory,
            openstatus,
            opensetting,
            btnclose,
            btnskill,
            btnstatus,
            playerInvImageNodes,
            playerInvLabelNodes,
            playerInvButtonNodes
        );

        rebuildSharedUiAfterBridge();
    }

    private void bindStatusLabels() {
        if (playerState == null) return;

        playerState.playerNameProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.expProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.hpProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.maxHpProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.staminaProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.maxStaminaProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.strProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.vitProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.dexProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
        playerState.agiProperty().addListener((obs, o, n) -> refreshStatusAllocationUi());
    }

    private void bindSettingControls() {
        GameTimeManager time = GameTimeManager.get();

        if (timescaleslider != null) {
            timescaleslider.valueProperty().unbindBidirectional(time.timeScaleProperty());
            timescaleslider.valueProperty().bindBidirectional(time.timeScaleProperty());
        }

        if (sliderscale != null) {
            sliderscale.textProperty().unbind();
            sliderscale.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("Current Time Speed : x%.1f", time.timeScaleProperty().get()),
                time.timeScaleProperty()
            ));
        }

        if (lbldays != null) {
            lbldays.textProperty().unbind();
            lbldays.textProperty().bind(Bindings.createStringBinding(
                () -> "Days : " + time.dayNumberProperty().get(),
                time.dayNumberProperty()
            ));
        }

        if (lbltime != null) {
            lbltime.textProperty().unbind();
            lbltime.textProperty().bind(Bindings.createStringBinding(
                () -> "Time : " + time.formattedProperty().get(),
                time.formattedProperty()
            ));
        }

        if (btnsetday != null) btnsetday.setOnAction(e -> time.setTimeOfDay(8, 0));
        if (btnsetnight != null) btnsetnight.setOnAction(e -> time.setTimeOfDay(20, 0));
        if (btnplus1h != null) btnplus1h.setOnAction(e -> time.addMinutes(60));
        if (btnsaveandexit != null) {
            btnsaveandexit.setOnAction(e -> {
                try {
                    capturePlayerSnapshot();
                    new SaveGameService().saveGame(GameState.get());
                    if (gameLoop != null) gameLoop.stop();
                    if (player != null) player.stop();
                    SceneManager.switchScene("/view/main_menu.fxml");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @Override
    protected void refreshStatusAllocationUi() {
        refreshStatusView();
        refreshSkillsView();
        refreshStatusButtons();
    }

    private void refreshStatusView() {
        if (playerState == null) return;

        if (statusName != null) statusName.setText("Name : " + playerState.getPlayerName());
        if (statusEXP != null) statusEXP.setText(formatVisibleExpLabel());
        if (statusHP != null) {
            statusHP.setText("HP : " + playerState.getHp() + "/" + (playerState.getMaxHp() + pendingHpPoints) + formatPending(pendingHpPoints));
        }
        if (statusSP != null) {
            statusSP.setText("SP : " + playerState.getStamina() + "/" + (playerState.getMaxStamina() + pendingSpPoints) + formatPending(pendingSpPoints));
        }
        if (statusSTR != null) statusSTR.setText("STR : " + playerState.getStr() + formatPending(pendingStrPoints));
        if (statusVIT != null) statusVIT.setText("VIT : " + playerState.getVit() + formatPending(pendingVitPoints));
        if (statusDEX != null) statusDEX.setText("DEX : " + playerState.getDex() + formatPending(pendingDexPoints));
        if (statusAGI != null) statusAGI.setText("AGI : " + playerState.getAgi() + formatPending(pendingAgiPoints));
    }

    private String formatPending(int points) {
        return points <= 0 ? "" : "(+" + points + ")";
    }

    private void refreshSkillsView() {
        if (gameState == null) return;
        GameState.PlayerSkills skills = gameState.getPlayerSkills();

        if (farming != null) farming.setText(appendPendingSkillCost(skills.getFarmingDisplay(), pendingFarmingSkillExp));
        if (gathering != null) gathering.setText(appendPendingSkillCost(skills.getGatheringDisplay(), pendingGatheringSkillExp));
        if (crafting != null) crafting.setText(appendPendingSkillCost(skills.getCraftingDisplay(), pendingCraftingSkillExp));
        if (fishing != null) fishing.setText(appendPendingSkillCost(skills.getFishingDisplay(), pendingFishingSkillExp));
        if (cooking != null) cooking.setText(appendPendingSkillCost(skills.getCookingDisplay(), pendingCookingSkillExp));
    }

    private void refreshStatusButtons() {
        boolean canUsePoint = playerState != null && playerState.getExp() > 0;
        boolean skillsVisible = isSkillsScreenVisible();

        configureImageButton(btnUsePoint, canUsePoint, true, statusAllocationMode || skillAllocationMode ? 1.0 : 0.85);

        if (skillsVisible) {
            configureImageButton(btnConfirm, skillAllocationMode && getPendingSkillExpTotal() > 0, skillAllocationMode, 1.0);

            configureImageButton(btnfarmingplus1, canIncreaseSkill("farming"), skillAllocationMode, 1.0);
            configureImageButton(btngatheringplus1, canIncreaseSkill("gathering"), skillAllocationMode, 1.0);
            configureImageButton(btncraftingplus1, canIncreaseSkill("crafting"), skillAllocationMode, 1.0);
            configureImageButton(btnfishingplus1, canIncreaseSkill("fishing"), skillAllocationMode, 1.0);
            configureImageButton(btncookingplus1, canIncreaseSkill("cooking"), skillAllocationMode, 1.0);

            configureImageButton(btnfarmingminus1, skillAllocationMode && pendingFarmingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btngatheringminus1, skillAllocationMode && pendingGatheringSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btncraftingminus1, skillAllocationMode && pendingCraftingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btnfishingminus1, skillAllocationMode && pendingFishingSkillExp > 0, skillAllocationMode, 1.0);
            configureImageButton(btncookingminus1, skillAllocationMode && pendingCookingSkillExp > 0, skillAllocationMode, 1.0);

            configureImageButton(btnHPplus, false, false, 1.0);
            configureImageButton(btnSPplus, false, false, 1.0);
            configureImageButton(btnSTRplus, false, false, 1.0);
            configureImageButton(btnVITplus, false, false, 1.0);
            configureImageButton(btnDEXplus, false, false, 1.0);
            configureImageButton(btnAGIplus, false, false, 1.0);
            configureImageButton(btnHPminus, false, false, 1.0);
            configureImageButton(btnSPminus, false, false, 1.0);
            configureImageButton(btnSTRminus, false, false, 1.0);
            configureImageButton(btnVITminus, false, false, 1.0);
            configureImageButton(btnDEXminus, false, false, 1.0);
            configureImageButton(btnAGIminus, false, false, 1.0);
            return;
        }

        if (isStatusScreenVisible()) {
            configureImageButton(btnConfirm, statusAllocationMode && getPendingExpCost() > 0, statusAllocationMode, 1.0);

            configureImageButton(btnHPplus, canIncreaseStat("hp"), statusAllocationMode, 1.0);
            configureImageButton(btnSPplus, canIncreaseStat("sp"), statusAllocationMode, 1.0);
            configureImageButton(btnSTRplus, canIncreaseStat("str"), statusAllocationMode, 1.0);
            configureImageButton(btnVITplus, canIncreaseStat("vit"), statusAllocationMode, 1.0);
            configureImageButton(btnDEXplus, canIncreaseStat("dex"), statusAllocationMode, 1.0);
            configureImageButton(btnAGIplus, canIncreaseStat("agi"), statusAllocationMode, 1.0);

            configureImageButton(btnHPminus, statusAllocationMode && pendingHpPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnSPminus, statusAllocationMode && pendingSpPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnSTRminus, statusAllocationMode && pendingStrPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnVITminus, statusAllocationMode && pendingVitPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnDEXminus, statusAllocationMode && pendingDexPoints > 0, statusAllocationMode, 1.0);
            configureImageButton(btnAGIminus, statusAllocationMode && pendingAgiPoints > 0, statusAllocationMode, 1.0);

            configureImageButton(btnfarmingplus1, false, false, 1.0);
            configureImageButton(btngatheringplus1, false, false, 1.0);
            configureImageButton(btncraftingplus1, false, false, 1.0);
            configureImageButton(btnfishingplus1, false, false, 1.0);
            configureImageButton(btncookingplus1, false, false, 1.0);
            configureImageButton(btnfarmingminus1, false, false, 1.0);
            configureImageButton(btngatheringminus1, false, false, 1.0);
            configureImageButton(btncraftingminus1, false, false, 1.0);
            configureImageButton(btnfishingminus1, false, false, 1.0);
            configureImageButton(btncookingminus1, false, false, 1.0);
        }
    }

    private void configureImageButton(ImageView imageView, boolean enabled, boolean visible, double activeOpacity) {
        if (imageView == null) return;
        imageView.setVisible(visible);
        imageView.setManaged(visible);
        imageView.setDisable(!enabled);
        imageView.setMouseTransparent(!visible || !enabled);
        imageView.setOpacity(!visible ? 0.0 : (enabled ? activeOpacity : 0.45));
    }

    @Override
    protected String mapKey() {
        return "left_map";
    }

    @Override
    protected void configureExits() {
       registerExit(exit_left, "/view/home_map.fxml", SpawnTag.FROM_LEFT);
       registerExit(exit_up, "/view/left_map_noone.fxml", SpawnTag.FROM_DOWN);
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
