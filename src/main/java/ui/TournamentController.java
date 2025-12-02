package ui;

import db.DB;
import db.TournamentRow;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TournamentController {

    @FXML private TableView<TournamentRow> tournamentTable;

    @FXML private TableColumn<TournamentRow, Integer> colRank;
    @FXML private TableColumn<TournamentRow, String>  colTeam;
    @FXML private TableColumn<TournamentRow, Integer> colPoints;
    @FXML private TableColumn<TournamentRow, Integer> colWinsReg;
    @FXML private TableColumn<TournamentRow, Integer> colLossesReg;
    @FXML private TableColumn<TournamentRow, Integer> colWinsOt;
    @FXML private TableColumn<TournamentRow, Integer> colLossesOt;
    @FXML private TableColumn<TournamentRow, Integer> colGoalsFor;
    @FXML private TableColumn<TournamentRow, Integer> colGoalsAgainst;
    @FXML private TableColumn<TournamentRow, Integer> colGoalDiff;

    @FXML
    private void initialize() {
        tournamentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colRank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        colTeam.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        colWinsReg.setCellValueFactory(new PropertyValueFactory<>("winsReg"));
        colLossesReg.setCellValueFactory(new PropertyValueFactory<>("lossesReg"));
        colWinsOt.setCellValueFactory(new PropertyValueFactory<>("winsOt"));
        colLossesOt.setCellValueFactory(new PropertyValueFactory<>("lossesOt"));
        colGoalsFor.setCellValueFactory(new PropertyValueFactory<>("goalsFor"));
        colGoalsAgainst.setCellValueFactory(new PropertyValueFactory<>("goalsAgainst"));
        colGoalDiff.setCellValueFactory(new PropertyValueFactory<>("goalsDifference"));


        refreshTournamentTable();
    }


    public void refreshTournamentTable() {
        try {
            DB db = new DB();
            List<TournamentRow> rows = db.getTournamentTable();

            for (int i = 0; i < rows.size(); i++) {
                rows.get(i).setRank(i + 1);
            }

            tournamentTable.getItems().setAll(rows);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
