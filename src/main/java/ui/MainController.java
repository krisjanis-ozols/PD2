package ui;

import db.DB;
import db.JSONImporter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class MainController {

    @FXML private Button chooseJsonButton;
    @FXML private TextField JSONPathTextField;
    @FXML private TextArea JSONLogArea;

    @FXML
    public void onChooseJSONFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select JSON Folder");

        File selectedDir = chooser.showDialog(null);
        if (selectedDir == null) return;

        JSONPathTextField.setText(selectedDir.getAbsolutePath());
        log("Chosen folder: " + selectedDir.getAbsolutePath());
    }

    @FXML
    public void loadJSON() {
        try{
            DB db = new DB();
            JSONLogArea.clear();
            JSONImporter.importJSON(JSONPathTextField.getText(), db, this::log);
            db.close();
        } catch (Exception e) {
            log("Problēma lādējot datus: " + e.getMessage());
        }
    }

    @FXML
    public void clearJSON() {
        JSONLogArea.clear();
        try{
            DB db = new DB();
            db.clear();
            log("Dati izdzēsti");
            db.close();
        } catch (Exception e) {
            log("Neizdevās izdzēst: " + e.getMessage());
        }
    }

    @FXML
    private TournamentController tournamentTabController;

    @FXML
    public void onTournamentTabSelected() {
        tournamentTabController.refreshTournamentTable();
    }

    @FXML
    private TopPlayerGoalsController topPlayersTabController;

    @FXML
    public void onPlayerGoalsTabSelected() {
        topPlayersTabController.refreshTopPlayers();
    }

    @FXML
    private MostActivePlayersController mostActiveTabController;

    @FXML
    public void onMostActiveTabSelected() {
        if (mostActiveTabController != null) {
            mostActiveTabController.refreshMostActivePlayers();
        }
    }

    private void log(String msg) {
        JSONLogArea.appendText(msg + "\n");
        JSONLogArea.positionCaret(JSONLogArea.getText().length());
    }
}
