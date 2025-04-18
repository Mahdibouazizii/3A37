package ecorayen.services;

import ecorayen.interfaces.iservices;
import ecorayen.models.badge;
import ecorayen.utils.Myconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ServiceBadge implements iservices<badge> {

    private Connection cnx;

    public ServiceBadge() {
        cnx = Myconnection.getInstance().getConnection();
    }

    @Override
    public boolean add(badge badge) {
        String qry = "INSERT INTO `badge`(`name`, `description`, `image_path`) VALUES (?, ?, ?)"; // Corrected column name
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, badge.getName());
            pstm.setString(2, badge.getDescription());
            pstm.setString(3, badge.getImagePath());
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding badge: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(badge badge) {
        String qry = "DELETE FROM `badge` WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, badge.getId());
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting badge: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(badge badge) {
        String qry = "UPDATE `badge` SET `name` = ?, `description` = ?, `image_path` = ? WHERE id = ?"; // Corrected column name
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, badge.getName());
            pstm.setString(2, badge.getDescription());
            pstm.setString(3, badge.getImagePath());
            pstm.setInt(4, badge.getId());
            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating badge: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<badge> getAll() {
        ArrayList<badge> badges = new ArrayList<>();
        String qry = "SELECT b.id, b.name, b.description, b.image_path FROM `badge` b"; // Corrected column name
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(qry)) {
            while (rs.next()) {
                badge b = new badge();
                b.setId(rs.getInt("id"));
                b.setName(rs.getString("name"));
                b.setDescription(rs.getString("description"));
                b.setImagePath(rs.getString("image_path")); // Corrected column name
                badges.add(b);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all badges: " + e.getMessage());
        }
        return badges;
    }

    @Override
    public badge getById(int id) {
        String qry = "SELECT b.id, b.name, b.description, b.image_path FROM `badge` b WHERE b.id = ?"; // Corrected column name
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    badge b = new badge();
                    b.setId(rs.getInt("id"));
                    b.setName(rs.getString("name"));
                    b.setDescription(rs.getString("description"));
                    b.setImagePath(rs.getString("image_path")); // Corrected column name
                    return b;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting badge by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<badge> getByField(String field) {
        System.err.println("getByField(String field) is not implemented.");
        return null;
    }

    @Override
    public ArrayList<badge> getByField(String field, String value) {
        ArrayList<badge> badges = new ArrayList<>();
        String qry = "SELECT b.id, b.name, b.description, b.image_path FROM `badge` b WHERE b." + field + " = ?"; // Corrected column name
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, value);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    badge b = new badge();
                    b.setId(rs.getInt("id"));
                    b.setName(rs.getString("name"));
                    b.setDescription(rs.getString("description"));
                    b.setImagePath(rs.getString("image_path")); // Corrected column name
                    badges.add(b);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting badges by field: " + e.getMessage());
        }
        return badges;
    }
}