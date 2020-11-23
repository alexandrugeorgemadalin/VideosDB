package main;

import actor.ActorsAwards;
import entertainment.Season;
import fileio.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;

public class Query {

    public static void calculateratings(final ArrayList<Rating.Movie> movieratings,
                                        final ArrayList<Rating.Show> showratings) {
        int numratings;
        double average;
        for (Rating.Movie movie1 : movieratings) {
            numratings = 0;
            average = 0;
            for (Rating.Movie movie2 : movieratings) {
                if (movie1.getName().equals(movie2.getName())) {
                    average += movie2.getRating();
                    numratings += 1;
                }
            }
            average /= numratings;
            movie1.setAveragerating(average);
        }

        for (Rating.Show show : showratings) {
            double[] ratings = new double[show.getSeasonnumber()];
            int[] noratings = new int[show.getSeasonnumber()];
            for (int i = 0; i < show.getSeasonnumber(); i++) {
                ratings[i] = 0;
                noratings[i] = 0;
            }
            for (Rating.Show show2 : showratings) {
                if (show.getName().equals(show2.getName())) {
                    ratings[show2.getSeason() - 1] += show2.getRating();
                    noratings[show2.getSeason() - 1] += 1;
                }
            }
            average = 0;
            for (int i = 0; i < show.getSeasonnumber(); i++) {
                if (ratings[i] != 0) {
                    average += ratings[i] / noratings[i];
                }
            }
            average /= show.getSeasonnumber();
            show.setAveragerating(average);
        }
    }

    public static final class ActorsQuery {

        class Actors {
            private String name;
            private Double rating;
            private Integer awardsnumber;


            Actors(final String name, final Double rating) {
                this.name = name;
                this.rating = rating;
            }

            @Override
            public String toString() {
                return "Actors{" +
                        "name='" + name + '\'' +
                        ", rating=" + rating +
                        '}';
            }

            Actors(final String name, final int awardsnumber) {
                this.name = name;
                this.awardsnumber = awardsnumber;
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
        }

        class SortbyRating implements Comparator<Actors> {
            public int compare(final Actors a, final Actors b) {
                if (a.rating.compareTo(b.rating) == 0) {
                    return a.name.compareTo(b.name);
                }
                return a.rating.compareTo(b.rating);
            }
        }

        class SortbyRatingDesc implements Comparator<Actors> {
            public int compare(final Actors a, final Actors b) {
                if (b.rating.compareTo(a.rating) == 0) {
                    return b.name.compareTo(a.name);
                }
                return b.rating.compareTo(a.rating);
            }
        }

        public void average(final int id, final int number,
                            final ArrayList<Rating.Movie> movieratings,
                            final String sorttype,
                            final ArrayList<Rating.Show> showratings,
                            final List<ActorInputData> actors,
                            final JSONArray arrayResult, final Writer fileWriter) {
            JSONObject result = new JSONObject();
            ArrayList<Actors> ratedactors = new ArrayList<Actors>();
            Double average;
            int numberofratings;
            for (ActorInputData actor : actors) {
                average = 0.00;
                numberofratings = 0;
                for (String video : actor.getFilmography()) {
                    for (Rating.Movie movie : movieratings) {
                        if (movie.getName().equals(video)) {
                            average += movie.getAveragerating();
                            numberofratings += 1;
                            break;
                        }
                    }
                    for (Rating.Show show : showratings) {
                        if (show.getName().equals(video)) {
                            average += show.getAveragerating();
                            numberofratings += 1;
                            break;
                        }
                    }
                }
                average /= numberofratings;
                Actors act = new Actors(actor.getName(), average);
                ratedactors.add(act);
            }

            if (sorttype.equals("asc")) {
                Collections.sort(ratedactors, new SortbyRating());
            } else {
                Collections.sort(ratedactors, new SortbyRatingDesc());
            }

            ArrayList<String> names = new ArrayList<String>();
            int index = 0;
            for (int i = 0; i < ratedactors.size(); i++) {
                if (index == number) {
                    break;
                }
                if (ratedactors.get(i).getRating() > 0) {
                    names.add(ratedactors.get(i).name);
                    index++;
                }
            }
            try {
                result = fileWriter.writeFile(id, "message",
                        "Query result: " + names.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            arrayResult.add(result);

        }

        class SortbyAwards implements Comparator<Actors> {
            public int compare(final Actors a, final Actors b) {
                if (a.awardsnumber.compareTo(b.awardsnumber) == 0) {
                    return a.name.compareTo(b.name);
                }
                return a.awardsnumber.compareTo(b.awardsnumber);
            }
        }

        public void awards(final int id, final int number, final String sorttype,
                           final List<String> awards,
                           final List<ActorInputData> actors,
                           final JSONArray arrayResult, final Writer fileWriter) {
            JSONObject result = new JSONObject();
            ArrayList<Actors> awardedactors = new ArrayList<>();
            boolean ok = true;
            int awardsnum;
            for (ActorInputData actor : actors) {
                awardsnum = 0;
                for (Integer no_award : actor.getAwards().values()) {
                    awardsnum += no_award;
                }
                for (String award : awards) {
                    if (!actor.getAwards().containsKey(ActorsAwards.valueOf(award))) {
                        awardsnum = -1;
                        break;
                    }
                }
                if (awardsnum != -1) {
                    Actors a = new Actors(actor.getName(), awardsnum);
                    awardedactors.add(a);
                }
            }

            Collections.sort(awardedactors, new SortbyAwards());
            ArrayList<String> names = new ArrayList<>();
            for (Actors actor : awardedactors) {
                names.add(actor.getName());
            }

            if (sorttype.equals("asc")) {
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + names.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                arrayResult.add(result);
            } else {
                Collections.reverse(names);
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + names.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                arrayResult.add(result);
            }
        }

        public void filter(final int id, final String sorttype, List<List<String>> filters,
                           final List<ActorInputData> actors,
                           final JSONArray arrayResult, final Writer fileWriter) {
            JSONObject result = new JSONObject();
            ArrayList<String> filteredactors = new ArrayList<>();
            int found, filtersno;
            for (ActorInputData actor : actors) {
                found = 0;
                filtersno = 0;
                String[] words = actor.getCareerDescription().split("[-.,;()\\s]+");
                for (List<String> filter : filters) {
                    if (filter != null) {
                        for (String subfilter : filter) {
                            if (subfilter != null) {
                                filtersno += 1;
                                for (String w : words) {
                                    if (w.toLowerCase().equals(subfilter)) {
                                        found += 1;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (found == filtersno) {
                    filteredactors.add(actor.getName());
                }
            }
            Collections.sort(filteredactors);
            if (sorttype.equals("asc")) {
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + filteredactors.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Collections.reverse(filteredactors);
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + filteredactors.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            arrayResult.add(result);
        }
    }

    public static class VideoQuery {
        static class Data {
            private String name;
            private Double rating = 0.0;
            private Integer duration;
            private Integer no_views;
            private ArrayList<String> genre;
            private Integer no_fav;

            public Data() {
            }


            public Integer getNo_fav() {
                return no_fav;
            }

            public void setNo_fav(Integer no_fav) {
                this.no_fav = no_fav;
            }


            @Override
            public String toString() {
                return "Data{" +
                        "name='" + name + '\'' +
                        ", rating=" + rating +
                        '}';
            }

            public ArrayList<String> getGenre() {
                return genre;
            }

            public void setGenre(ArrayList<String> genre) {
                this.genre = genre;
            }


            public String getName() {
                return name;
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

            public Integer getDuration() {
                return duration;
            }

            public void setDuration(Integer duration) {
                this.duration = duration;
            }

            public Integer getNo_views() {
                return no_views;
            }

            public void setNo_views(Integer no_views) {
                this.no_views = no_views;
            }
        }


        static void getdata(ArrayList<Data> moviesdata,
                            ArrayList<Data> showsdata,
                            Input input, ArrayList<Rating.Movie> movieratings,
                            ArrayList<Rating.Show> showratings) {
            List<MovieInputData> movies = input.getMovies();
            List<SerialInputData> shows = input.getSerials();
            List<UserInputData> users = input.getUsers();
            for (MovieInputData movie : movies) {
                Data data = new Data();
                data.setName(movie.getTitle());
                data.setDuration(movie.getDuration());
                data.setGenre(movie.getGenres());
                data.genre.add(String.valueOf(movie.getYear()));
                int no_views = 0;
                for (UserInputData user : input.getUsers()) {
                    if (user.getHistory().containsKey(movie.getTitle()))
                        no_views += user.getHistory().getOrDefault(data.getName(), 0);
                }
                data.setNo_views(no_views);
                for (Rating.Movie rmovie : movieratings) {
                    if (rmovie.getName().equals(data.getName())) {
                        data.setRating(rmovie.getAveragerating());
                        break;
                    }
                }
                int no_favs = 0;
                for (UserInputData user : users) {
                    if (user.getFavoriteMovies().contains(data.getName())) {
                        no_favs += 1;
                    }
                }
                data.setNo_fav(no_favs);
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
                int no_views = 0;
                for (UserInputData user : input.getUsers()) {
                    if (user.getHistory().containsKey(show.getTitle()))
                        no_views += user.getHistory().getOrDefault(data.getName(), 0);
                }
                data.setNo_views(no_views);
                for (Rating.Show rshow : showratings) {
                    if (rshow.getName().equals(data.getName())) {
                        data.setRating(rshow.getAveragerating());
                        break;
                    }
                }
                int no_favs = 0;
                for (UserInputData user : users) {
                    if (user.getFavoriteMovies().contains(data.getName())) {
                        no_favs += 1;
                    }
                }
                data.setNo_fav(no_favs);
                showsdata.add(data);
            }
        }

        class SortbyRating implements Comparator<VideoQuery.Data> {
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

        public void rating(final int id, final String sorttype,
                           final String videotype, final int number,
                           final ArrayList<Data> moviesdata,
                           final ArrayList<Data> showsdata,
                           List<List<String>> filters,
                           final JSONArray arrayResult, final Writer fileWriter) {
            JSONObject result = new JSONObject();
            if (videotype.equals("movies")) {
                ArrayList<String> sortedmovies = new ArrayList<>();
                Collections.sort(moviesdata, new SortbyRating());
                int index = 0;
                boolean ok;
                for (Data movie : moviesdata) {
                    if (index == number | movie.getRating() == null)
                        break;
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
                    if (index == number | show.getRating() == null)
                        break;
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

        public void longest(final int id, final String sorttype,
                            final String videotype, final int number,
                            final ArrayList<Data> moviesdata,
                            final ArrayList<Data> showsdata,
                            List<List<String>> filters,
                            final JSONArray arrayResult, final Writer fileWriter) {
            JSONObject result = new JSONObject();
            if (videotype.equals("movies")) {
                ArrayList<String> sortedmovies = new ArrayList<>();
                Collections.sort(moviesdata, new SortbyDuration());
                int index = 0;
                boolean ok;
                for (Data movie : moviesdata) {
                    if (index == number | movie.getRating() == null)
                        break;
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
                    if (index == number | show.getRating() == null)
                        break;
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
                if (a.getNo_views().compareTo(b.getNo_views()) == 0) {
                    return a.getName().compareTo(b.getName());
                }
                return a.getNo_views().compareTo(b.getNo_views());
            }
        }

        class SortbyViewsDesc implements Comparator<VideoQuery.Data> {
            public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
                if (b.getNo_views().compareTo(a.getNo_views()) == 0) {
                    return b.getName().compareTo(a.getName());
                }
                return b.getNo_views().compareTo(a.getNo_views());
            }
        }

        public void mostviewed(final int id, final String sorttype,
                               final String videotype, final int number,
                               final ArrayList<Data> moviesdata,
                               final ArrayList<Data> showsdata,
                               List<List<String>> filters,
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
                    if (index == number | movie.getRating() == null)
                        break;
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
                        if (movie.getNo_views() != 0 & !sortedmovies.contains(movie.getName())) {
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
                    if (index == number | show.getRating() == null)
                        break;
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
                        if (show.getNo_views() != 0 & !sortedshows.contains(show.getName())) {
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
                if (a.getNo_fav().compareTo(b.getNo_fav()) == 0) {
                    return a.getName().compareTo(b.getName());
                }
                return a.getNo_fav().compareTo(b.getNo_fav());
            }
        }

        class SortbyFavoriteDesc implements Comparator<VideoQuery.Data> {
            public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
                if (b.getNo_fav().compareTo(a.getNo_fav()) == 0) {
                    return b.getName().compareTo(a.getName());
                }
                return b.getNo_fav().compareTo(a.getNo_fav());
            }
        }

        public void favorite(final int id, final String sorttype,
                             final String videotype, final int number,
                             final ArrayList<Data> moviesdata,
                             final ArrayList<Data> showsdata,
                             List<List<String>> filters,
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
                    if (index == number | movie.getRating() == null)
                        break;
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
                        if (movie.getNo_fav() != 0) {
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
                    if (index == number | show.getRating() == null)
                        break;
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
                        if (show.getNo_fav() != 0) {
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

    public static class UsersQuery {
        static class User {
            private String username;
            private Integer ratings;

            @Override
            public String toString() {
                return username;

            }

            public User(String username, Integer ratings) {
                this.username = username;
                this.ratings = ratings;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public Integer getRatings() {
                return ratings;
            }

            public void setRatings(Integer ratings) {
                this.ratings = ratings;
            }
        }

        class SortbyRatings implements Comparator<UsersQuery.User> {
            public int compare(final UsersQuery.User a, final UsersQuery.User b) {
                if (a.getRatings().compareTo(b.getRatings()) == 0) {
                    return a.getUsername().compareTo(b.getUsername());
                }
                return a.getRatings().compareTo(b.getRatings());
            }
        }

        public void ratingsnumber(final int id, final String sorttype,
                                  final int number, Input input,
                                  ArrayList<Rating.Movie> movieratings,
                                  ArrayList<Rating.Show> showratings,
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
}
