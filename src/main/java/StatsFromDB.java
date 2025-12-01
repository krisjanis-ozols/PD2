import java.sql.SQLException;
import java.util.List;

public class StatsFromDB {
    public static void printTournamentTable(DB db){
        try {
            List<TournamentRow> table = db.getTournamentTable();
            int place = 1;
            for (TournamentRow row : table) {
                System.out.printf(
                        "%2d. %-20s Pts:%2d  W:%d  L:%d  W_OT:%d  L_OT:%d  GF:%d  GA:%d%n",
                        place++,
                        row.getName(),
                        row.getPoints(),
                        row.getWinsReg(),
                        row.getLossesReg(),
                        row.getWinsOt(),
                        row.getLossesOt(),
                        row.getGoalsFor(),
                        row.getGoalsAgainst()
                );
            }
        }catch(SQLException e){
            System.out.println("problem reading tournament table");
        }
    }
    public static void printTopPlayers(DB db) {
        try {
            List<TopPlayerRow> players = db.getTopPlayers();
            int place = 1;
            for (TopPlayerRow p : players) {
                System.out.printf(
                        "%2d. %-12s %-15s %-20s G:%2d  A:%2d%n",
                        place++,
                        p.getFirstName(),
                        p.getLastName(),
                        p.getTeamName(),
                        p.getGoals(),
                        p.getAssists()
                );
            }
        } catch (SQLException e) {
            System.out.println("problem getting top players: " + e.getMessage());
        }
    }
}