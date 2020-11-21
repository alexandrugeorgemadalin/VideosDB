package main.Recommendations;

import entertainment.Genre;
import fileio.*;
import main.Query;
import main.Rating;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class PremiumUser {
    static class Video {
        private String name;
        private Double rating;
        private Integer views;
        private String genre;

        public Video(Integer views, String genre) {
            this.views = views;
            this.genre = genre;
        }

        public String getGenre() {
            return genre;
        }

        @Override
        public String toString() {
            return name;
        }


        public void setGenre(String genre) {
            this.genre = genre;
        }

        public Video(String name, double rating) {
            this.name = name;
            this.rating = rating;
        }

        public Video(String name, Integer views) {
            this.name = name;
            this.views = views;
        }

        public Integer getViews() {
            return views;
        }

        public void setViews(Integer views) {
            this.views = views;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }
    }

    static class SortbyRating implements Comparator<Video> {
        public int compare(final Video a, final Video b) {
            if (a.rating.compareTo(b.rating) == 0) {
                return a.name.compareTo(b.name);
            }
            return a.rating.compareTo(b.rating);
        }
    }

    public static void search(final int id, final UserInputData user,
                              final String genre,
                              final ArrayList<Rating.Movie> movieratings,
                              final ArrayList<Rating.Show> showratings,
                              final Input input,
                              final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        ArrayList<Video> videos = new ArrayList<>();
        for (MovieInputData movie : input.getMovies()) {
            if (movie.getGenres().contains(genre)) {
                if (!user.getHistory().containsKey(movie.getTitle())) {
                    double rating = 0;
                    for (Rating.Movie video : movieratings) {
                        if (video.getName().equals(movie.getTitle())) {
                            rating = video.getAveragerating();
                            break;
                        }
                    }
                    Video v = new Video(movie.getTitle(), rating);
                    videos.add(v);
                }
            }
        }
        for (SerialInputData show : input.getSerials()) {
            if (show.getGenres().contains(genre)) {
                if (!user.getHistory().containsKey(show.getTitle())) {
                    double rating = 0;
                    for (Rating.Show video : showratings) {
                        if (video.getName().equals(show.getTitle())) {
                            rating = video.getAveragerating();
                            break;
                        }
                    }
                    Video v = new Video(show.getTitle(), rating);
                    videos.add(v);
                }
            }
        }
        Collections.sort(videos, new SortbyRating());
        if (videos.size() != 0) {
            try {
                result = fileWriter.writeFile(id, "message",
                        "SearchRecommendation result: " + videos.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            arrayResult.add(result);
        } else {
            try {
                result = fileWriter.writeFile(id, "message",
                        "SearchRecommendation cannot be applied!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            arrayResult.add(result);
        }
    }

    static class SortbyViews implements Comparator<Video> {
        public int compare(final Video a, final Video b) {
            return b.views.compareTo(a.views);
        }
    }

    public static void favorite(final int id, final UserInputData user,
                                final Input input,
                                final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        ArrayList<Video> videos = new ArrayList<>();
        for (MovieInputData movie : input.getMovies()) {
            Integer no_views = 0;
            if (!user.getHistory().containsKey(movie.getTitle())) {
                for (UserInputData u : input.getUsers()) {
                    if (u.getHistory().containsKey(movie.getTitle())) {
                        no_views += u.getHistory().getOrDefault(movie.getTitle(), 0);
                    }
                }
            }
            Video v = new Video(movie.getTitle(), no_views);
            videos.add(v);
        }
        for (SerialInputData show : input.getSerials()) {
            Integer no_views = 0;
            if (!user.getHistory().containsKey(show.getTitle())) {
                for (UserInputData u : input.getUsers()) {
                    if (u.getHistory().containsKey(show.getTitle())) {
                        no_views += u.getHistory().getOrDefault(show.getTitle(), 0);
                    }
                }
            }
            Video v = new Video(show.getTitle(), no_views);
            videos.add(v);
        }
        Collections.sort(videos, new SortbyViews());
        if (videos.size() != 0) {
            boolean ok = false;
            for(Video video:videos){
                for(UserInputData u: input.getUsers()){
                    if(u.getFavoriteMovies().contains(video.getName())){
                        try {
                            result = fileWriter.writeFile(id, "message",
                                    "FavoriteRecommendation result: " + video.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        arrayResult.add(result);
                        ok = true;
                        break;
                    }
                }
                if(ok){
                    break;
                }
            }
            if(!ok){
                try {
                    result = fileWriter.writeFile(id, "message",
                            "FavoriteRecommendation cannot be applied!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                arrayResult.add(result);
            }
        }
        else{
            try {
                result = fileWriter.writeFile(id, "message",
                        "FavoriteRecommendation cannot be applied!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            arrayResult.add(result);
        }
    }

    public static void popular(final int id, final UserInputData user,
                               final Input input,
                               final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        ArrayList<String> genres = new ArrayList<>();
        for (MovieInputData movie : input.getMovies()) {
            for (String genre : movie.getGenres()) {
                if (!genre.matches("[0-9]+") & !genres.contains(genre)) {
                    genres.add(genre);
                }
            }
        }
        for (SerialInputData show : input.getSerials()) {
            for (String genre : show.getGenres()) {
                if (!genre.matches("[0-9]+") & !genres.contains(genre)) {
                    genres.add(genre);
                }
            }
        }
        ArrayList<Video> popular = new ArrayList<>();
        for (String genre : genres) {
            Integer views = 0;
            for (MovieInputData movie : input.getMovies()) {
                if (movie.getGenres().contains(genre)) {
                    for (UserInputData u : input.getUsers()) {
                        if (u.getHistory().containsKey(movie.getTitle())) {
                            views += u.getHistory().getOrDefault(movie.getTitle(), 0);
                        }
                    }
                }
            }
            for (SerialInputData show : input.getSerials()) {
                if (show.getGenres().contains(genre)) {
                    for (UserInputData u : input.getUsers()) {
                        if (u.getHistory().containsKey(show.getTitle())) {
                            views += u.getHistory().getOrDefault(show.getTitle(), 0);
                        }
                    }
                }
            }
            Video v = new Video(views, genre);
            popular.add(v);
        }
        Collections.sort(popular, new SortbyViews());
        boolean ok = false;
        for (Video genre : popular) {
            for (MovieInputData movie : input.getMovies()) {
                if (movie.getGenres().contains(genre.getGenre())) {
                    if (!user.getHistory().containsKey(movie.getTitle())) {
                        ok = true;
                        try {
                            result = fileWriter.writeFile(id, "message",
                                    "PopularRecommendation result: " + movie.getTitle());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        arrayResult.add(result);
                        break;
                    }
                }
            }
            if (!ok) {
                for (SerialInputData show : input.getSerials()) {
                    if (show.getGenres().contains(genre.getGenre())) {
                        if (!user.getHistory().containsKey(show.getTitle())) {
                            ok = true;
                            try {
                                result = fileWriter.writeFile(id, "message",
                                        "PopularRecommendation result: " + show.getTitle());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            arrayResult.add(result);
                            break;
                        }
                    }
                }
            }
            if (ok) {
                break;
            }
        }
        if (!ok) {
            try {
                result = fileWriter.writeFile(id, "message",
                        "PopularRecommendation cannot be applied!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            arrayResult.add(result);
        }
    }
}
