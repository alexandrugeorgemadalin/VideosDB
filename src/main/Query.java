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

        for (Rating.Show show1 : showratings) {
            numratings = 0;
            average = 0;
            for (Rating.Show show2 : showratings) {
                if (show1.getName().equals(show2.getName())) {
                    average += show2.getRating();
                    numratings += 1;
                }
            }
            average /= numratings;
            average /= show1.getSeasonnumber();
            show1.setAveragerating(average);
        }
    }

    public static final class ActorsQuery {

        class Actors {
            private String name;
            private Double rating;
            private Integer awardsnumber;

            public Integer getAwardsnumber() {
                return awardsnumber;
            }

            public void setAwardsnumber(Integer awardsnumber) {
                this.awardsnumber = awardsnumber;
            }

            Actors(final String name, final Double rating) {
                this.name = name;
                this.rating = rating;
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
            Collections.sort(ratedactors, new SortbyRating());
            if (sorttype.equals("asc")) {
                ArrayList<String> names = new ArrayList<String>();
                for (int i = 0; i < number; i++) {
                    if (ratedactors.get(i).getRating() != null) {
                        names.add(ratedactors.get(i).name);
                    }
                }
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + names.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                arrayResult.add(result);
            } else {
                ArrayList<String> names = new ArrayList<String>();
                for (int i = ratedactors.size() - 1; i >= ratedactors.size() - number; i -= 1) {
                    if (ratedactors.get(i).getRating() != null) {
                        names.add(ratedactors.get(i).name);
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
            ArrayList<Actors> awardedactors = new ArrayList<Actors>();
            int awardsnum;
            for (ActorInputData actor : actors) {
                awardsnum = 0;
                for (String award : awards) {
                    if (actor.getAwards().containsKey(ActorsAwards.valueOf(award))) {
                        awardsnum += actor.getAwards().get(ActorsAwards.valueOf(award));
                    } else {
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
            if (sorttype.equals("asc")) {
                ArrayList<String> names = new ArrayList<String>();
                for (int i = 0; i < number & i < awardedactors.size(); i++) {
                    if (awardedactors.get(i).getAwardsnumber() != null) {
                        names.add(awardedactors.get(i).name);
                    }
                }
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + names.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                arrayResult.add(result);
            } else {
                ArrayList<String> names = new ArrayList<String>();
                for (int i = awardedactors.size() - 1;
                     i >= awardedactors.size() - number & i > 0; i -= 1) {
                    if (awardedactors.get(i).getAwardsnumber() != null) {
                        names.add(awardedactors.get(i).name);
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
        }

        public void filter(final int id, final String sorttype, List<List<String>> filters,
                           final List<ActorInputData> actors,
                           final JSONArray arrayResult, final Writer fileWriter) {
            JSONObject result = new JSONObject();
            ArrayList<String> filteredactors = new ArrayList<String>();
            boolean ok;
            for (ActorInputData actor : actors) {
                ok = true;
                for (List<String> filter : filters) {
                    if (filter != null) {
                        for (String subfilter : filter) {
                            if (subfilter != null) {
                                if (!actor.getCareerDescription().contains(subfilter)) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (ok) {
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

            public Data(String name, Double rating,
                        Integer duration, Integer no_views,
                        ArrayList<String> genre, Integer no_fav) {
                this.name = name;
                this.rating = rating;
                this.duration = duration;
                this.no_views = no_views;
                this.genre = genre;
                this.no_fav = no_fav;
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
                        ", duration=" + duration +
                        ", no_views=" + no_views +
                        ", genre=" + genre +
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
                    if (user.getHistory().containsKey(data.getName())) {
                        no_favs += user.getHistory().getOrDefault(data.getName(), 0);
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
                    if (user.getHistory().containsKey(data.getName())) {
                        no_favs += user.getHistory().getOrDefault(data.getName(), 0);
                    }
                }
                data.setNo_fav(no_favs);
                showsdata.add(data);
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

        public void rating(final int id, final String sorttype,
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
                    if (ok) {
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
                    if (ok) {
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

        class SortbyRating implements Comparator<VideoQuery.Data> {
            public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
                if (a.getRating().compareTo(b.getRating()) == 0) {
                    return a.getName().compareTo(b.getName());
                }
                return a.getRating().compareTo(b.getRating());
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
                    if (ok) {
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
                Collections.sort(showsdata, new SortbyRating());
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


        public void mostviewed(final int id, final String sorttype,
                               final String videotype, final int number,
                               final ArrayList<Data> moviesdata,
                               final ArrayList<Data> showsdata,
                               List<List<String>> filters,
                               final JSONArray arrayResult, final Writer fileWriter) {
            JSONObject result = new JSONObject();
            if (videotype.equals("movies")) {
                ArrayList<String> sortedmovies = new ArrayList<>();
                Collections.sort(moviesdata, new SortbyViews());
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
                        if (movie.getNo_views() != 0) {
                            sortedmovies.add(movie.getName());
                            index += 1;
                        }
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
                Collections.sort(showsdata, new SortbyViews());
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
                        if (show.getNo_views() != 0) {
                            sortedshows.add(show.getName());
                            index += 1;
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

        class SortbyFavorite implements Comparator<VideoQuery.Data> {
            public int compare(final VideoQuery.Data a, final VideoQuery.Data b) {
                if (a.getNo_fav().compareTo(b.getNo_fav()) == 0) {
                    return a.getName().compareTo(b.getName());
                }
                return a.getNo_fav().compareTo(b.getNo_fav());
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
                Collections.sort(moviesdata, new SortbyFavorite());
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
                            if(!sortedmovies.contains(movie.getName())) {
                                sortedmovies.add(movie.getName());
                                index += 1;
                            }
                        }
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
                            if(!sortedshows.contains(show.getName())) {
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

            public User() {

            }

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
            if (sorttype.equals("asc")) {
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + ratedusers.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Collections.reverse(ratedusers);
                try {
                    result = fileWriter.writeFile(id, "message",
                            "Query result: " + ratedusers.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            arrayResult.add(result);

        }
    }
}
