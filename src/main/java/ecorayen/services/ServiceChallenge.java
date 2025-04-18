package ecorayen.services;

import ecorayen.interfaces.iservices;
import ecorayen.models.challenge;
import ecorayen.utils.Myconnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ServiceChallenge implements iservices<challenge> {

    private Connection cnx;

    public ServiceChallenge() {
        cnx = Myconnection.getInstance().getConnection();
    }

    @Override
    public boolean add(challenge challenge) {
        String qry = "INSERT INTO `challenge`(`name`, `description`, `date_start`, `date_end`, `location`, `image`) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, challenge.getName());
            pstm.setString(2, challenge.getDescription());
            pstm.setDate(3, Date.valueOf(challenge.getDate_start()));
            pstm.setDate(4, Date.valueOf(challenge.getDate_end()));
            pstm.setString(5, challenge.getLocation());
            pstm.setString(6, challenge.getImage());
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding challenge: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(challenge challenge) {
        String qry = "DELETE FROM `challenge` WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, challenge.getId());
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting challenge: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(challenge challenge) {
        String qry = "UPDATE `challenge` SET `name` = ?, `description` = ?, `date_start` = ?, `date_end` = ?, `location` = ?, `image` = ? WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, challenge.getName());
            pstm.setString(2, challenge.getDescription());
            pstm.setDate(3, Date.valueOf(challenge.getDate_start()));
            pstm.setDate(4, Date.valueOf(challenge.getDate_end()));
            pstm.setString(5, challenge.getLocation());
            pstm.setString(6, challenge.getImage());
            pstm.setInt(7, challenge.getId());
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating challenge: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<challenge> getAll() {
        ArrayList<challenge> challenges = new ArrayList<>();
        String qry = "SELECT c.id, c.name, c.description, c.date_start, c.date_end, c.location, c.image FROM `challenge` c";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(qry)) {
            while (rs.next()) {
                challenge c = new challenge();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setDescription(rs.getString("description"));
                c.setDate_start(rs.getDate("date_start").toLocalDate());
                c.setDate_end(rs.getDate("date_end").toLocalDate());
                c.setLocation(rs.getString("location"));
                c.setImage(rs.getString("image"));
                challenges.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all challenges: " + e.getMessage());
        }
        return challenges;
    }

    @Override
    public challenge getById(int id) {
        String qry = "SELECT c.id, c.name, c.description, c.date_start, c.date_end, c.location, c.image FROM `challenge` c WHERE c.id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    challenge c = new challenge();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setDescription(rs.getString("description"));
                    c.setDate_start(rs.getDate("date_start").toLocalDate());
                    c.setDate_end(rs.getDate("date_end").toLocalDate());
                    c.setLocation(rs.getString("location"));
                    c.setImage(rs.getString("image"));
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting challenge by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<challenge> getByField(String field) {
        System.err.println("getByField(String field) is not implemented.");
        return null;
    }

    @Override
    public ArrayList<challenge> getByField(String field, String value) {
        ArrayList<challenge> challenges = new ArrayList<>();
        String qry = "SELECT c.id, c.name, c.description, c.date_start, c.date_end, c.location, c.image FROM `challenge` c WHERE c." + field + " = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, value);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    challenge c = new challenge();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setDescription(rs.getString("description"));
                    c.setDate_start(rs.getDate("date_start").toLocalDate());
                    c.setDate_end(rs.getDate("date_end").toLocalDate());
                    c.setLocation(rs.getString("location"));
                    c.setImage(rs.getString("image"));
                    challenges.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting challenges by field: " + e.getMessage());
        }
        return challenges;
    }
}