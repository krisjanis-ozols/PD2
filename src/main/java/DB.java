import java.sql.*;

public class DB {
    private Connection connection;

    public DB(){
        String url = "jdbc:sqlite:lfl.db";
        try{
            this.connection=DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
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

    public int playerId(int teamId, int number, String firstName, String lastName, String role) throws SQLException{
        String select= """
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

    public int matchId(String date, String venue, Integer spectators, int team1Id, int team2Id) throws SQLException{
        String select = """
            SELECT id FROM match
            WHERE match_date = ? AND venue = ? AND team1_id = ? AND team2_id = ?
            """;
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setString(1, date);
            ps.setString(2, venue);
            ps.setInt(3, team1Id);
            ps.setInt(4, team2Id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

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

    public int goalId(int matchId, int teamId, int scorerId, int seconds, boolean isPenalty) throws SQLException{
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

    public int assistId(int goalId, int playerId, int assistOrder) throws SQLException{
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

    public int cardId(int matchId, int teamId, int playerId, int seconds, String cardType) throws SQLException{
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

    public int substitutionId(int matchId, int teamId, int seconds, int playerOutId, int playerInId) throws SQLException{
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

    public int refereeId(String firstName, String lastName) throws SQLException{
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

    public int matchRefereeId(int matchId, int refereeId, String role) throws SQLException{
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
    public int matchPlayerId(int matchId, int playerId, boolean isStarter, Integer minutesPlayed) throws SQLException {
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
        INSERT INTO match_player(match_id, player_id, is_starter, minutes_played)
        VALUES (?, ?, ?, ?)
        """;
        try (PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, matchId);
            ps.setInt(2, playerId);
            ps.setInt(3, isStarter ? 1 : 0);
            if (minutesPlayed == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, minutesPlayed);
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert match_player");
    }
}
