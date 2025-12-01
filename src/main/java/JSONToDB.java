import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONToDB {
    private final DB db;

    public JSONToDB(DB db) {
        this.db = db;
    }

    public void importMatch(JSONObject root) throws SQLException {
        JSONObject spele = root.getObject("Spele");

        String date = spele.getString("Laiks");
        String venue = spele.getString("Vieta");
        int spectators = spele.getInt("Skatitaji");

        JSONObject[] komandas = spele.getArrayOrObject("Komanda");
        if (komandas.length != 2) {
            throw new IllegalStateException("Expected exactly 2 teams");
        }

        JSONObject komanda1 = komandas[0];
        JSONObject komanda2 = komandas[1];

        String team1Name = komanda1.getString("Nosaukums");
        String team2Name = komanda2.getString("Nosaukums");

        int team1Id = db.onlyGetTeamId(team1Name);
        int team2Id = db.onlyGetTeamId(team2Name);
        if(db.checkIfMatchAdded(date, team1Id, team2Id)){
            System.out.println("Match already added: skipping");
            return;
        }

        team1Id = db.teamId(team1Name);
        team2Id = db.teamId(team2Name);


        int matchId = db.matchId(date, venue, spectators, team1Id, team2Id);

        importPlayersForTeam(komanda1, team1Id, matchId);
        importPlayersForTeam(komanda2, team2Id, matchId);

        importGoalsForTeam(komanda1, matchId, team1Id);
        importGoalsForTeam(komanda2, matchId, team2Id);

        updateMatchGoals(matchId, team1Id, team2Id);
        updateMatchDecidedInOT(matchId);

        importCardsForTeam(komanda1, matchId, team1Id);
        importCardsForTeam(komanda2, matchId, team2Id);

        importSubstitutionsForTeam(komanda1, matchId, team1Id);
        importSubstitutionsForTeam(komanda2, matchId, team2Id);

        importReferees(spele, matchId);

        int matchDurationSeconds = computeMatchDuration(spele);
        updateTeamSeconds(matchId, team1Id, team2Id, matchDurationSeconds);
    }

    private void importPlayersForTeam(JSONObject komanda, int teamId, int matchId) throws SQLException {
        Map<Integer, Integer> numberToPlayerId = new HashMap<>();

        try {
            JSONObject speletajiNode = komanda.getObject("Speletaji");
            JSONObject[] speletaji = speletajiNode.getArrayOrObject("Speletajs");
            for (JSONObject playerObj : speletaji) {
                int number = playerObj.getInt("Nr");
                String firstName = playerObj.getString("Vards");
                String lastName = playerObj.getString("Uzvards");
                String role = playerObj.getString("Loma");
                int playerId = db.playerId(teamId, number, firstName, lastName, role);
                numberToPlayerId.put(number, playerId);
            }
        } catch (IllegalArgumentException e) {
            return;
        }

        Map<Integer, Boolean> isStarter = new HashMap<>();
        try {
            JSONObject pnode = komanda.getObject("Pamatsastavs");
            JSONObject[] starters = pnode.getArrayOrObject("Speletajs");
            for (JSONObject p : starters) {
                int num = p.getInt("Nr");
                isStarter.put(num, true);
            }
        } catch (IllegalArgumentException e) {
        }

        for (Map.Entry<Integer, Integer> entry : numberToPlayerId.entrySet()) {
            int number = entry.getKey();
            int playerId = entry.getValue();
            boolean starter = isStarter.getOrDefault(number, false);
            db.matchPlayerId(matchId, playerId, starter);   // seconds_played = null
        }
    }

    private void importGoalsForTeam(JSONObject komanda, int matchId, int teamId) throws SQLException {
        JSONObject vartiNode;
        try {
            vartiNode = komanda.getObject("Varti");
        } catch (IllegalArgumentException e) {
            return;
        }

        JSONObject[] vgArray;
        try {
            vgArray = vartiNode.getArrayOrObject("VG");
        } catch (IllegalArgumentException e) {
            return;
        }

        for (JSONObject vg : vgArray) {
            int scorerNumber = vg.getInt("Nr");
            String timeString = vg.getString("Laiks");
            String sitiens = vg.getString("Sitiens");
            boolean isPenalty = "J".equalsIgnoreCase(sitiens);

            int seconds = timeToSeconds(timeString);
            int scorerId = playerIdFromNumber(teamId, scorerNumber);
            int goalId = db.goalId(matchId, teamId, scorerId, seconds, isPenalty);

            try {
                JSONObject[] assists = vg.getArrayOrObject("P");
                int order = 1;
                for (JSONObject assistObj : assists) {
                    int assistNumber = assistObj.getInt("Nr");
                    int assistPlayerId = playerIdFromNumber(teamId, assistNumber);
                    db.assistId(goalId, assistPlayerId, order);
                    order++;
                }
            } catch (IllegalArgumentException e) {}
        }
    }

    private void importCardsForTeam(JSONObject komanda, int matchId, int teamId) throws SQLException {
        JSONObject sodiNode;
        try {
            sodiNode = komanda.getObject("Sodi");
        } catch (IllegalArgumentException e) {
            return;
        }

        JSONObject[] sodi;
        try {
            sodi = sodiNode.getArrayOrObject("Sods");
        } catch (IllegalArgumentException e) {
            return;
        }

        Map<Integer, Integer> foulCount = new HashMap<>();

        for (JSONObject sods : sodi) {
            if (sods == null) return;

            int number = sods.getInt("Nr");
            String timeString = sods.getString("Laiks");
            int seconds = timeToSeconds(timeString);
            int playerId = playerIdFromNumber(teamId, number);

            int count = foulCount.getOrDefault(playerId, 0) + 1;
            foulCount.put(playerId, count);

            String cardType;
            if (count == 1) cardType = "Y";
            else if (count == 2) cardType = "R";
            else continue;

            db.cardId(matchId, teamId, playerId, seconds, cardType);
        }
    }

    private void importSubstitutionsForTeam(JSONObject komanda, int matchId, int teamId) throws SQLException {
        JSONObject mainasNode;
        try {
            mainasNode = komanda.getObject("Mainas");
        } catch (IllegalArgumentException e) {
            return;
        }

        JSONObject[] mainas;
        try {
            mainas = mainasNode.getArrayOrObject("Maina");
        } catch (IllegalArgumentException e) {
            return;
        }

        for (JSONObject maina : mainas) {
            if (maina == null) return;

            String timeString = maina.getString("Laiks");
            int seconds = timeToSeconds(timeString);

            int nrOut = maina.getInt("Nr1");
            int nrIn = maina.getInt("Nr2");

            int playerOutId = playerIdFromNumber(teamId, nrOut);
            int playerInId = playerIdFromNumber(teamId, nrIn);

            db.substitutionId(matchId, teamId, seconds, playerOutId, playerInId);
        }
    }

    private void importReferees(JSONObject spele, int matchId) throws SQLException {
        try {
            JSONObject vt = spele.getObject("VT");
            String lastName = vt.getString("Uzvards");
            String firstName = vt.getString("Vards");
            int vtId = db.refereeId(firstName, lastName);
            db.matchRefereeId(matchId, vtId, "VT");
        } catch (IllegalArgumentException e) {}

        try {
            JSONObject[] tiesnesi = spele.getArrayOrObject("T");
            int i = 1;
            for (JSONObject t : tiesnesi) {
                String last = t.getString("Uzvards");
                String first = t.getString("Vards");
                int refId = db.refereeId(first, last);
                db.matchRefereeId(matchId, refId, "T" + i);
                i++;
            }
        } catch (IllegalArgumentException e) {}
    }

    private int playerIdFromNumber(int teamId, int number) throws SQLException {
        return db.playerId(teamId, number, null, null, null);
    }
    private void updateMatchGoals(int matchId, int team1Id, int team2Id) throws SQLException {
        int goals1 = db.goalCount(matchId, team1Id);
        int goals2 = db.goalCount(matchId, team2Id);
        db.setMatchGoals(matchId, goals1, goals2);
    }

    private void updateMatchDecidedInOT(int matchId) throws SQLException {
        int maxSec = db.maxGoalSeconds(matchId);
        boolean decidedInOT = maxSec > 60 * 60;
        db.setMatchDecidedInOT(matchId, decidedInOT);
    }

    private int timeToSeconds(String mmss) {
        String[] parts = mmss.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }

    private int computeMatchDuration(JSONObject spele) {

        int duration = 60 * 60;

        JSONObject[] komandas = spele.getArrayOrObject("Komanda");
        for (JSONObject komanda : komandas) {
            try {
                JSONObject vartiNode = komanda.getObject("Varti");
                JSONObject[] vgArray = vartiNode.getArrayOrObject("VG");
                for (JSONObject vg : vgArray) {
                    String timeString = vg.getString("Laiks");
                    int sec = timeToSeconds(timeString);
                    if (sec > duration) {
                        duration = sec;
                    }
                }
            } catch (IllegalArgumentException e) {
            }
        }

        return duration;
    }

    private void updateTeamSeconds(int matchId, int team1Id, int team2Id, int matchDurationSeconds) throws SQLException {

        List<Integer> players = db.getMatchPlayers(matchId);
        List<Integer> starterPlayers = db.getStarterPlayers(matchId);
        List<int[]> substitutions = db.getSubstitutions(matchId);
        Map<Integer, List<Integer>> subTimes = new HashMap<>();
        int startTime;
        int endTime;
        int total;

        for(int playerId : players){
            subTimes.put(playerId,new ArrayList<>());
            if(starterPlayers.contains(playerId)){
                subTimes.get(playerId).add(0);
            }
        }

        for(int[] substitution: substitutions){
            subTimes.get(substitution[1]).add(substitution[0]);
            subTimes.get(substitution[2]).add(substitution[0]);
        }

        for (Map.Entry<Integer, List<Integer>> entry : subTimes.entrySet()) {
            int length = entry.getValue().size();
            if(length%2==1){
                entry.getValue().add(matchDurationSeconds);
            }
            db.setSecondsPlayed(matchId,entry.getKey(),computeSubSeconds(entry.getValue()));
        }



    }

    private int computeSubSeconds(List<Integer> substitutions){
        substitutions.sort(Integer::compareTo);
        int total=0;
        for (int i = 0; i < substitutions.size(); i+=2){
            total+=substitutions.get(i+1)-substitutions.get(i);
        };
        return total;

    }



}
