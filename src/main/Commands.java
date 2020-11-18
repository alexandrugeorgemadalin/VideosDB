package main;

import fileio.UserInputData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import fileio.Writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Commands {
    private JSONObject result = new JSONObject();

    public void addfavorite(final int id, final String title, final String username,
                            final List<UserInputData> usersData, final JSONArray arrayResult,
                            final Writer fileWriter) throws IOException {
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) {
                if (user.getFavoriteMovies().contains(title)) {
                    result = fileWriter.writeFile(id, "message",
                            "error -> " + title + " is already in favourite list");
                    arrayResult.add(result);
                } else {
                    Map<String, Integer> history = user.getHistory();
                    if (history.containsKey(title)) {
                        user.getFavoriteMovies().add(title);
                        result = fileWriter.writeFile(id, "message",
                                "success -> " + title + " was added as favourite");
                        arrayResult.add(result);
                    } else {
                        result = fileWriter.writeFile(id, "message",
                                "error -> " + title + " is not seen");
                        arrayResult.add(result);
                    }
                }
            }
        }
    }

    public void view(final int id, final String title, final String username,
                     final List<UserInputData> usersData, final JSONArray arrayResult,
                     final Writer fileWriter) throws IOException {
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) {
                if (user.getHistory().containsKey(title)) {
                    user.getHistory().put(String.valueOf(user.getHistory()),
                            user.getHistory().get(title) + 1);
                    result = fileWriter.writeFile(id, "message",
                            "success -> " + title + " was viewed with total views of "
                                    + user.getHistory().get(title));
                } else {
                    user.getHistory().put(String.valueOf(user.getHistory()), 1);
                    result = fileWriter.writeFile(id, "message",
                            "success -> " + title + " was viewed with total views of " + 1);
                }
                arrayResult.add(result);
            }
        }
    }

    public void ratingmovie(final int id, final String title, final String username,
                            final double rating, final ArrayList<Rating.Movie> ratings,
                            final List<UserInputData> usersData, final JSONArray arrayResult,
                            final Writer fileWriter) throws IOException {
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) {
                if (user.getHistory().containsKey(title)) {
                    Map<String, Double> data = new HashMap<>();
                    data.put(username, rating);
                    Rating.Movie movie = new Rating.Movie(title, data);
                    ratings.add(movie);
                    result = fileWriter.writeFile(id, "message",
                            "success -> " + title + " was rated with " + rating
                                    + " by " + username);
                    arrayResult.add(result);
                }
            }
        }
    }

    public void ratingshow(final int id, final String title, final int season,
                           final String username, final double rating,
                           final ArrayList<Rating.Show> ratings,
                           final List<UserInputData> usersData, final JSONArray arrayResult,
                           final Writer fileWriter) throws IOException {
        for (UserInputData user : usersData) {
            if (user.getUsername().equals(username)) {
                if (user.getHistory().containsKey(title)) {
                    Map<String, Double> data = new HashMap<>();
                    data.put(username, rating);
                    Rating.Show show = new Rating.Show(title, season, data);
                    ratings.add(show);
                    result = fileWriter.writeFile(id, "message",
                            "success -> " + title + " was rated with " + rating
                                    + " by " + username);
                    arrayResult.add(result);
                }
            }
        }
    }
}
