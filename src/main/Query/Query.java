package main.Query;

import main.Rating;

import java.util.ArrayList;

public class Query {

    /**
     * @param movieratings lista ce contine toate rating-urile date pentru filme
     * @param showratings  lista ce contine toate rating-urile date pentru seriale
     */
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

}
