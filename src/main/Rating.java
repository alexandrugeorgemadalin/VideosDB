package main;

import java.util.Map;

public class Rating {

    static class Movie {
        private String name;
        private Map<String, Double> data;

        Movie(final String name, final Map data) {
            this.name = name;
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public Map<String, Double> getData() {
            return data;
        }

        public void setData(final Map<String, Double> data) {
            this.data = data;
        }
    }

    static class Show {
        private String name;
        private int season;
        private Map<String, Double> data;

        Show(final String name, final int season, final Map<String, Double> data) {
            this.name = name;
            this.season = season;
            this.data = data;
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

        public Map<String, Double> getData() {
            return data;
        }

        public void setData(final Map<String, Double> data) {
            this.data = data;
        }
    }
}
