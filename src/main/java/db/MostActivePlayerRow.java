package db;

public class MostActivePlayerRow {
    private int rank;

    private final String firstName;
    private final String lastName;
    private final String teamName;
    private final int totalSeconds;

    public MostActivePlayerRow(String firstName, String lastName, String teamName, int totalSeconds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.teamName = teamName;
        this.totalSeconds = totalSeconds;
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getTeamName() { return teamName; }
    public int getTotalSeconds() { return totalSeconds; }

    // smuks formāts tabulai: mm:ss vai hh:mm:ss
    public String getTotalTime() {
        int s = totalSeconds;
        int h = s / 3600;
        int m = (s % 3600) / 60;
        int sec = s % 60;
        return (h > 0) ? String.format("%d:%02d:%02d", h, m, sec) : String.format("%d:%02d", m, sec);
    }
}
