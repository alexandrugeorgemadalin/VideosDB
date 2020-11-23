package main.Query;

import entertainment.Season;
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
import java.util.List;

public class VideoQuery {
    public static final class Data {
        private String name;
        private Double rating = 0.0;
        private Integer duration;
        private Integer noviews;
        private ArrayList<String> genre;
        private Integer nofav;

        public Data() {
        }

        public Integer getNofav() {
            return nofav;
        }

        public void setNofav(final Integer nofav) {
            this.nofav = nofav;
        }

        public ArrayList<String> getGenre() {
            return genre;
        }

        public void setGenre(final ArrayList<String> genre) {
            this.genre = genre;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(final Double rating) {
            this.rating = rating;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(final Integer duration) {
            this.duration = duration;
        }

        public Integer getNoviews() {
            return noviews;
        }

        public void setNoviews(final Integer noviews) {
            this.noviews = noviews;
        }
    }

    /**
     * @param moviesdata   lista ce contine o baza de date despre toate filmele
     * @param showsdata    lista ce contine o baza de date despre toate serialele
     * @param input        datele de intrare
     * @param movieratings lista in care sunt adaugate toate rating-urile date pentru filme
     * @param showratings  lista in care sunt adaugate toate rating-urile date pentru seriale
     */
    public static void getdata(final ArrayList<Data> moviesdata,
                               final ArrayList<Data> showsdata,
                               final Input input, final ArrayList<Rating.Movie> movieratings,
                               final ArrayList<Rating.Show> showratings) {
        List<MovieInputData> movies = input.getMovies();
        List<SerialInputData> shows = input.getSerials();
        List<UserInputData> users = input.getUsers();
        for (MovieInputData movie : movies) {
            Data data = new Data();
            data.setName(movie.getTitle());
            data.setDuration(movie.getDuration());
            data.setGenre(movie.getGenres());
            data.genre.add(String.valueOf(movie.getYear()));
            int noviews = 0;
            for (UserInputData user : input.getUsers()) {
                if (user.getHistory().containsKey(movie.getTitle())) {
                    noviews += user.getHistory().getOrDefault(data.getName(), 0);
                }
            }
            data.setNoviews(noviews);
            for (Rating.Movie rmovie : movieratings) {
                if (rmovie.getName().equals(data.getName())) {
                    data.setRating(rmovie.getAveragerating());
                    break;
                }
            }
            int nofavs = 0;
            for (UserInputData user : users) {
                if (user.getFavoriteMovies().contains(data.getName())) {
                    nofavs += 1;
                }
            }
            data.setNofav(nofavs);
            moviesdata.add(data);
        }
        for (SerialInputData show : shows) {
            Data data = new Data();
            data.setName(show.getTitle());
            data.setGenre(show.getGenres());
            data.genre.add(String.valueOf(show.getYear()));
            int time = 0;
            for (Season season : show.getSeasons()) {
                time += season.getDuration();
            }
            data.setDuration(time);
            int noviews = 0;
            for (UserInputData user : input.getUsers()) {
                if (user.getHistory().containsKey(show.getTitle())) {
                    noviews += user.getHistory().getOrDefault(data.getName(), 0);
                }
            }
            data.setNoviews(noviews);
            for (Rating.Show rshow : showratings) {
                if (rshow.getName().equals(data.getName())) {
                    data.setRating(rshow.getAveragerating());
                    break;
                }
            }
            int nofavs = 0;
            for (UserInputData user : users) {
                if (user.getFavoriteMovies().contains(data.getName())) {
                    nofavs += 1;
                }
            }
            data.setNofav(nofavs);
            showsdata.add(data);
        }
    }

    class SortbyRating implements Comparator<Data> {
        public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
            if (a.getRating().compareTo(b.getRating()) == 0) {
                return a.getName().compareTo(b.getName());
            }
            return a.getRating().compareTo(b.getRating());
        }
    }

    class SortbyRatingDesc implements Comparator<VideoQuery.Data> {
        public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
            if (b.getRating().compareTo(a.getRating()) == 0) {
                return b.getName().compareTo(a.getName());
            }
            return b.getRating().compareTo(a.getRating());
        }
    }

    /**
     * @param id          id-ul comenzii
     * @param sorttype    tipul sortarii
     * @param videotype   tipul videoclipului
     * @param number      numarul de elemente ce trebuiesc returnate
     * @param moviesdata  lista ce contine o baza de date despre toate filmele
     * @param showsdata   lista ce contine o baza de date despre toate serialele
     * @param filters     filtrele ce trebuiau verificate
     * @param arrayResult rezultatul ce urmeaza sa fie scris
     * @param fileWriter  The file where the data will be written
     */
    public final void rating(final int id, final String sorttype,
                             final String videotype, final int number,
                             final ArrayList<Data> moviesdata,
                             final ArrayList<Data> showsdata,
                             final List<List<String>> filters,
                             final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        if (videotype.equals("movies")) {
            ArrayList<String> sortedmovies = new ArrayList<>();
            Collections.sort(moviesdata, new SortbyRating());
            int index = 0;
            boolean ok;
            for (Data movie : moviesdata) {
                if (index == number | movie.getRating() == null) {
                    break;
                }
                ok = true;
                for (List<String> filter : filters) {
                    if (filter != null) {
                        for (String subfilter : filter) {
                            if (subfilter != null) {
                                if (!movie.getGenre().contains(subfilter)) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (ok & !sortedmovies.contains(movie.getName()) & movie.getRating() > 0) {
                    sortedmovies.add(movie.getName());
                    index += 1;
                }
            }

            if (sorttype.equals("asc")) {
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + sortedmovies.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Collections.reverse(sortedmovies);
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + sortedmovies.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            arrayResult.add(result);
        } else {
            ArrayList<String> sortedshows = new ArrayList<>();
            if (sorttype.equals("asc")) {
                Collections.sort(showsdata, new SortbyRating());
            } else {
                Collections.sort(showsdata, new SortbyRatingDesc());
            }
            int index = 0;
            boolean ok;
            for (Data show : showsdata) {
                if (index == number | show.getRating() == null) {
                    break;
                }
                ok = true;
                for (List<String> filter : filters) {
                    if (filter != null) {
                        for (String subfilter : filter) {
                            if (subfilter != null) {
                                if (!show.getGenre().contains(subfilter)) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (ok & !sortedshows.contains(show.getName()) & show.getRating() != 0) {
                    sortedshows.add(show.getName());
                    index += 1;
                }
            }
            try {
                result = fileWriter.writeFile(id, "message",
                        "Query result: " + sortedshows.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            arrayResult.add(result);

        }
    }


    class SortbyDuration implements Comparator<VideoQuery.Data> {
        public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
            if (a.getDuration().compareTo(b.getDuration()) == 0) {
                return a.getName().compareTo(b.getName());
            }
            return a.getDuration().compareTo(b.getDuration());
        }
    }

    /**
     * @param id          id-ul comenzii
     * @param sorttype    tipul sortarii
     * @param videotype   tipul videoclipului
     * @param number      numarul de elemente ce trebuiesc returnate
     * @param moviesdata  lista ce contine o baza de date despre toate filmele
     * @param showsdata   lista ce contine o baza de date despre toate serialele
     * @param filters     filtrele ce trebuiau verificate
     * @param arrayResult rezultatul ce urmeaza sa fie scris
     * @param fileWriter  The file where the data will be written
     */
    public final void longest(final int id, final String sorttype,
                              final String videotype, final int number,
                              final ArrayList<Data> moviesdata,
                              final ArrayList<Data> showsdata,
                              final List<List<String>> filters,
                              final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        if (videotype.equals("movies")) {
            ArrayList<String> sortedmovies = new ArrayList<>();
            Collections.sort(moviesdata, new SortbyDuration());
            int index = 0;
            boolean ok;
            for (Data movie : moviesdata) {
                if (index == number | movie.getRating() == null) {
                    break;
                }
                ok = true;
                for (List<String> filter : filters) {
                    if (filter != null) {
                        for (String subfilter : filter) {
                            if (subfilter != null) {
                                if (!movie.getGenre().contains(subfilter)) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (ok & !sortedmovies.contains(movie.getName())) {
                    sortedmovies.add(movie.getName());
                    index += 1;
                }
            }
            if (sorttype.equals("asc")) {
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + sortedmovies.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Collections.reverse(sortedmovies);
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + sortedmovies.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            arrayResult.add(result);
        } else {
            ArrayList<String> sortedshows = new ArrayList<>();
            Collections.sort(showsdata, new SortbyDuration());
            int index = 0;
            boolean ok;
            for (Data show : showsdata) {
                if (index == number | show.getRating() == null) {
                    break;
                }
                ok = true;
                for (List<String> filter : filters) {
                    if (filter != null) {
                        for (String subfilter : filter) {
                            if (subfilter != null) {
                                if (!show.getGenre().contains(subfilter)) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (ok & !sortedshows.contains(show.getName())) {
                    sortedshows.add(show.getName());
                    index += 1;
                }
            }
            if (sorttype.equals("asc")) {
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + sortedshows.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Collections.reverse(sortedshows);
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + sortedshows.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            arrayResult.add(result);

        }
    }

    class SortbyViews implements Comparator<VideoQuery.Data> {
        public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
            if (a.getNoviews().compareTo(b.getNoviews()) == 0) {
                return a.getName().compareTo(b.getName());
            }
            return a.getNoviews().compareTo(b.getNoviews());
        }
    }

    class SortbyViewsDesc implements Comparator<VideoQuery.Data> {
        public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
            if (b.getNoviews().compareTo(a.getNoviews()) == 0) {
                return b.getName().compareTo(a.getName());
            }
            return b.getNoviews().compareTo(a.getNoviews());
        }
    }

    /**
     * @param id          id-ul comenzii
     * @param sorttype    tipul sortarii
     * @param videotype   tipul videoclipului
     * @param number      numarul de elemente ce trebuiesc returnate
     * @param moviesdata  lista ce contine o baza de date despre toate filmele
     * @param showsdata   lista ce contine o baza de date despre toate serialele
     * @param filters     filtrele ce trebuiau verificate
     * @param arrayResult rezultatul ce urmeaza sa fie scris
     * @param fileWriter  The file where the data will be written
     */
    public final void mostviewed(final int id, final String sorttype,
                                 final String videotype, final int number,
                                 final ArrayList<Data> moviesdata,
                                 final ArrayList<Data> showsdata,
                                 final List<List<String>> filters,
                                 final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        if (videotype.equals("movies")) {
            ArrayList<String> sortedmovies = new ArrayList<>();
            if (sorttype.equals("asc")) {
                Collections.sort(moviesdata, new SortbyViews());
            } else {
                Collections.sort(moviesdata, new SortbyViewsDesc());
            }
            int index = 0;
            boolean ok;
            for (Data movie : moviesdata) {
                if (index == number | movie.getRating() == null) {
                    break;
                }
                ok = true;
                for (List<String> filter : filters) {
                    if (filter != null) {
                        for (String subfilter : filter) {
                            if (subfilter != null) {
                                if (!movie.getGenre().contains(subfilter)) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (ok) {
                    if (movie.getNoviews() != 0 & !sortedmovies.contains(movie.getName())) {
                        sortedmovies.add(movie.getName());
                        index += 1;
                    }
                }
            }
            try {
                result = fileWriter.writeFile(id, "message",
                        "Query result: " + sortedmovies.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            arrayResult.add(result);
        } else {
            ArrayList<String> sortedshows = new ArrayList<>();
            if (sorttype.equals("asc")) {
                Collections.sort(showsdata, new SortbyViews());
            } else {
                Collections.sort(showsdata, new SortbyViewsDesc());
            }
            int index = 0;
            boolean ok;
            for (Data show : showsdata) {
                if (index == number | show.getRating() == null) {
                    break;
                }
                ok = true;
                for (List<String> filter : filters) {
                    if (filter != null) {
                        for (String subfilter : filter) {
                            if (subfilter != null) {
                                if (!show.getGenre().contains(subfilter)) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (ok) {
                    if (show.getNoviews() != 0 & !sortedshows.contains(show.getName())) {
                        sortedshows.add(show.getName());
                        index += 1;
                    }
                }
            }
            try {
                result = fileWriter.writeFile(id, "message",
                        "Query result: " + sortedshows.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            arrayResult.add(result);

        }
    }

    class SortbyFavorite implements Comparator<VideoQuery.Data> {
        public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
            if (a.getNofav().compareTo(b.getNofav()) == 0) {
                return a.getName().compareTo(b.getName());
            }
            return a.getNofav().compareTo(b.getNofav());
        }
    }

    class SortbyFavoriteDesc implements Comparator<VideoQuery.Data> {
        public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
            if (b.getNofav().compareTo(a.getNofav()) == 0) {
                return b.getName().compareTo(a.getName());
            }
            return b.getNofav().compareTo(a.getNofav());
        }
    }

    /**
     * @param id          id-ul comenzii
     * @param sorttype    tipul sortarii
     * @param videotype   tipul videoclipului
     * @param number      numarul de elemente ce trebuiesc returnate
     * @param moviesdata  lista ce contine o baza de date despre toate filmele
     * @param showsdata   lista ce contine o baza de date despre toate serialele
     * @param filters     filtrele ce trebuiau verificate
     * @param arrayResult rezultatul ce urmeaza sa fie scris
     * @param fileWriter  The file where the data will be written
     */
    public final void favorite(final int id, final String sorttype,
                               final String videotype, final int number,
                               final ArrayList<Data> moviesdata,
                               final ArrayList<Data> showsdata,
                               final List<List<String>> filters,
                               final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        if (videotype.equals("movies")) {
            ArrayList<String> sortedmovies = new ArrayList<>();
            if (sorttype.equals("asc")) {
                Collections.sort(moviesdata, new SortbyFavorite());
            } else {
                Collections.sort(moviesdata, new SortbyFavoriteDesc());
            }
            int index = 0;
            boolean ok;
            for (Data movie : moviesdata) {
                if (index == number | movie.getRating() == null) {
                    break;
                }
                ok = true;
                for (List<String> filter : filters) {
                    if (filter != null) {
                        for (String subfilter : filter) {
                            if (subfilter != null) {
                                if (!movie.getGenre().contains(subfilter)) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (ok) {
                    if (movie.getNofav() != 0) {
                        if (!sortedmovies.contains(movie.getName())) {
                            sortedmovies.add(movie.getName());
                            index += 1;
                        }
                    }
                }
            }
            try {
                result = fileWriter.writeFile(id, "message",
                        "Query result: " + sortedmovies.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            arrayResult.add(result);
        } else {
            ArrayList<String> sortedshows = new ArrayList<>();
            Collections.sort(showsdata, new SortbyFavorite());
            int index = 0;
            boolean ok;
            for (Data show : showsdata) {
                if (index == number | show.getRating() == null) {
                    break;
                }
                ok = true;
                for (List<String> filter : filters) {
                    if (filter != null) {
                        for (String subfilter : filter) {
                            if (subfilter != null) {
                                if (!show.getGenre().contains(subfilter)) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (ok) {
                    if (show.getNofav() != 0) {
                        if (!sortedshows.contains(show.getName())) {
                            sortedshows.add(show.getName());
                            index += 1;
                        }
                    }
                }
            }
            if (sorttype.equals("asc")) {
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + sortedshows.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Collections.reverse(sortedshows);
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + sortedshows.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            arrayResult.add(result);

        }
    }
}
