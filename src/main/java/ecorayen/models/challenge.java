    package ecorayen.models;

    import java.time.LocalDate;
    import java.util.Objects;

    public class challenge {
        private int id;
        private String name;
        private String description;
        private LocalDate date_start;
        private LocalDate date_end;
        private String location;
        private String image;

        public challenge(String name, String description, LocalDate date_start, LocalDate date_end, String location, String image) {
            this.name = name;
            this.description = description;
            this.date_start = date_start;
            this.date_end = date_end;
            this.location = location;
            this.image = image;
        }

        public challenge(int id, String name, String description, LocalDate date_start, LocalDate date_end, String location, String image) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.date_start = date_start;
            this.date_end = date_end;
            this.location = location;
            this.image = image;
        }

        public challenge() {

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

        public LocalDate getDate_start() {
            return date_start;
        }

        public void setDate_start(LocalDate date_start) {
            this.date_start = date_start;
        }

        public LocalDate getDate_end() {
            return date_end;
        }

        public void setDate_end(LocalDate date_end) {
            this.date_end = date_end;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            challenge challenge = (challenge) o;
            return id == challenge.id && Objects.equals(name, challenge.name) && Objects.equals(description, challenge.description) && Objects.equals(date_start, challenge.date_start) && Objects.equals(date_end, challenge.date_end) && Objects.equals(location, challenge.location) && Objects.equals(image, challenge.image);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, description, date_start, date_end, location, image);
        }

        @Override
        public String toString() {
            return "challenge{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", date_start=" + date_start +
                    ", date_end=" + date_end +
                    ", location='" + location + '\'' +
                    ", image='" + image + '\'' +
                    '}';
        }
    }