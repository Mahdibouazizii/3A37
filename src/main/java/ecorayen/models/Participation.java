package ecorayen.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Participation {
    private int id;
    private int challenge_id; // Updated to match database
    private LocalDateTime participation_date_time; // Updated to match database
    private double score;
    private String submission_details; // Updated to match database

    // Constructors
    public Participation() {
    }

    public Participation(int challenge_id, LocalDateTime participation_date_time, double score, String submission_details) {
        this.challenge_id = challenge_id;
        this.participation_date_time = participation_date_time;
        this.score = score;
        this.submission_details = submission_details;
    }

    public Participation(int id, int challenge_id, LocalDateTime participation_date_time, double score, String submission_details) {
        this.id = id;
        this.challenge_id = challenge_id;
        this.participation_date_time = participation_date_time;
        this.score = score;
        this.submission_details = submission_details;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChallengeId() {
        return challenge_id; // Updated getter name
    }

    public void setChallengeId(int challenge_id) { // Updated setter name
        this.challenge_id = challenge_id;
    }

    public LocalDateTime getParticipationDateTime() {
        return participation_date_time; // Updated getter name
    }

    public void setParticipationDateTime(LocalDateTime participation_date_time) { // Updated setter name
        this.participation_date_time = participation_date_time;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSubmissionDetails() {
        return submission_details; // Updated getter name
    }

    public void setSubmissionDetails(String submission_details) { // Updated setter name
        this.submission_details = submission_details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participation that = (Participation) o;
        return id == that.id && challenge_id == that.challenge_id && Double.compare(that.score, score) == 0 && Objects.equals(participation_date_time, that.participation_date_time) && Objects.equals(submission_details, that.submission_details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, challenge_id, participation_date_time, score, submission_details);
    }

    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", challenge_id=" + challenge_id +
                ", participation_date_time=" + participation_date_time +
                ", score=" + score +
                ", submission_details='" + submission_details + '\'' +
                '}';
    }
}