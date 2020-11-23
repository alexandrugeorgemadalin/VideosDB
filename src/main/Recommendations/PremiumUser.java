package main.Recommendations;

import fileio.SerialInputData;
import fileio.Writer;
import fileio.Input;
import fileio.UserInputData;
import fileio.MovieInputData;
import main.Rating;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PremiumUser {
    static class Video {
        private String name;
        private Double rating;
        private Integer views;
        private String genre;

        Video(final Integer views, final String genre) {
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

        public void setGenre(final String genre) {
            this.genre = genre;
        }

        Video(final String name, final double rating) {
            this.name = name;
            this.rating = rating;
        }

        Video(final String name, final Integer views) {
            this.name = name;
            this.views = views;
        }

        public Integer getViews() {
            return views;
        }

        public void setViews(final Integer views) {
            this.views = views;
        }


        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(final double rating) {
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

    /**
     * @param id           id-ul comenzii
     * @param user         user-ul pentru care se efectueaza comanda
     * @param genre        genurile cautarii
     * @param movieratings clasa in care sunt salvate date relevante pentru filme
     * @param showratings  clasa in care sunt salvate date relevante pentru filme
     * @param input        input-ul testului
     * @param arrayResult  rezultatul ce urmeaza sa fie scris
     * @param fileWriter   The file where the data will be written
     */
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

    /**
     * @param id          id-ul comenzii
     * @param user        user-ul pentru care se efectueaza comanda
     * @param input       input-ul testului
     * @param arrayResult rezultatul ce urmeaza sa fie scris
     * @param fileWriter  The file where the data will be written
     */
    public static void favorite(final int id, final UserInputData user,
                                final Input input,
                                final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        ArrayList<Video> videos = new ArrayList<>();
        for (MovieInputData movie : input.getMovies()) {
            Integer noviews = 0;
            if (!user.getHistory().containsKey(movie.getTitle())) {
                for (UserInputData u : input.getUsers()) {
                    if (u.getFavoriteMovies().contains(movie.getTitle())) {
                        noviews += 1;
                    }

                }
            }
            Video v = new Video(movie.getTitle(), noviews);
            videos.add(v);
        }
        for (SerialInputData show : input.getSerials()) {
            Integer noviews = 0;
            if (!user.getHistory().containsKey(show.getTitle())) {
                for (UserInputData u : input.getUsers()) {
                    if (u.getFavoriteMovies().contains(show.getTitle())) {
                        noviews += 1;
                    }
                }
            }
            Video v = new Video(show.getTitle(), noviews);
            videos.add(v);
        }
        Collections.sort(videos, new SortbyViews());
        if (videos.size() != 0) {
            boolean ok = false;
            for (Video video : videos) {
                for (UserInputData u : input.getUsers()) {
                    if (u.getFavoriteMovies().contains(video.getName())) {
                        if (!user.getHistory().containsKey(video.getName())) {
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
                }
                if (ok) {
                    break;
                }
            }
            if (!ok) {
                try {
                    result = fileWriter.writeFile(id, "message",
                            "FavoriteRecommendation cannot be applied!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                arrayResult.add(result);
            }
        } else {
            try {
                result = fileWriter.writeFile(id, "message",
                        "FavoriteRecommendation cannot be applied!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            arrayResult.add(result);
        }
    }

    /**
     * @param id          id-ul comenzii
     * @param user        user-ul pentru care se efectueaza comanda
     * @param input       input-ul testului
     * @param arrayResult rezultatul ce urmeaza sa fie scris
     * @param fileWriter  The file where the data will be written
     */
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
