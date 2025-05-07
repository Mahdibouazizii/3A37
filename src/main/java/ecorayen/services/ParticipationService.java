package ecorayen.services;

import ecorayen.models.Participation;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParticipationService {

    private final String DB_URL = "jdbc:mysql://localhost:3306/ff16"; // Replace with your DB URL
    private final String DB_USER = "root"; // Replace with your DB user
    private final String DB_PASSWORD = ""; // Replace with your DB password

    public boolean add(Participation p) {
        String sql = "INSERT INTO participations (challenge_id, user_id, participation_date_time, score, submission_details) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getChallengeId());
            pstmt.setInt(2, p.getUserId()); // Assuming you'll eventually use user IDs
            pstmt.setObject(3, p.getParticipationDateTime());
            pstmt.setDouble(4, p.getScore());
            pstmt.setString(5, p.getSubmissionDetails());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding participation: " + e.getMessage());
            return false;
        }
    }

    public List<Participation> getAll() {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT id, challenge_id, user_id, participation_date_time, score, submission_details FROM participations";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Participation p = new Participation(
                        rs.getInt("id"),
                        rs.getInt("challenge_id"),
                        rs.getInt("user_id"),
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

    public Participation getById(int id) {
        String sql = "SELECT challenge_id, user_id, participation_date_time, score, submission_details FROM participations WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Participation(
                        id,
                        rs.getInt("challenge_id"),
                        rs.getInt("user_id"),
                        rs.getObject("participation_date_time", LocalDateTime.class),
                        rs.getDouble("score"),
                        rs.getString("submission_details")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting participation by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean delete(Participation p) {
        String sql = "DELETE FROM participations WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting participation: " + e.getMessage());
            return false;
        }
    }

    // Statistics methods (basic examples)
    public int getParticipationCountForChallenge(int challengeId) {
        String sql = "SELECT COUNT(*) FROM participations WHERE challenge_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, challengeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting participation count: " + e.getMessage());
        }
        return 0;
    }

    public double getAverageScoreForChallenge(int challengeId) {
        String sql = "SELECT AVG(score) FROM participations WHERE challenge_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, challengeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting average score: " + e.getMessage());
        }
        return 0.0;
    }

    // Add more statistical methods as needed (e.g., highest score, lowest score)
}