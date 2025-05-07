package ecorayen.services;

import ecorayen.models.post;
import ecorayen.utils.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class postservice {

    private Connection connection;

    public postservice() {
        connection = Myconnection.getInstance().getConnection();
        if (connection == null) {
            System.err.println("Error: Database connection is null in postservice.");
        }
    }

    public void add(post p) {
        if (connection == null) {
            System.err.println("Error: Cannot add post. Database connection is null.");
            return;
        }
        try {
            String sql = "INSERT INTO posts (caption, image_url, location, created_at, updated_at, post_type, aspect_ratio, post_like_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, p.getCaption());
            statement.setString(2, p.getImage_url());
            statement.setString(3, p.getLocation());
            statement.setString(4, p.getCreated_at());
            statement.setString(5, p.getUpdated_at());
            statement.setString(6, p.getPost_type());
            if (p.getAspect_ratio() != null) {
                statement.setDouble(7, p.getAspect_ratio());
            } else {
                statement.setNull(7, Types.DOUBLE);
            }
            statement.setInt(8, p.getPost_like_count());
            statement.executeUpdate();
            System.out.println("Post added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding post to database: " + e.getMessage());
        }
    }

    public void update(post p) {
        if (connection == null) {
            System.err.println("Error: Cannot update post. Database connection is null.");
            return;
        }
        try {
            String sql = "UPDATE posts SET caption = ?, image_url = ?, location = ?, updated_at = CURRENT_TIMESTAMP, post_type = ?, aspect_ratio = ?, post_like_count = ? WHERE post_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, p.getCaption());
            statement.setString(2, p.getImage_url());
            statement.setString(3, p.getLocation());
            statement.setString(4, p.getPost_type());
            if (p.getAspect_ratio() != null) {
                statement.setDouble(5, p.getAspect_ratio());
            } else {
                statement.setNull(5, Types.DOUBLE);
            }
            statement.setInt(6, p.getPost_like_count());
            statement.setInt(7, p.getPost_id());
            statement.executeUpdate();
            System.out.println("Post updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating post in database: " + e.getMessage());
        }
    }

    public boolean delete(post p) {
        if (connection == null) {
            System.err.println("Error: Cannot delete post. Database connection is null.");
            return false;
        }
        try {
            String sql = "DELETE FROM posts WHERE post_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, p.getPost_id());
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Post deleted successfully.");
                return true;
            } else {
                System.out.println("Post with id " + p.getPost_id() + " not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting post from database: " + e.getMessage());
            return false;
        }
    }

    public List<post> getAll() {
        if (connection == null) {
            System.err.println("Error: Cannot fetch posts. Database connection is null.");
            return new ArrayList<>();
        }
        List<post> posts = new ArrayList<>();
        String sql = "SELECT post_id, caption, image_url, location, created_at, updated_at, post_type, aspect_ratio, post_like_count FROM posts";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("Executing SQL query: " + sql);
            while (resultSet.next()) {
                post p = new post();
                p.setPost_id(resultSet.getInt("post_id"));
                p.setCaption(resultSet.getString("caption"));
                p.setImage_url(resultSet.getString("image_url"));
                p.setLocation(resultSet.getString("location"));
                p.setCreated_at(resultSet.getString("created_at"));
                p.setUpdated_at(resultSet.getString("updated_at"));
                p.setPost_type(resultSet.getString("post_type"));
                p.setAspect_ratio(resultSet.getDouble("aspect_ratio"));
                p.setPost_like_count(resultSet.getInt("post_like_count"));
                posts.add(p);
            }
            System.out.println("SQL query executed successfully.");
            System.out.println("Number of posts fetched: " + posts.size());
        } catch (SQLException e) {
            System.err.println("Error fetching all posts from database: " + e.getMessage());
        }
        return posts;
    }

    public post getOne(int id) {
        if (connection == null) {
            System.err.println("Error: Cannot fetch post. Database connection is null.");
            return null;
        }
        String sql = "SELECT post_id, caption, image_url, location, created_at, updated_at, post_type, aspect_ratio, post_like_count FROM posts WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                post p = new post();
                p.setPost_id(resultSet.getInt("post_id"));
                p.setCaption(resultSet.getString("caption"));
                p.setImage_url(resultSet.getString("image_url"));
                p.setLocation(resultSet.getString("location"));
                p.setCreated_at(resultSet.getString("created_at"));
                p.setUpdated_at(resultSet.getString("updated_at"));
                p.setPost_type(resultSet.getString("post_type"));
                p.setAspect_ratio(resultSet.getDouble("aspect_ratio"));
                p.setPost_like_count(resultSet.getInt("post_like_count"));
                return p;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching post by id from database: " + e.getMessage());
        }
        return null;
    }

    public void incrementLikeCount(int postId) {
        if (connection == null) {
            System.err.println("Error: Cannot update like count. Database connection is null.");
            return;
        }
        String sql = "UPDATE posts SET post_like_count = post_like_count + 1 WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            statement.executeUpdate();
            System.out.println("Like count incremented for post ID: " + postId);
        } catch (SQLException e) {
            System.err.println("Error incrementing like count in database: " + e.getMessage());
        }
    }

    public void decrementLikeCount(int postId) {
        if (connection == null) {
            System.err.println("Error: Cannot update like count. Database connection is null.");
            return;
        }
        String sql = "UPDATE posts SET post_like_count = GREATEST(0, post_like_count - 1) WHERE post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            statement.executeUpdate();
            System.out.println("Like count decremented for post ID: " + postId);
        } catch (SQLException e) {
            System.err.println("Error decrementing like count in database: " + e.getMessage());
        }
    }
}