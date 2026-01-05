package ui;

import db.DB;
import db.TeamDisciplineRow;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TeamDisciplineController {

    @FXML private TableView<TeamDisciplineRow> disciplineTable;

    @FXML private TableColumn<TeamDisciplineRow, Integer> colRank;
    @FXML private TableColumn<TeamDisciplineRow, String> colTeam;
    @FXML private TableColumn<TeamDisciplineRow, Integer> colY;
    @FXML private TableColumn<TeamDisciplineRow, Integer> colR;
    @FXML private TableColumn<TeamDisciplineRow, Integer> colTotal;

    @FXML
    private void initialize() {
        disciplineTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colRank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        colTeam.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        colY.setCellValueFactory(new PropertyValueFactory<>("yellows"));
        colR.setCellValueFactory(new PropertyValueFactory<>("reds"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        refreshTeamDiscipline();
    }

    public void refreshTeamDiscipline() {
        try {
            DB db = new DB();
            List<TeamDisciplineRow> rows = db.getTeamDisciplineTop10();

            int place = 1;
            for (TeamDisciplineRow r : rows) {
                r.setRank(place++);
            }

            disciplineTable.getItems().setAll(rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}