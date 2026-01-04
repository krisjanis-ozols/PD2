package ui;

import db.DB;
import db.MostActivePlayerRow;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MostActivePlayersController {

    @FXML private TableView<MostActivePlayerRow> mostActiveTable;

    @FXML private TableColumn<MostActivePlayerRow, Integer> colPlace;
    @FXML private TableColumn<MostActivePlayerRow, String>  colFirstName;
    @FXML private TableColumn<MostActivePlayerRow, String>  colLastName;
    @FXML private TableColumn<MostActivePlayerRow, String>  colTeam;
    @FXML private TableColumn<MostActivePlayerRow, String>  colTime;

    @FXML
    private void initialize() {
        mostActiveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colPlace.setCellValueFactory(new PropertyValueFactory<>("rank"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colTeam.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));

        refreshMostActivePlayers();
    }

    public void refreshMostActivePlayers() {
        try {
            DB db = new DB();
            List<MostActivePlayerRow> rows = db.getMostActivePlayersTop10();

            int place = 1;
            for (MostActivePlayerRow r : rows) {
                r.setRank(place++);
            }

            mostActiveTable.getItems().setAll(rows);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
