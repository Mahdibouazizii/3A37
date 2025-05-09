package ecorayen.services;

import ecorayen.models.Participation;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipationService {

    private final String DB_URL = "jdbc:mysql://localhost:3306/ff16"; // Replace with your DB URL
    private final String DB_USER = "root"; // Replace with your DB user
    private final String DB_PASSWORD = ""; // Replace with your DB password

    public boolean add(Participation p) {
        String sql = "INSERT INTO participations (challenge_id, participation_date_time, score, submission_details) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getChallengeId());
            pstmt.setObject(2, p.getParticipationDateTime());
            pstmt.setDouble(3, p.getScore());
            pstmt.setString(4, p.getSubmissionDetails());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding participation: " + e.getMessage());
            return false;
        }
    }

    public List<Participation> getAll() {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT id, challenge_id, participation_date_time, score, submission_details FROM participations";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Participation p = new Participation(
                        rs.getInt("id"),
                        rs.getInt("challenge_id"),
                        rs.getObject("participation_date_time", LocalDateTime.class),
                        rs.getDouble("score"),
                        rs.getString("submission_details")
                );
                participations.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all participations: " + e.getMessage());
        }
        return participations;
    }

    public Optional<Participation> getById(int id) {
        String sql = "SELECT challenge_id, participation_date_time, score, submission_details FROM participations WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Participation(
                        id,
                        rs.getInt("challenge_id"),
                        rs.getObject("participation_date_time", LocalDateTime.class),
                        rs.getDouble("score"),
                        rs.getString("submission_details")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting participation by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean update(Participation p) {
        String sql = "UPDATE participations SET challenge_id = ?, participation_date_time = ?, score = ?, submission_details = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getChallengeId());
            pstmt.setObject(2, p.getParticipationDateTime());
            pstmt.setDouble(3, p.getScore());
            pstmt.setString(4, p.getSubmissionDetails());
            pstmt.setInt(5, p.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating participation: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM participations WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting participation: " + e.getMessage());
            return false;
        }
    }

    // Statistics methods
    public int getTotalParticipations() {
        String sql = "SELECT COUNT(*) FROM participations";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total participation count: " + e.getMessage());
        }
        return 0;
    }

    public double getAverageScore() {
        String sql = "SELECT AVG(score) FROM participations";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting average score: " + e.getMessage());
        }
        return 0.0;
    }

    public Optional<Double> getHighestScore() {
        String sql = "SELECT MAX(score) FROM participations";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return Optional.ofNullable(rs.getDouble(1));
            }
        } catch (SQLException e) {
            System.err.println("Error getting highest score: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Double> getLowestScore() {
        String sql = "SELECT MIN(score) FROM participations";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return Optional.ofNullable(rs.getDouble(1));
            }
        } catch (SQLException e) {
            System.err.println("Error getting lowest score: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Participation> getTopNParticipations(int n) {
        List<Participation> topParticipations = new ArrayList<>();
        String sql = "SELECT id, challenge_id, participation_date_time, score, submission_details FROM participations ORDER BY score DESC LIMIT ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, n);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Participation p = new Participation(
                        rs.getInt("id"),
                        rs.getInt("challenge_id"),
                        rs.getObject("participation_date_time", LocalDateTime.class),
                        rs.getDouble("score"),
                        rs.getString("submission_details")
                );
                topParticipations.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error getting top " + n + " participations: " + e.getMessage());
        }
        return topParticipations;
    }

    public List<Participation> getParticipationsByChallenge(int challengeId) {
        List<Participation> challengeParticipations = new ArrayList<>();
        String sql = "SELECT id, challenge_id, participation_date_time, score, submission_details FROM participations WHERE challenge_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, challengeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Participation p = new Participation(
                        rs.getInt("id"),
                        rs.getInt("challenge_id"),
                        rs.getObject("participation_date_time", LocalDateTime.class),
                        rs.getDouble("score"),
                        rs.getString("submission_details")
                );
                challengeParticipations.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error getting participations for challenge " + challengeId + ": " + e.getMessage());
        }
        return challengeParticipations;
    }

    // Add more statistical methods as needed (e.g., participations by date range, etc.)
}