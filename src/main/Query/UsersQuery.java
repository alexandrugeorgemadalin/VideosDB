package main.Query;

import fileio.Input;
import fileio.UserInputData;
import fileio.Writer;
import main.Rating;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UsersQuery {
    static final class User {
        private String username;
        private Integer ratings;

        @Override
        public String toString() {
            return username;

        }

        User(final String username, final Integer ratings) {
            this.username = username;
            this.ratings = ratings;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public Integer getRatings() {
            return ratings;
        }

        public void setRatings(final Integer ratings) {
            this.ratings = ratings;
        }
    }

    class SortbyRatings implements Comparator<User> {
        public int compare(final User a, final User b) {
            if (a.getRatings().compareTo(b.getRatings()) == 0) {
                return a.getUsername().compareTo(b.getUsername());
            }
            return a.getRatings().compareTo(b.getRatings());
        }
    }

    /**
     * @param id           id-ul comenzii
     * @param sorttype     tipul sortarii
     * @param number       numarul de elemente ce trebuiesc returnate
     * @param input        datele de intrare
     * @param movieratings lista in care sunt adaugate toate rating-urile date pentru filme
     * @param showratings  lista in care sunt adaugate toate rating-urile date pentru seriale
     * @param arrayResult  rezultatul ce urmeaza sa fie scris
     * @param fileWriter   The file where the data will be written
     */
    public final void ratingsnumber(final int id, final String sorttype,
                                    final int number, final Input input,
                                    final ArrayList<Rating.Movie> movieratings,
                                    final ArrayList<Rating.Show> showratings,
                                    final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        ArrayList<User> ratedusers = new ArrayList<>();
        List<UserInputData> users = input.getUsers();
        int ratings;
        for (UserInputData user : users) {
            ratings = 0;
            for (Rating.Movie movie : movieratings) {
                if (movie.getUsername().equals(user.getUsername())) {
                    ratings += 1;
                }
            }
            for (Rating.Show show : showratings) {
                if (show.getUsername().equals(user.getUsername())) {
                    ratings += 1;
                }
            }
            if (ratings != 0) {
                User u = new User(user.getUsername(), ratings);
                ratedusers.add(u);
            }
        }
        Collections.sort(ratedusers, new SortbyRatings());
        if (sorttype.equals("desc")) {
            Collections.reverse(ratedusers);
        }
        ArrayList<String> names = new ArrayList<>();
        int index = 0;
        for (User user : ratedusers) {
            if (index != number) {
                index++;
                names.add(user.getUsername());
            }
        }
        try {
            result = fileWriter.writeFile(id, "message",
                    "Query result: " + names);
        } catch (IOException e) {
            e.printStackTrace();
        }

        arrayResult.add(result);

    }
}
