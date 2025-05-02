package ecorayen.models;

import java.util.Objects;

public class comment {
    private int commentId;
    private String author;
    private String content;
    private String date;
    private int likes;
    private int dislikes;

    // Constructors
    public comment() {
        // Default constructor
    }

    public comment(String author, String content, String date) {
        this.author = author;
        this.content = content;
        this.date = date;
        this.likes = 0;
        this.dislikes = 0;
    }

    public comment(int commentId, String author, String content, String date) {
        this.commentId = commentId;
        this.author = author;
        this.content = content;
        this.date = date;
        this.likes = 0;
        this.dislikes = 0;
    }

    public comment(int commentId, String author, String content, String date, int likes, int dislikes) {
        this.commentId = commentId;
        this.author = author;
        this.content = content;
        this.date = date;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    // Getters and Setters
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        comment comment = (comment) o;
        return commentId == comment.commentId && likes == comment.likes && dislikes == comment.dislikes && Objects.equals(author, comment.author) && Objects.equals(content, comment.content) && Objects.equals(date, comment.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, author, content, date, likes, dislikes);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                '}';
    }
}