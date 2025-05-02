package ecorayen.services;

import ecorayen.interfaces.iservices;
import ecorayen.models.comment;
import ecorayen.utils.Myconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class commentservice implements iservices<comment> {

    private Connection cnx;

    public commentservice() {
        cnx = Myconnection.getInstance().getConnection();
    }

    @Override
    public boolean add(comment comment) {
        String qry = "INSERT INTO `comments`(`author`, `content`, `date`, `likes`, `dislikes`) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = cnx.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setString(1, comment.getAuthor());
            pstm.setString(2, comment.getContent());
            pstm.setString(3, comment.getDate());
            pstm.setInt(4, comment.getLikes());
            pstm.setInt(5, comment.getDislikes());
            int affectedRows = pstm.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        comment.setCommentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding comment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(comment comment) {
        if (comment == null || comment.getCommentId() == 0) {
            return false;
        }
        String qry = "DELETE FROM `comments` WHERE comment_id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, comment.getCommentId());
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting comment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(comment comment) {
        if (comment == null || comment.getCommentId() == 0) {
            return false;
        }
        String qry = "UPDATE `comments` SET `author` = ?, `content` = ?, `date` = ?, `likes` = ?, `dislikes` = ? WHERE comment_id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, comment.getAuthor());
            pstm.setString(2, comment.getContent());
            pstm.setString(3, comment.getDate());
            pstm.setInt(4, comment.getLikes());
            pstm.setInt(5, comment.getDislikes());
            pstm.setInt(6, comment.getCommentId());
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating comment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public ArrayList<comment> getAll() {
        List<comment> comments = new ArrayList<>();
        String qry = "SELECT c.comment_id, c.author, c.content, c.date, c.likes, c.dislikes FROM `comments` c";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(qry)) {
            while (rs.next()) {
                comment c = new comment();
                c.setCommentId(rs.getInt("comment_id"));
                c.setAuthor(rs.getString("author"));
                c.setContent(rs.getString("content"));
                c.setDate(rs.getString("date"));
                c.setLikes(rs.getInt("likes"));
                c.setDislikes(rs.getInt("dislikes"));
                comments.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all comments: " + e.getMessage());
        }
        return (ArrayList<comment>) comments;
    }

    @Override
    public comment getById(int id) {
        String qry = "SELECT c.comment_id, c.author, c.content, c.date, c.likes, c.dislikes FROM `comments` c WHERE c.comment_id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    comment c = new comment();
                    c.setCommentId(rs.getInt("comment_id"));
                    c.setAuthor(rs.getString("author"));
                    c.setContent(rs.getString("content"));
                    c.setDate(rs.getString("date"));
                    c.setLikes(rs.getInt("likes"));
                    c.setDislikes(rs.getInt("dislikes"));
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting comment by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<comment> getByField(String field) {
        System.err.println("getByField(String field) is not implemented.");
        return null;
    }

    @Override
    public ArrayList<comment> getByField(String field, String value) {
        List<comment> comments = new ArrayList<>();
        String qry = "SELECT c.comment_id, c.author, c.content, c.date, c.likes, c.dislikes FROM `comments` c WHERE c." + field + " LIKE ?";
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setString(1, "%" + value + "%");
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    comment c = new comment();
                    c.setCommentId(rs.getInt("comment_id"));
                    c.setAuthor(rs.getString("author"));
                    c.setContent(rs.getString("content"));
                    c.setDate(rs.getString("date"));
                    c.setLikes(rs.getInt("likes"));
                    c.setDislikes(rs.getInt("dislikes"));
                    comments.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting comments by field: " + e.getMessage());
        }
        return (ArrayList<comment>) comments;
    }

    public boolean likeComment(int commentId) {
        String qry = "UPDATE `comments` SET `likes` = `likes` + 1 WHERE comment_id = ?";
        return updateLikeDislike(commentId, qry);
    }

    public boolean dislikeComment(int commentId) {
        String qry = "UPDATE `comments` SET `dislikes` = `dislikes` + 1 WHERE comment_id = ?";
        return updateLikeDislike(commentId, qry);
    }

    private boolean updateLikeDislike(int commentId, String qry) {
        try (PreparedStatement pstm = cnx.prepareStatement(qry)) {
            pstm.setInt(1, commentId);
            return pstm.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating likes/dislikes: " + e.getMessage());
            return false;
        }
    }
}