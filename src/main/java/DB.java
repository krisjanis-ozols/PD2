import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DB {
    private Connection connection;

    public DB() {
        String url = "jdbc:sqlite:lfl.db";
        try {
            this.connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int teamId(String teamName) throws SQLException {

        String select = "SELECT id FROM team WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setString(1, teamName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insert = "INSERT INTO team(name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, teamName);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert team");
    }

    public int playerId(int teamId, int number, String firstName, String lastName, String role) throws SQLException {
        String select = """
                SELECT id FROM player
                WHERE team_id = ? AND number = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setInt(1, teamId);
            ps.setInt(2, number);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insert = """
                INSERT INTO player(team_id, number, first_name, last_name, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, teamId);
            ps.setInt(2, number);
            ps.setString(3, firstName);
            ps.setString(4, lastName);
            ps.setString(5, role);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert player");
    }

    public int matchId(String date, String venue, Integer spectators, int team1Id, int team2Id) throws SQLException {

        String insert = """
                INSERT INTO match(match_date, venue, spectators, team1_id, team2_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, date);
            ps.setString(2, venue);
            if (spectators == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, spectators);
            }
            ps.setInt(4, team1Id);
            ps.setInt(5, team2Id);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert match");
    }

    public int goalId(int matchId, int teamId, int scorerId, int seconds, boolean isPenalty) throws SQLException {
        int penaltyFlag = isPenalty ? 1 : 0;

        String select = """
                SELECT id FROM goal
                WHERE match_id = ? AND team_id = ? AND scorer_id = ? AND seconds = ? AND is_penalty = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ps.setInt(3, scorerId);
            ps.setInt(4, seconds);
            ps.setInt(5, penaltyFlag);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insert = """
                INSERT INTO goal(match_id, team_id, scorer_id, seconds, is_penalty)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ps.setInt(3, scorerId);
            ps.setInt(4, seconds);
            ps.setInt(5, penaltyFlag);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert goal");
    }

    public int assistId(int goalId, int playerId, int assistOrder) throws SQLException {
        String select = """
                SELECT id FROM assist
                WHERE goal_id = ? AND player_id = ? AND assist_order = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setInt(1, goalId);
            ps.setInt(2, playerId);
            ps.setInt(3, assistOrder);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insert = """
                INSERT INTO assist(goal_id, player_id, assist_order)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, goalId);
            ps.setInt(2, playerId);
            ps.setInt(3, assistOrder);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert assist");
    }

    public int cardId(int matchId, int teamId, int playerId, int seconds, String cardType) throws SQLException {
        String select = """
                SELECT id FROM card
                WHERE match_id = ? AND team_id = ? AND player_id = ? AND seconds = ? AND card_type = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ps.setInt(3, playerId);
            ps.setInt(4, seconds);
            ps.setString(5, cardType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insert = """
                INSERT INTO card(match_id, team_id, player_id, seconds, card_type)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ps.setInt(3, playerId);
            ps.setInt(4, seconds);
            ps.setString(5, cardType);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert card");
    }

    public int substitutionId(int matchId, int teamId, int seconds, int playerOutId, int playerInId) throws SQLException {
        String select = """
                SELECT id FROM substitution
                WHERE match_id = ? AND team_id = ? AND seconds = ? AND player_out_id = ? AND player_in_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ps.setInt(3, seconds);
            ps.setInt(4, playerOutId);
            ps.setInt(5, playerInId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insert = """
                INSERT INTO substitution(match_id, team_id, seconds, player_out_id, player_in_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            ps.setInt(3, seconds);
            ps.setInt(4, playerOutId);
            ps.setInt(5, playerInId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert substitution");
    }

    public int refereeId(String firstName, String lastName) throws SQLException {
        String select = """
                SELECT id FROM referee
                WHERE first_name = ? AND last_name = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insert = """
                INSERT INTO referee(first_name, last_name)
                VALUES (?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert referee");
    }

    public int matchRefereeId(int matchId, int refereeId, String role) throws SQLException {
        String select = """
                SELECT id FROM match_referee
                WHERE match_id = ? AND referee_id = ? AND role = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setInt(1, matchId);
            ps.setInt(2, refereeId);
            ps.setString(3, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insert = """
                INSERT INTO match_referee(match_id, referee_id, role)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, matchId);
            ps.setInt(2, refereeId);
            ps.setString(3, role);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert match_referee");
    }

    public int matchPlayerId(int matchId, int playerId, boolean isStarter) throws SQLException {
        String select = """
                SELECT id FROM match_player
                WHERE match_id = ? AND player_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setInt(1, matchId);
            ps.setInt(2, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insert = """
                INSERT INTO match_player(match_id, player_id, is_starter)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, matchId);
            ps.setInt(2, playerId);
            ps.setInt(3, isStarter ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert match_player");
    }

    public Boolean checkIfMatchAdded(String date, int team1Id, int team2Id) throws SQLException {

        String sql = """
                SELECT id FROM match
                WHERE match_date = ?
                AND (team1_id = ? OR team2_id = ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setInt(2, team1Id);
            ps.setInt(3, team2Id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }

        return false;
    }

    public int onlyGetTeamId(String teamName) throws SQLException {

        String select = "SELECT id FROM team WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setString(1, teamName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }

    public int goalCount(int matchId, int teamId) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM goal
                WHERE match_id = ? AND team_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ps.setInt(2, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        }
        return 0;
    }

    public void setMatchGoals(int matchId, int goalsTeam1, int goalsTeam2) throws SQLException {
        String sql = """
                UPDATE match
                SET goals_team1 = ?, goals_team2 = ?
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, goalsTeam1);
            ps.setInt(2, goalsTeam2);
            ps.setInt(3, matchId);
            ps.executeUpdate();
        }
    }

    public int maxGoalSeconds(int matchId) throws SQLException {
        String sql = """
                SELECT MAX(seconds) AS max_sec
                FROM goal
                WHERE match_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("max_sec");
                }
            }
        }
        return 0;
    }

    public void setMatchDecidedInOT(int matchId, boolean decided) throws SQLException {
        String sql = """
                UPDATE match
                SET decided_in_ot = ?
                WHERE id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, decided ? 1 : 0);
            ps.setInt(2, matchId);
            ps.executeUpdate();
        }
    }

    public void setSecondsPlayed(int matchId, int playerId, int seconds) throws SQLException {
        String sql = """
                    UPDATE match_player
                    SET seconds_played = ?
                    WHERE match_id = ? AND player_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, seconds);
            ps.setInt(2, matchId);
            ps.setInt(3, playerId);
            ps.executeUpdate();
        }
    }

    public List<Integer> getMatchPlayers(int matchId) throws SQLException {
        String sql = """
                    SELECT player_id
                    FROM match_player
                    WHERE match_id = ?
                """;

        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("player_id"));
                }
            }
        }
        return ids;
    }


    public List<Integer> getStarterPlayers(int matchId) throws SQLException {
        String sql = """
                SELECT player_id
                FROM match_player
                WHERE match_id = ? AND is_starter = 1
                """;

        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("player_id"));
                }
            }
        }
        return ids;
    }

    public List<int[]> getSubstitutions(int matchId) throws SQLException {
        String sql = """
                    SELECT seconds, player_out_id, player_in_id
                    FROM substitution
                    WHERE match_id = ?
                    ORDER BY seconds ASC
                """;

        List<int[]> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int sec = rs.getInt("seconds");
                    int out = rs.getInt("player_out_id");
                    int in = rs.getInt("player_in_id");

                    list.add(new int[]{sec, out, in});
                }
            }
        }

        return list;
    }

    public List<TournamentRow> getTournamentTable() throws SQLException {
        String sql = """
        SELECT
            t.id AS team_id,
            t.name,
            SUM(
                CASE
                    WHEN m.goals_team1 > m.goals_team2 AND m.team1_id = t.id AND m.decided_in_ot = 0 THEN 3
                    WHEN m.goals_team2 > m.goals_team1 AND m.team2_id = t.id AND m.decided_in_ot = 0 THEN 3
                    WHEN m.goals_team1 > m.goals_team2 AND m.team1_id = t.id AND m.decided_in_ot = 1 THEN 2
                    WHEN m.goals_team2 > m.goals_team1 AND m.team2_id = t.id AND m.decided_in_ot = 1 THEN 2
                    WHEN m.goals_team1 < m.goals_team2 AND m.team1_id = t.id AND m.decided_in_ot = 1 THEN 1
                    WHEN m.goals_team2 < m.goals_team1 AND m.team2_id = t.id AND m.decided_in_ot = 1 THEN 1
                    ELSE 0
                END
            ) AS points,
            SUM(
                CASE
                    WHEN m.goals_team1 > m.goals_team2 AND m.team1_id = t.id AND m.decided_in_ot = 0 THEN 1
                    WHEN m.goals_team2 > m.goals_team1 AND m.team2_id = t.id AND m.decided_in_ot = 0 THEN 1
                    ELSE 0
                END
            ) AS wins_reg,
            SUM(
                CASE
                    WHEN m.goals_team1 < m.goals_team2 AND m.team1_id = t.id AND m.decided_in_ot = 0 THEN 1
                    WHEN m.goals_team2 < m.goals_team1 AND m.team2_id = t.id AND m.decided_in_ot = 0 THEN 1
                    ELSE 0
                END
            ) AS losses_reg,
            SUM(
                CASE
                    WHEN m.goals_team1 > m.goals_team2 AND m.team1_id = t.id AND m.decided_in_ot = 1 THEN 1
                    WHEN m.goals_team2 > m.goals_team1 AND m.team2_id = t.id AND m.decided_in_ot = 1 THEN 1
                    ELSE 0
                END
            ) AS wins_ot,
            SUM(
                CASE
                    WHEN m.goals_team1 < m.goals_team2 AND m.team1_id = t.id AND m.decided_in_ot = 1 THEN 1
                    WHEN m.goals_team2 < m.goals_team1 AND m.team2_id = t.id AND m.decided_in_ot = 1 THEN 1
                    ELSE 0
                END
            ) AS losses_ot,
            SUM(
                CASE
                    WHEN m.team1_id = t.id THEN m.goals_team1
                    ELSE m.goals_team2
                END
            ) AS goals_for,
            SUM(
                CASE
                    WHEN m.team1_id = t.id THEN m.goals_team2
                    ELSE m.goals_team1
                END
            ) AS goals_against
        FROM team t
        LEFT JOIN match m
          ON t.id = m.team1_id OR t.id = m.team2_id
        GROUP BY t.id
        ORDER BY points DESC, (goals_for - goals_against) DESC
        """;

        List<TournamentRow> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TournamentRow row = new TournamentRow(
                        rs.getInt("team_id"),
                        rs.getString("name"),
                        rs.getInt("points"),
                        rs.getInt("wins_reg"),
                        rs.getInt("losses_reg"),
                        rs.getInt("wins_ot"),
                        rs.getInt("losses_ot"),
                        rs.getInt("goals_for"),
                        rs.getInt("goals_against")
                );
                result.add(row);
            }
        }

        return result;
    }
    public List<TopPlayerRow> getTopPlayers() throws SQLException {
        String sql = """
        SELECT
            p.id            AS player_id,
            p.first_name    AS first_name,
            p.last_name     AS last_name,
            t.name          AS team_name,
            COUNT(DISTINCT g.id) AS goals,
            COUNT(a.id)     AS assists
        FROM player p
        JOIN team t ON p.team_id = t.id
        LEFT JOIN goal g   ON g.scorer_id = p.id
        LEFT JOIN assist a ON a.player_id = p.id
        GROUP BY p.id
        HAVING COUNT(DISTINCT g.id) > 0 OR COUNT(a.id) > 0
        ORDER BY goals DESC,
                 assists DESC,
                 last_name,
                 first_name
        LIMIT 10
        """;

        List<TopPlayerRow> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TopPlayerRow row = new TopPlayerRow(
                        rs.getInt("player_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("team_name"),
                        rs.getInt("goals"),
                        rs.getInt("assists")
                );
                result.add(row);
            }
        }

        return result;
    }

}
