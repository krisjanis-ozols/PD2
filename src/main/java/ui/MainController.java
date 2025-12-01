package ui;

import db.JSONImporter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import db.DB;

import java.io.File;

public class MainController {

    @FXML
    private Button chooseJsonButton;

    @FXML
    private TextField JSONPathTextField;

    @FXML
    private TextArea JSONLogArea;

    @FXML
    public void onChooseJSONFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select JSON Folder");

        File selectedDir = chooser.showDialog(null);
        if (selectedDir == null) {
            System.out.println("User canceled folder selection.");
            return;
        }
        System.out.println("Chosen folder: " + selectedDir.getAbsolutePath());
        JSONPathTextField.setText(selectedDir.getAbsolutePath());
    }
    @FXML
    public void loadJSON() {
        try {
            DB db = new DB();
            JSONLogArea.clear();
            JSONImporter.importJSON(JSONPathTextField.getText(), db, msg -> log(msg));
            db.close();
        }catch(Exception e){
            log("Problēma lādējot datus");
        }
    }
    @FXML
    public void clearJSON(){
        JSONLogArea.clear();
        try {
            DB db = new DB();
            db.clear();
            db.close();
            log("Dati izdzēsti");
        }catch(Exception e){
            log("Neizdevās izdzēst");
        }
    }

    private void log(String msg) {
        JSONLogArea.appendText(msg + "\n");
        JSONLogArea.positionCaret(JSONLogArea.getText().length());
    }

}