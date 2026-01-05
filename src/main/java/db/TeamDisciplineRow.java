package db;

public class TeamDisciplineRow {
    private int rank;

    private final String teamName;
    private final int yellows;
    private final int reds;
    private final int total;

    public TeamDisciplineRow(String teamName, int yellows, int reds, int total) {
        this.teamName = teamName;
        this.yellows = yellows;
        this.reds = reds;
        this.total = total;
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getTeamName() { return teamName; }
    public int getYellows() { return yellows; }
    public int getReds() { return reds; }
    public int getTotal() { return total; }
}
