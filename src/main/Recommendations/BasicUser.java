package main.Recommendations;

import fileio.*;
import main.Rating;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BasicUser {
    private static class Video {
        private String name;
        private Double rating;

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Video{" +
                    "name='" + name + '\'' +
                    ", rating=" + rating +
                    '}';
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }

        public Video(String name, Double rating) {
            this.name = name;
            this.rating = rating;
        }
    }

    public static void standard(final int id, final UserInputData user, final Input input,
                                final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        String recommmendation = null;
        for (MovieInputData movie : input.getMovies()) {
            if (!user.getHistory().containsKey(movie.getTitle())) {
                recommmendation = movie.getTitle();
                break;
            }
        }
        if (recommmendation == null) {
            for (SerialInputData show : input.getSerials()) {
                if (!user.getHistory().containsKey(show.getTitle())) {
                    recommmendation = show.getTitle();
                    break;
                }
            }
        }
        if (recommmendation != null) {
            try {
                result = fileWriter.writeFile(id, "message",
                        "StandardRecommendation result: " + recommmendation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                result = fileWriter.writeFile(id, "message",
                        "StandardRecommendation cannot be applied!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        arrayResult.add(result);

    }

    static class SortbyRating implements Comparator<Video> {
        public int compare(final Video a, final Video b) {
            return b.getRating().compareTo(a.getRating());
        }
    }

    public static void bestunseed(final int id, final UserInputData user,
                                  final ArrayList<Rating.Movie> movieratings,
                                  final ArrayList<Rating.Show> showratings,
                                  final Input input,
                                  final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        ArrayList<Video> unseen = new ArrayList<>();
        for (MovieInputData movie : input.getMovies()) {
            if (!user.getHistory().containsKey(movie.getTitle())) {
                for (Rating.Movie ratedmovie : movieratings) {
                    if (ratedmovie.getName().equals(movie.getTitle())) {
                        Video video = new Video(movie.getTitle(), ratedmovie.getAveragerating());
                        unseen.add(video);
                        break;
                    }
                }
            }
        }
        for (SerialInputData show : input.getSerials()) {
            if (!user.getHistory().containsKey(show.getTitle())) {
                for (Rating.Show ratedshow : showratings) {
                    if (ratedshow.getName().equals(show.getTitle())) {
                        Video video = new Video(show.getTitle(), ratedshow.getAveragerating());
                        unseen.add(video);
                        break;
                    }
                }
            }
        }
        Collections.sort(unseen, new SortbyRating());
        if (unseen.size() != 0) {
            try {
                result = fileWriter.writeFile(id, "message",
                        "BestRatedUnseenRecommendation result: " + unseen.get(0).getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                result = fileWriter.writeFile(id, "message",
                        "BestRatedUnseenRecommendation result: " + input.getMovies().get(0).getTitle());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        arrayResult.add(result);
    }
}
