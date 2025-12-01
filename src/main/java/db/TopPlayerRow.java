package db;

public class TopPlayerRow {
    private final int playerId;
    private final String firstName;
    private final String lastName;
    private final String teamName;
    private final int goals;
    private final int assists;

    public TopPlayerRow(int playerId, String firstName, String lastName, String teamName, int goals, int assists) {
        this.playerId = playerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teamName = teamName;
        this.goals = goals;
        this.assists = assists;
    }

    public int getPlayerId() { return playerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getTeamName() { return teamName; }
    public int getGoals() { return goals; }
    public int getAssists() { return assists; }
}