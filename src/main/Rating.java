package main;

public class Rating {

    public static final class Movie {
        private String name;
        private String username;
        private Double rating;
        private Double averagerating = 0.00;

        public Movie(final String name, final String username, final Double rating) {
            this.name = name;
            this.username = username;
            this.rating = rating;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public Double getRating() {
            return rating;
        }

        public Double getAveragerating() {
            return averagerating;
        }

        public void setAveragerating(final Double averagerating) {
            this.averagerating = averagerating;
        }

        public void setRating(final Double rating) {
            this.rating = rating;
        }
    }

    public static final class Show {
        private String name;
        private int seasonnumber;
        private int season;
        private String username;
        private Double rating;
        private Double averagerating = 0.00;

        public Show(final String name, final int season, final int seasonnumber,
                    final String username, final Double rating) {
            this.name = name;
            this.season = season;
            this.username = username;
            this.rating = rating;
            this.seasonnumber = seasonnumber;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public int getSeason() {
            return season;
        }

        public void setSeason(final int season) {
            this.season = season;
        }

        public int getSeasonnumber() {
            return seasonnumber;
        }

        public void setSeasonnumber(final int seasonnumber) {
            this.seasonnumber = seasonnumber;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(final Double rating) {
            this.rating = rating;
        }

        public Double getAveragerating() {
            return averagerating;
        }

        public void setAveragerating(final Double averagerating) {
            this.averagerating = averagerating;
        }

    }
}
