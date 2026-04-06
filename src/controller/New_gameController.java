/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import db.dbcon;
import game.GameState;
import game.GameTimeManager;
import game.SceneController;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javax.swing.JOptionPane;
import util.AppDialogs;
import model.PlayerState;
import util.SceneManager;
import util.WorldScaler;

/**
 * FXML Controller class
 *
 * @author Shane Khant Ko
 */
public class New_gameController implements Initializable, SceneController {

    private static final int NEW_GAME_BASE_HP = 45;
    private static final int NEW_GAME_BASE_STAMINA = 50;
    private static final int NEW_GAME_BASE_STR = 1;
    private static final int NEW_GAME_BASE_VIT = 1;
    private static final int NEW_GAME_BASE_DEX = 1;
    private static final int NEW_GAME_BASE_AGI = 1;
    private static final int NEW_GAME_STARTING_EXP = 100;

    @FXML
    private TextField txtchaname;
    @FXML
    private Label lblstr;
    @FXML
    private Label lblagi;
    @FXML
    private Label lblvit;
    @FXML
    private Button btnStartGame;
    @FXML
    private ImageView char_img;
    @FXML
    private Button btnback;
    @FXML
    private Label lbldex;
    @FXML
    private Label lblhp;
    @FXML
    private Label lblstamina;
    @FXML
    private TextField txtworldname;
    @FXML 
    private StackPane view;
    @FXML 
    private Group uiRoot;

    Parent root;
    public static String char_name;
    public static String world_name;
    public static int str;
    public static int agi;
    public static int vit;
    public static int dex;
    public static int hp;
    public static int stamina;
    @FXML
    private ImageView bgImage;
    @FXML
    private Label lblexp;
    @FXML
    private ImageView btnHPplus;
    @FXML
    private ImageView btnHPminus;
    @FXML
    private ImageView btnSTRplus;
    @FXML
    private ImageView btnSTRminus;
    @FXML
    private ImageView btnVITplus;
    @FXML
    private ImageView btnVITminus;
    @FXML
    private ImageView btnSPplus;
    @FXML
    private ImageView btnSPminus;
    @FXML
    private ImageView btnDEXplus;
    @FXML
    private ImageView btnDEXminus;
    @FXML
    private ImageView btnAGIplus;
    @FXML
    private ImageView btnAGIminus;
    @FXML
    private ImageView btnConfirm;
    @FXML
    private ImageView btnClear;

    private PlayerState playerState;
    private int pendingHpPoints;
    private int pendingSpPoints;
    private int pendingStrPoints;
    private int pendingVitPoints;
    private int pendingDexPoints;
    private int pendingAgiPoints;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new WorldScaler(view, uiRoot);
        playerState = GameState.get().getPlayerState();
        resetPlayerStateToNewGameDefaults();
        refreshStatusView();
    }

    @Override
    public void init(GameState gameState) {
        playerState = gameState.getPlayerState();
        refreshStatusView();
    }

    @FXML
    private void handlebtnStartGame(ActionEvent event) {
        String characterName = txtchaname == null ? "" : txtchaname.getText().trim();
        String worldName = txtworldname == null ? "" : txtworldname.getText().trim();

        if (characterName.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter Character Name!");
            return;
        }

        if (worldName.isEmpty()) {
            AppDialogs.showMessageDialog(null, "Please Enter World Name!");
            return;
        }

        int confirm = AppDialogs.showConfirmDialog(
            null,
            "World Name: \"" + worldName + "\"\nThis name will be shown as the Save Name.\nDo you want to continue?",
            "Confirm New Game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        handleconfirmaction(null);

        GameState gs = GameState.get();
        gs.resetForNewGame();
        gs.setCurrentPlayerName(characterName);
        gs.setCurrentWorldName(worldName);

        if (playerState != null) {
            playerState.setPlayerName(characterName);
            playerState.setHp(playerState.getMaxHp());
            playerState.setStamina(playerState.getMaxStamina());
        }

        int accountId = gs.getCurrentAccountId();
        if (accountId <= 0) {
            AppDialogs.showMessageDialog(null, "No logged-in account found. Please log in first.");
            return;
        }

        try {
            int characterId = createCharacterIfMissing(accountId, characterName);
            gs.setCurrentCharacterId(characterId);
            gs.setCurrentSaveId(-1);
            gs.setCurrentMapKey("home_map");
            gs.setSelectedSaveSlot(1);
        } catch (Exception e) {
            e.printStackTrace();
            AppDialogs.showMessageDialog(null, "Failed to create character.");
            return;
        }

        char_name = characterName;
        world_name = worldName;
        str = playerState.getStr();
        agi = playerState.getAgi();
        vit = playerState.getVit();
        dex = playerState.getDex();
        hp = playerState.getMaxHp();
        stamina = playerState.getMaxStamina();

        GameTimeManager time = GameTimeManager.get();
        time.setDay(1);
        time.setTimeOfDay(6, 0);
        SceneManager.switchScene("/view/home_map.fxml");
    }

    @FXML
    private void handlebackaction(ActionEvent event) {
        SceneManager.switchScene("/view/main_menu.fxml");
    }

    @FXML
    private void handleHPplusaction(MouseEvent event) { changePendingStat("hp", 1); }
    @FXML
    private void handleHPminusaction(MouseEvent event) { changePendingStat("hp", -1); }
    @FXML
    private void handleSTRplusaction(MouseEvent event) { changePendingStat("str", 1); }
    @FXML
    private void handleSTRminusaction(MouseEvent event) { changePendingStat("str", -1); }
    @FXML
    private void handleVITplusaction(MouseEvent event) { changePendingStat("vit", 1); }
    @FXML
    private void handleVITminusaction(MouseEvent event) { changePendingStat("vit", -1); }
    @FXML
    private void handleSPplusaction(MouseEvent event) { changePendingStat("sp", 1); }
    @FXML
    private void handleSPminusaction(MouseEvent event) { changePendingStat("sp", -1); }
    @FXML
    private void handleDEXplusaction(MouseEvent event) { changePendingStat("dex", 1); }
    @FXML
    private void handleDEXminusaction(MouseEvent event) { changePendingStat("dex", -1); }
    @FXML
    private void handleAGIplusaction(MouseEvent event) { changePendingStat("agi", 1); }
    @FXML
    private void handleAGIminusaction(MouseEvent event) { changePendingStat("agi", -1); }
    @FXML
    private void handleconfirmaction(MouseEvent event) { applyPendingStatusChanges(); }
    @FXML
    private void handleclearaction(MouseEvent event) {
        resetPlayerStateToNewGameDefaults();
        refreshStatusView();
    }

    private void resetPlayerStateToNewGameDefaults() {
        if (playerState == null) return;

        playerState.setMaxHp(NEW_GAME_BASE_HP);
        playerState.setHp(NEW_GAME_BASE_HP);
        playerState.setMaxStamina(NEW_GAME_BASE_STAMINA);
        playerState.setStamina(NEW_GAME_BASE_STAMINA);
        playerState.setStr(NEW_GAME_BASE_STR);
        playerState.setVit(NEW_GAME_BASE_VIT);
        playerState.setDex(NEW_GAME_BASE_DEX);
        playerState.setAgi(NEW_GAME_BASE_AGI);
        GameState.get().getPlayerSkills().clear();
        playerState.setExp(NEW_GAME_STARTING_EXP);

        resetPendingStatusChanges();
    }

    private void resetPendingStatusChanges() {
        pendingHpPoints = 0;
        pendingSpPoints = 0;
        pendingStrPoints = 0;
        pendingVitPoints = 0;
        pendingDexPoints = 0;
        pendingAgiPoints = 0;
    }

    private int getPendingPointCount(String statKey) {
        return switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> pendingHpPoints;
            case "sp" -> pendingSpPoints;
            case "str" -> pendingStrPoints;
            case "vit" -> pendingVitPoints;
            case "dex" -> pendingDexPoints;
            case "agi" -> pendingAgiPoints;
            default -> 0;
        };
    }

    private void setPendingPointCount(String statKey, int value) {
        int nextValue = Math.max(0, value);
        switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> pendingHpPoints = nextValue;
            case "sp" -> pendingSpPoints = nextValue;
            case "str" -> pendingStrPoints = nextValue;
            case "vit" -> pendingVitPoints = nextValue;
            case "dex" -> pendingDexPoints = nextValue;
            case "agi" -> pendingAgiPoints = nextValue;
            default -> {
                return;
            }
        }
    }

    private int getCommittedStatIncreases(String statKey) {
        if (playerState == null) return 0;

        return switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> Math.max(0, playerState.getMaxHp() - NEW_GAME_BASE_HP);
            case "sp" -> Math.max(0, playerState.getMaxStamina() - NEW_GAME_BASE_STAMINA);
            case "str" -> Math.max(0, playerState.getStr() - NEW_GAME_BASE_STR);
            case "vit" -> Math.max(0, playerState.getVit() - NEW_GAME_BASE_VIT);
            case "dex" -> Math.max(0, playerState.getDex() - NEW_GAME_BASE_DEX);
            case "agi" -> Math.max(0, playerState.getAgi() - NEW_GAME_BASE_AGI);
            default -> 0;
        };
    }

    private int getNextPointCost(String statKey) {
        int nextPointIndex = getCommittedStatIncreases(statKey) + getPendingPointCount(statKey);
        return 1 + Math.max(0, nextPointIndex / 10);
    }

    private int getPendingExpCostForStat(String statKey) {
        int committed = getCommittedStatIncreases(statKey);
        int pending = getPendingPointCount(statKey);
        int total = 0;

        for (int i = 0; i < pending; i++) {
            total += 1 + ((committed + i) / 10);
        }

        return total;
    }

    private int getPendingExpCost() {
        return getPendingExpCostForStat("hp")
            + getPendingExpCostForStat("sp")
            + getPendingExpCostForStat("str")
            + getPendingExpCostForStat("vit")
            + getPendingExpCostForStat("dex")
            + getPendingExpCostForStat("agi");
    }

    private int getPreviewRemainingExp() {
        if (playerState == null) return 0;
        return Math.max(0, playerState.getExp() - getPendingExpCost());
    }

    private boolean canIncreaseStat(String statKey) {
        return playerState != null
                && !isStatAtCurrentCap(statKey)
                && getPreviewRemainingExp() >= getNextPointCost(statKey);
    }

    private int getCurrentCoreStatCap() {
        return 5;
    }

    private int getCurrentStatValueWithPending(String statKey) {
        if (playerState == null) return 0;

        return switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> playerState.getMaxHp() + pendingHpPoints;
            case "sp" -> playerState.getMaxStamina() + pendingSpPoints;
            case "str" -> playerState.getStr() + pendingStrPoints;
            case "vit" -> playerState.getVit() + pendingVitPoints;
            case "dex" -> playerState.getDex() + pendingDexPoints;
            case "agi" -> playerState.getAgi() + pendingAgiPoints;
            default -> 0;
        };
    }

    private int getAverageCoreStatWithPending() {
        if (playerState == null) return 0;
        int total =
            (playerState.getStr() + pendingStrPoints) +
            (playerState.getVit() + pendingVitPoints) +
            (playerState.getDex() + pendingDexPoints) +
            (playerState.getAgi() + pendingAgiPoints);
        return Math.max(0, total / 4);
    }

    private int getHpSpPointCap() {
        return getAverageCoreStatWithPending() + getCurrentCoreStatCap();
    }

    private int getHpSpInvestedPointsWithPending(String statKey) {
        if (playerState == null) return 0;

        return switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> Math.max(0, (playerState.getMaxHp() - NEW_GAME_BASE_HP) + pendingHpPoints);
            case "sp" -> Math.max(0, (playerState.getMaxStamina() - NEW_GAME_BASE_STAMINA) + pendingSpPoints);
            default -> 0;
        };
    }

    private boolean isStatAtCurrentCap(String statKey) {
        int coreCap = getCurrentCoreStatCap();
        int hpSpCap = getHpSpPointCap();

        return switch (Objects.requireNonNullElse(statKey, "")) {
            case "hp" -> getHpSpInvestedPointsWithPending("hp") >= hpSpCap;
            case "sp" -> getHpSpInvestedPointsWithPending("sp") >= hpSpCap;
            case "str" -> getCurrentStatValueWithPending("str") >= coreCap;
            case "vit" -> getCurrentStatValueWithPending("vit") >= coreCap;
            case "dex" -> getCurrentStatValueWithPending("dex") >= coreCap;
            case "agi" -> getCurrentStatValueWithPending("agi") >= coreCap;
            default -> false;
        };
    }

    private void changePendingStat(String statKey, int delta) {
        if (playerState == null || delta == 0) return;

        int currentPending = getPendingPointCount(statKey);

        if (delta < 0) {
            if (currentPending <= 0) return;
            setPendingPointCount(statKey, currentPending - 1);
            refreshStatusView();
            return;
        }

        if (!canIncreaseStat(statKey)) return;

        setPendingPointCount(statKey, currentPending + 1);
        refreshStatusView();
    }

    private void applyPendingStatusChanges() {
        if (playerState == null) return;

        int spentExp = getPendingExpCost();
        if (spentExp <= 0) return;
        if (playerState.getExp() < spentExp) {
            refreshStatusView();
            return;
        }

        playerState.setExp(playerState.getExp() - spentExp);

        if (pendingHpPoints > 0) {
            playerState.setMaxHp(playerState.getMaxHp() + pendingHpPoints);
            playerState.setHp(playerState.getMaxHp());
        }
        if (pendingSpPoints > 0) {
            playerState.setMaxStamina(playerState.getMaxStamina() + pendingSpPoints);
            playerState.setStamina(playerState.getMaxStamina());
        }
        if (pendingStrPoints > 0) playerState.setStr(playerState.getStr() + pendingStrPoints);
        if (pendingVitPoints > 0) playerState.setVit(playerState.getVit() + pendingVitPoints);
        if (pendingDexPoints > 0) playerState.setDex(playerState.getDex() + pendingDexPoints);
        if (pendingAgiPoints > 0) playerState.setAgi(playerState.getAgi() + pendingAgiPoints);

        resetPendingStatusChanges();
        refreshStatusView();
    }

    private void refreshStatusView() {
        if (playerState == null) return;

        int pendingExpCost = getPendingExpCost();

        if (lblhp != null) lblhp.setText(String.valueOf(playerState.getMaxHp() + pendingHpPoints));
        if (lblstamina != null) lblstamina.setText(String.valueOf(playerState.getMaxStamina() + pendingSpPoints));
        if (lblstr != null) lblstr.setText(String.valueOf(playerState.getStr() + pendingStrPoints));
        if (lblvit != null) lblvit.setText(String.valueOf(playerState.getVit() + pendingVitPoints));
        if (lbldex != null) lbldex.setText(String.valueOf(playerState.getDex() + pendingDexPoints));
        if (lblagi != null) lblagi.setText(String.valueOf(playerState.getAgi() + pendingAgiPoints));
        if (lblexp != null) lblexp.setText(String.valueOf(Math.max(0, playerState.getExp() - pendingExpCost)));

        refreshButtons();
    }

    private void refreshButtons() {
        configureImageButton(btnHPplus, canIncreaseStat("hp"), 1.0);
        configureImageButton(btnSPplus, canIncreaseStat("sp"), 1.0);
        configureImageButton(btnSTRplus, canIncreaseStat("str"), 1.0);
        configureImageButton(btnVITplus, canIncreaseStat("vit"), 1.0);
        configureImageButton(btnDEXplus, canIncreaseStat("dex"), 1.0);
        configureImageButton(btnAGIplus, canIncreaseStat("agi"), 1.0);

        configureImageButton(btnHPminus, pendingHpPoints > 0, 1.0);
        configureImageButton(btnSPminus, pendingSpPoints > 0, 1.0);
        configureImageButton(btnSTRminus, pendingStrPoints > 0, 1.0);
        configureImageButton(btnVITminus, pendingVitPoints > 0, 1.0);
        configureImageButton(btnDEXminus, pendingDexPoints > 0, 1.0);
        configureImageButton(btnAGIminus, pendingAgiPoints > 0, 1.0);

        configureImageButton(btnConfirm, getPendingExpCost() > 0, 1.0);
        configureImageButton(btnClear, true, 1.0);
    }

    private void configureImageButton(ImageView view, boolean enabled, double activeOpacity) {
        if (view == null) return;
        view.setDisable(!enabled);
        view.setOpacity(enabled ? activeOpacity : 0.35);
    }

    private int createCharacterIfMissing(int accountId, String playerName) throws Exception {
        GameState gs = GameState.get();
        if (gs.getCurrentCharacterId() > 0) {
            return gs.getCurrentCharacterId();
        }

        dbcon db = new dbcon();
        try (Connection con = db.getConnection()) {
            String sql = "INSERT INTO characters (account_id, player_name, skin_id) VALUES (?, ?, NULL)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, accountId);
                ps.setString(2, playerName);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new IllegalStateException("Failed to create character row.");
                    }
                    return rs.getInt(1);
                }
            }
        }
    }
}
