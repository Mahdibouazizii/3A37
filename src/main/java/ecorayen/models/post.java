package ecorayen.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class post {
    private int post_id;
    private String caption;
    private String image_url;
    private String location;
    private String created_at;
    private String updated_at;
    private String post_type;
    private Double aspect_ratio;
    private int post_like_count;
    private List<comment> comments; // Represents individual comments

    public post() {
        this.comments = new ArrayList<>(); // Initialize comments
        this.post_like_count = 0; // Initialize like count
        this.post_type = "image"; // Default value
    }

    // Constructor without post_id (for creating new posts)
    public post(String caption, String image_url, String location, String created_at, String updated_at, String post_type, Double aspect_ratio) {
        this.caption = caption;
        this.image_url = image_url;
        this.location = location;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.post_type = post_type;
        this.aspect_ratio = aspect_ratio;
        this.post_like_count = 0;
        this.comments = new ArrayList<>();
    }

    // Constructor with all fields including post_id (for fetching from database)
    public post(int post_id, String caption, String image_url, String location, String created_at, String updated_at, String post_type, Double aspect_ratio, int post_like_count, List<comment> comments) {
        this.post_id = post_id;
        this.caption = caption;
        this.image_url = image_url;
        this.location = location;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.post_type = post_type;
        this.aspect_ratio = aspect_ratio;
        this.post_like_count = post_like_count;
        this.comments = comments;
    }

    // Getters and Setters

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public Double getAspect_ratio() {
        return aspect_ratio;
    }

    public void setAspect_ratio(Double aspect_ratio) {
        this.aspect_ratio = aspect_ratio;
    }

    public int getPost_like_count() {
        return post_like_count;
    }

    public void setPost_like_count(int post_like_count) {
        this.post_like_count = post_like_count;
    }

    public List<comment> getComments() {
        return comments;
    }

    public void setComments(List<comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        post post = (post) o;
        return post_id == post.post_id && post_like_count == post.post_like_count && Objects.equals(caption, post.caption) && Objects.equals(image_url, post.image_url) && Objects.equals(location, post.location) && Objects.equals(created_at, post.created_at) && Objects.equals(updated_at, post.updated_at) && Objects.equals(post_type, post.post_type) && Objects.equals(aspect_ratio, post.aspect_ratio) && Objects.equals(comments, post.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post_id, caption, image_url, location, created_at, updated_at, post_type, aspect_ratio, post_like_count, comments);
    }

    @Override
    public String toString() {
        return "post{" +
                "post_id=" + post_id +
                ", caption='" + caption + '\'' +
                ", image_url='" + image_url + '\'' +
                ", location='" + location + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", post_type='" + post_type + '\'' +
                ", aspect_ratio=" + aspect_ratio +
                ", post_like_count=" + post_like_count +
                ", comments=" + comments +
                '}';
    }
}