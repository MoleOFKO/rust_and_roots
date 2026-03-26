package controller;

import db.LoadGameService;
import db.SaveGameService;
import db.dbcon;
import game.GameState;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import util.SceneManager;
import javafx.scene.image.Image;
import java.io.InputStream;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;

public class SaveloadsController implements Initializable {

    @FXML
    private Button btnLoad;
    @FXML
    private Button btnback;

    @FXML
    private Label save1;
    @FXML
    private Button btnsave1;
    @FXML
    private Label save2;
    @FXML
    private Button btnsave2;
    @FXML
    private Label save3;
    @FXML
    private Button btnsave3;
    @FXML
    private Label save4;
    @FXML
    private Button btnsave4;
    @FXML
    private ImageView cimg;
    @FXML
    private Label cname;
    @FXML
    private Label wname;
    @FXML
    private Label day;

    private int selectedSlot = 1;
    @FXML
    private ImageView deletesaveload;
    @FXML
    private Button btndelete;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedSlot = GameState.get().getSelectedSaveSlot();
        refreshModeUi();
        refreshSlotLabels();
        refreshSelectionUi();
        refreshSelectedSlotPreview();
        btnLoad.setText("");
    }

    private void refreshModeUi() {
        if (btnLoad != null) {
            btnLoad.setText(GameState.get().isSaveMenuMode() ? "Save" : "Load");
        }
    }

    private void refreshSelectionUi() {
        styleSlotButton(btnsave1, selectedSlot == 1);
        styleSlotButton(btnsave2, selectedSlot == 2);
        styleSlotButton(btnsave3, selectedSlot == 3);
        styleSlotButton(btnsave4, selectedSlot == 4);
    }

    private void styleSlotButton(Button button, boolean selected) {
        if (button == null) return;
        button.setOpacity(selected ? 1.0 : 0.7);
    }

    private void refreshSlotLabels() {
        int characterId = GameState.get().getCurrentCharacterId();

        if (save1 != null) save1.setText(readSlotLabel(characterId, 1));
        if (save2 != null) save2.setText(readSlotLabel(characterId, 2));
        if (save3 != null) save3.setText(readSlotLabel(characterId, 3));
        if (save4 != null) save4.setText(readSlotLabel(characterId, 4));
    }

    private String readSlotLabel(int characterId, int slotNumber) {
        String slotName = "Save " + slotNumber;
        dbcon db = new dbcon();

        try (Connection con = db.getConnection()) {
            String sql = """
                SELECT saved_name, last_saved_date
                FROM save_game
                WHERE character_id = ? AND saved_name = ?
                LIMIT 1
                """;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, characterId);
                ps.setString(2, slotName);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return slotName + " - " + rs.getTimestamp("last_saved_date");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return slotName + " - Empty";
    }

    @FXML
    private void handlebtnLoadaction(ActionEvent event) {
        try {
            GameState.get().setSelectedSaveSlot(selectedSlot);

            if (GameState.get().isSaveMenuMode()) {
                SaveGameService saveService = new SaveGameService();
                saveService.saveGameToSlot(GameState.get(), selectedSlot);

                refreshSlotLabels();
                refreshSelectedSlotPreview();

                GameState.get().setSaveMenuMode(false);

                String returnFxml = GameState.get().getReturnFxml();
                if (returnFxml == null || returnFxml.isBlank()) {
                    returnFxml = "/view/" + GameState.get().getCurrentMapKey() + ".fxml";
                }
                SceneManager.switchScene(returnFxml);
                return;
            }

            int characterId = GameState.get().getCurrentCharacterId();
            int saveId = findSaveIdForSlot(characterId, selectedSlot);

            if (saveId <= 0) {
                if (day != null) {
                    day.setText("No save found in slot " + selectedSlot);
                }
                return;
            }

            LoadGameService loadService = new LoadGameService();
            boolean ok = loadService.loadGame(saveId);

            if (!ok) {
                if (day != null) {
                    day.setText("Load failed");
                }
                return;
            }

            GameState.get().setSelectedSaveSlot(selectedSlot);
            SceneManager.switchScene("/view/" + GameState.get().getCurrentMapKey() + ".fxml");

        } catch (Exception e) {
            e.printStackTrace();
            if (day != null) {
                day.setText("Error during save/load");
            }
        }
    }

    private int findSaveIdForSlot(int characterId, int slotNumber) {
        String slotName = "Save " + slotNumber;
        dbcon db = new dbcon();

        try (Connection con = db.getConnection()) {
            String sql = "SELECT saved_id FROM save_game WHERE character_id=? AND saved_name=? LIMIT 1";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, characterId);
                ps.setString(2, slotName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("saved_id");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @FXML
    private void handlebackaction(ActionEvent event) throws IOException {
        String returnFxml = GameState.get().getReturnFxml();

        if (returnFxml == null || returnFxml.isBlank()) {
            returnFxml = "/view/main_menu.fxml";
        }

        GameState.get().setSaveMenuMode(false);
        SceneManager.switchScene(returnFxml);
    }

    @FXML
    private void handlebtnsave1action(ActionEvent event) {
        selectedSlot = 1;
        GameState.get().setSelectedSaveSlot(1);
        refreshSelectionUi();
        refreshSelectedSlotPreview();
    }

    @FXML
    private void handlebtnsave2action(ActionEvent event) {
        selectedSlot = 2;
        GameState.get().setSelectedSaveSlot(2);
        refreshSelectionUi();
        refreshSelectedSlotPreview();
    }

    @FXML
    private void handlebtnsave3action(ActionEvent event) {
        selectedSlot = 3;
        GameState.get().setSelectedSaveSlot(3);
        refreshSelectionUi();
        refreshSelectedSlotPreview();
    }

    @FXML
    private void handlebtnsave4action(ActionEvent event) {
        selectedSlot = 4;
        GameState.get().setSelectedSaveSlot(4);
        refreshSelectionUi();
        refreshSelectedSlotPreview();
    }
    
    private void refreshSelectedSlotPreview() {
        int characterId = GameState.get().getCurrentCharacterId();
        SlotPreview preview = loadSlotPreview(characterId, selectedSlot);

        if (preview == null) {
            if (cimg != null) cimg.setImage(null);
            if (cname != null) cname.setText("Empty");
            if (wname != null) wname.setText("No save data");
            if (day != null) day.setText("Day -");
            return;
        }

        if (cname != null) cname.setText(preview.playerName == null || preview.playerName.isBlank()
                ? "Unknown Player"
                : preview.playerName);

        if (wname != null) wname.setText(preview.worldName == null || preview.worldName.isBlank()
                ? preview.slotName
                : preview.worldName);
        
        if (day != null) day.setText("Day " + preview.dayNumber);

        if (cimg != null) {
            cimg.setImage(loadCharacterPreviewImage(preview.skinId));
        }
    }

    @FXML
    private void handlesaveloaddeleteaction(MouseEvent event) {
    }

    @FXML
    private void handledeleteaction(ActionEvent event) {
    }

    private static class SlotPreview {
        String slotName;
        String playerName;
        String worldName;
        int skinId;
        int dayNumber;
    }

    private SlotPreview loadSlotPreview(int characterId, int slotNumber) {
        String slotName = "Save " + slotNumber;
        dbcon db = new dbcon();

        try (Connection con = db.getConnection()) {
            String sql = """
                SELECT sg.saved_name,
                       sg.world_name,
                       c.player_name,
                       c.skin_id,
                       st.day_number
                FROM save_game sg
                JOIN characters c ON sg.character_id = c.character_id
                LEFT JOIN saved_time st ON sg.saved_id = st.saved_id
                WHERE sg.character_id = ? AND sg.saved_name = ?
                LIMIT 1
                """;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, characterId);
                ps.setString(2, slotName);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    SlotPreview preview = new SlotPreview();
                    preview.slotName = rs.getString("saved_name");
                    preview.worldName = rs.getString("world_name");
                    preview.playerName = rs.getString("player_name");
                    
                    preview.skinId = rs.getInt("skin_id");
                    if (rs.wasNull()) {
                        preview.skinId = -1;
                    }
                    preview.dayNumber = rs.getInt("day_number");
                    if (rs.wasNull() || preview.dayNumber <= 0) {
                        preview.dayNumber = 1;
                    }
                    return preview;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Image loadCharacterPreviewImage(int skinId) {
        String[] candidates;

        switch (skinId) {
            case 1 -> candidates = new String[] {
                "/image/character_to_show.png"
//                "/image/character/skin1.png",
//                "/image/human/human_Idle.png"
            };
            case 2 -> candidates = new String[] {
                "/image/character_to_show.png"
//                "/image/human/human_Idle.png"
            };
            case 3 -> candidates = new String[] {
                "/image/character_to_show.png"
//                "/image/human/human_Idle.png"
            };
            default -> candidates = new String[] {
                "/image/character_to_show.png"
//                "/image/human/human_Idle.png"
            };
        }

        for (String path : candidates) {
            try (InputStream is = getClass().getResourceAsStream(path)) {
                if (is != null) {
                    return new Image(is);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}