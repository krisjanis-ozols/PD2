package db;

public class TournamentRow {
    private int rank;
    private final int teamId;
    private final String name;
    private final int points;
    private final int winsReg;
    private final int lossesReg;
    private final int winsOt;
    private final int lossesOt;
    private final int goalsFor;
    private final int goalsAgainst;
    private final int goalsDifference;

    public TournamentRow(int teamId, String name, int points, int winsReg, int lossesReg, int winsOt, int lossesOt, int goalsFor, int goalsAgainst, int goalsDifference) {
        this.teamId = teamId;
        this.name = name;
        this.points = points;
        this.winsReg = winsReg;
        this.lossesReg = lossesReg;
        this.winsOt = winsOt;
        this.lossesOt = lossesOt;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.goalsDifference = goalsDifference;
    }

    public int getTeamId() { return teamId; }
    public String getName() { return name; }
    public int getPoints() { return points; }
    public int getWinsReg() { return winsReg; }
    public int getLossesReg() { return lossesReg; }
    public int getWinsOt() { return winsOt; }
    public int getLossesOt() { return lossesOt; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public int getGoalsDifference() { return goalsDifference; }
    public int getRank() { return rank; }

    public void setRank(int rank) { this.rank = rank; }
}
