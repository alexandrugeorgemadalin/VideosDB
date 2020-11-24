package main.Query;

import actor.ActorsAwards;
import fileio.ActorInputData;
import fileio.Writer;
import main.Rating;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ActorsQuery {

    public static final class Actors {
        private String name;
        private Double rating;
        private Integer awardsnumber;


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

    static class SortbyRating implements Comparator<Actors> {
        public int compare(final Actors a, final Actors b) {
            if (a.rating.compareTo(b.rating) == 0) {
                return a.name.compareTo(b.name);
            }
            return a.rating.compareTo(b.rating);
        }
    }

    static class SortbyRatingDesc implements Comparator<Actors> {
        public int compare(final Actors a, final Actors b) {
            if (b.rating.compareTo(a.rating) == 0) {
                return b.name.compareTo(a.name);
            }
            return b.rating.compareTo(a.rating);
        }
    }

    /**
     * @param id           id-ul comenzii
     * @param number       numarul de elemente ce trebuiesc returnate
     * @param movieratings lista ce contine toate rating-urile date pentru filme
     * @param sorttype     tipul sortarii
     * @param showratings  lista ce contine toate rating-urile date pentru seriale
     * @param actors       datele de intrare ale actorilor
     * @param arrayResult  rezultatul ce urmeaza sa fie scris
     * @param fileWriter   The file where the data will be written
     */
    public static void average(final int id, final int number,
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

    static class SortbyAwards implements Comparator<Actors> {
        public int compare(final Actors a, final Actors b) {
            if (a.awardsnumber.compareTo(b.awardsnumber) == 0) {
                return a.name.compareTo(b.name);
            }
            return a.awardsnumber.compareTo(b.awardsnumber);
        }
    }

    /**
     * @param id          id-ul comenzii
     * @param sorttype    tipul sortarii
     * @param awards      liste de premii ce trebuiesc verificate
     * @param actors      datele de intrare ale actorilor
     * @param arrayResult rezultatul ce urmeaza sa fie scris
     * @param fileWriter  The file where the data will be written
     */
    public static void awards(final int id, final String sorttype,
                              final List<String> awards,
                              final List<ActorInputData> actors,
                              final JSONArray arrayResult, final Writer fileWriter) {
        JSONObject result = new JSONObject();
        ArrayList<Actors> awardedactors = new ArrayList<>();
        int awardsnum;
        for (ActorInputData actor : actors) {
            awardsnum = 0;
            for (Integer noaward : actor.getAwards().values()) {
                awardsnum += noaward;
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

    /**
     * @param id          id-ul comenzii
     * @param sorttype    tipul sortarii
     * @param filters     filtrele ce trebuiesc verificate
     * @param actors      datele de intrare ale actorilor
     * @param arrayResult rezultatul ce urmeaza sa fie scris
     * @param fileWriter  The file where the data will be written
     */
    public static void filter(final int id, final String sorttype,
                              final List<List<String>> filters,
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
