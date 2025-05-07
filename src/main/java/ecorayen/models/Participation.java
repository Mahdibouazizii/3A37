package ecorayen.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Participation {
    private int id;
    private int challengeId;
    private int userId; // Assuming you might want to track users later
    private LocalDateTime participationDateTime;
    private double score;
    private String submissionDetails;

    // Constructors
    public Participation() {
    }

    public Participation(int challengeId, int userId, LocalDateTime participationDateTime, double score, String submissionDetails) {
        this.challengeId = challengeId;
        this.userId = userId;
        this.participationDateTime = participationDateTime;
        this.score = score;
        this.submissionDetails = submissionDetails;
    }

    public Participation(int id, int challengeId, int userId, LocalDateTime participationDateTime, double score, String submissionDetails) {
        this.id = id;
        this.challengeId = challengeId;
        this.userId = userId;
        this.participationDateTime = participationDateTime;
        this.score = score;
        this.submissionDetails = submissionDetails;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getParticipationDateTime() {
        return participationDateTime;
    }

    public void setParticipationDateTime(LocalDateTime participationDateTime) {
        this.participationDateTime = participationDateTime;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSubmissionDetails() {
        return submissionDetails;
    }

    public void setSubmissionDetails(String submissionDetails) {
        this.submissionDetails = submissionDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participation that = (Participation) o;
        return id == that.id && challengeId == that.challengeId && userId == that.userId && Double.compare(that.score, score) == 0 && Objects.equals(participationDateTime, that.participationDateTime) && Objects.equals(submissionDetails, that.submissionDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, challengeId, userId, participationDateTime, score, submissionDetails);
    }

    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", challengeId=" + challengeId +
                ", userId=" + userId +
                ", participationDateTime=" + participationDateTime +
                ", score=" + score +
                ", submissionDetails='" + submissionDetails + '\'' +
                '}';
    }
}