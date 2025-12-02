package ui;

import db.DB;
import db.TopPlayerRow;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TopPlayerGoalsController {

    @FXML private TableView<TopPlayerRow> topPlayersTable;

    @FXML private TableColumn<TopPlayerRow, Integer> colPlace;
    @FXML private TableColumn<TopPlayerRow, String>  colFirstName;
    @FXML private TableColumn<TopPlayerRow, String>  colLastName;
    @FXML private TableColumn<TopPlayerRow, String>  colTeamP;
    @FXML private TableColumn<TopPlayerRow, Integer> colGoals;
    @FXML private TableColumn<TopPlayerRow, Integer> colAssists;

    @FXML
    private void initialize() {
        topPlayersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colPlace.setCellValueFactory(new PropertyValueFactory<>("rank"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colTeamP.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        colGoals.setCellValueFactory(new PropertyValueFactory<>("goals"));
        colAssists.setCellValueFactory(new PropertyValueFactory<>("assists"));

        refreshTopPlayers();
    }

    public void refreshTopPlayers() {
        try{
            DB db = new DB();
            List<TopPlayerRow> rows = db.getTopPlayers();

            int rank = 1;
            for (TopPlayerRow r : rows) {
                r.setRank(rank++);
            }

            topPlayersTable.getItems().setAll(rows);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
