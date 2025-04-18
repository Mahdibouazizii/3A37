package ecorayen.models;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class badge implements Serializable {

    private Integer id;
    private String name;
    private String description;
    private String imagePath;
    private File imageFile;
    // Default constructor
    public badge() {
    }

    // Constructor with fields (excluding id, imageFile, and challenge)
    public badge(String name, String description, String imagePath) {
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
    }

    // Constructor with all fields (excluding challenge)
    public badge(Integer id, String name, String description, String imagePath, File imageFile) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.imageFile = imageFile;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // public challenge getChallenge() { // Removed getChallenge()
    //     return challenge;
    // }

    // public void setChallenge(challenge challenge) { // Removed setChallenge()
    //     this.challenge = challenge;
    // }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
        // If a new file is uploaded, you can handle any additional logic here
        if (imageFile != null) {

        }
    }

    @Override
    public String toString() {
        return "badge{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                // ", challenge=" + challenge + // Removed challenge from toString
                ", imageFile=" + imageFile +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        badge badge = (badge) o;
        return Objects.equals(id, badge.id) && Objects.equals(name, badge.name) && Objects.equals(description, badge.description) && Objects.equals(imagePath, badge.imagePath) && Objects.equals(imageFile, badge.imageFile);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, imagePath, imageFile);

    }
}