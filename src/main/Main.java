package main;

import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import fileio.*;
import main.Recommendations.BasicUser;
import main.Recommendations.PremiumUser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        //TODO add here the entry point to your implementation
        ArrayList<Rating.Movie> movieratings = new ArrayList<Rating.Movie>();
        ArrayList<Rating.Show> showratings = new ArrayList<Rating.Show>();
        ArrayList<Query.VideoQuery.Data> moviesdata = new ArrayList<>();
        ArrayList<Query.VideoQuery.Data> showsdata = new ArrayList<>();

        for (int i = 0; i < input.getCommands().size(); i++) {
            if (input.getCommands().get(i).getActionType().equals("command")) {
                if (input.getCommands().get(i).getType().equals("favorite")) {
                    String title = input.getCommands().get(i).getTitle();
                    String user = input.getCommands().get(i).getUsername();
                    int id = input.getCommands().get(i).getActionId();
                    Commands commands = new Commands();
                    commands.addfavorite(id, title, user, input.getUsers(),
                            arrayResult, fileWriter);
                }
                if (input.getCommands().get(i).getType().equals("view")) {
                    String title = input.getCommands().get(i).getTitle();
                    String user = input.getCommands().get(i).getUsername();
                    int id = input.getCommands().get(i).getActionId();
                    Commands commands = new Commands();
                    commands.view(id, title, user, input.getUsers(),
                            arrayResult, fileWriter);
                }
                if (input.getCommands().get(i).getType().equals("rating")) {
                    String title = input.getCommands().get(i).getTitle();
                    String user = input.getCommands().get(i).getUsername();
                    Double rating = input.getCommands().get(i).getGrade();
                    int id = input.getCommands().get(i).getActionId();
                    int season = input.getCommands().get(i).getSeasonNumber();
                    Commands commands = new Commands();
                    if (season == 0) {
                        commands.ratingmovie(id, title, user, rating,
                                movieratings, input.getUsers(), arrayResult, fileWriter);
                    } else {
                        for (SerialInputData show : input.getSerials()) {
                            if (show.getTitle().equals(title)) {
                                commands.ratingshow(id, title, season,
                                        show.getNumberSeason(), user, rating,
                                        showratings, input.getUsers(), arrayResult, fileWriter);
                            }
                        }
                    }
                }
            }
            if (input.getCommands().get(i).getActionType().equals("query")) {
                Query.calculateratings(movieratings, showratings);
                Query.VideoQuery.getdata(moviesdata, showsdata, input, movieratings, showratings);
                int id = input.getCommands().get(i).getActionId();
                int number = input.getCommands().get(i).getNumber();
                String sorttype = input.getCommands().get(i).getSortType();
                if (input.getCommands().get(i).getObjectType().equals("actors")) {
                    Query.ActorsQuery actorsquery = new Query.ActorsQuery();
                    if (input.getCommands().get(i).getCriteria().equals("average")) {
                        actorsquery.average(id, number, movieratings, sorttype,
                                showratings, input.getActors(), arrayResult, fileWriter);
                    }
                    if (input.getCommands().get(i).getCriteria().equals("awards")) {
                        List<String> awards = input.getCommands().get(i).getFilters().get(3);
                        actorsquery.awards(id, number, sorttype, awards,
                                input.getActors(), arrayResult, fileWriter);
                    }
                    if (input.getCommands().get(i).getCriteria().equals("filter_description")) {
                        List<List<String>> filters = input.getCommands().get(i).getFilters();
                        actorsquery.filter(id, sorttype, filters,
                                input.getActors(), arrayResult, fileWriter);
                    }
                }
                if (input.getCommands().get(i).getObjectType().equals("movies")) {
                    String videotype = input.getCommands().get(i).getObjectType();
                    Query.VideoQuery videoquery = new Query.VideoQuery();
                    List<List<String>> filters = input.getCommands().get(i).getFilters();
                    if (input.getCommands().get(i).getCriteria().equals("ratings")) {
                        videoquery.rating(id, sorttype, videotype, number, moviesdata,
                                showsdata, filters, arrayResult, fileWriter);
                    }
                    if (input.getCommands().get(i).getCriteria().equals("longest")) {
                        videoquery.longest(id, sorttype, videotype, number, moviesdata,
                                showsdata, filters, arrayResult, fileWriter);
                    }
                    if (input.getCommands().get(i).getCriteria().equals("most_viewed")) {
                        videoquery.mostviewed(id, sorttype, videotype, number, moviesdata,
                                showsdata, filters, arrayResult, fileWriter);
                    }
                    if (input.getCommands().get(i).getCriteria().equals("favorite")) {
                        videoquery.favorite(id, sorttype, videotype, number, moviesdata,
                                showsdata, filters, arrayResult, fileWriter);
                    }
                }
                if (input.getCommands().get(i).getObjectType().equals("shows")) {
                    String videotype = input.getCommands().get(i).getObjectType();
                    Query.VideoQuery videoquery = new Query.VideoQuery();
                    List<List<String>> filters = input.getCommands().get(i).getFilters();
                    if (input.getCommands().get(i).getCriteria().equals("ratings")) {
                        videoquery.rating(id, sorttype, videotype, number, moviesdata,
                                showsdata, filters, arrayResult, fileWriter);
                    }
                    if (input.getCommands().get(i).getCriteria().equals("longest")) {
                        videoquery.longest(id, sorttype, videotype, number, moviesdata,
                                showsdata, filters, arrayResult, fileWriter);
                    }
                    if (input.getCommands().get(i).getCriteria().equals("most_viewed")) {
                        videoquery.mostviewed(id, sorttype, videotype, number, moviesdata,
                                showsdata, filters, arrayResult, fileWriter);
                    }
                    if (input.getCommands().get(i).getCriteria().equals("favorite")) {
                        videoquery.favorite(id, sorttype, videotype, number, moviesdata,
                                showsdata, filters, arrayResult, fileWriter);
                    }
                }
                if (input.getCommands().get(i).getObjectType().equals("users")) {
                    Query.UsersQuery usersquery = new Query.UsersQuery();
                    usersquery.ratingsnumber(id, sorttype, number, input, movieratings,
                            showratings, arrayResult, fileWriter);
                }
            }
            if (input.getCommands().get(i).getActionType().equals("recommendation")) {
                int id = input.getCommands().get(i).getActionId();
                if (input.getCommands().get(i).getType().equals("standard")) {
                    boolean ok = false;
                    for (UserInputData user : input.getUsers()) {
                        if (user.getUsername().equals(input.getCommands().get(i).getUsername())) {
                            BasicUser.standard(id, user, input, arrayResult, fileWriter);
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        JSONObject result = new JSONObject();
                        try {
                            result = fileWriter.writeFile(id, "message",
                                    "BestRatedUnseenRecommendation cannot be applied!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        arrayResult.add(result);
                    }
                }
                if (input.getCommands().get(i).getType().equals("best_unseen")) {
                    boolean ok = false;
                    for (UserInputData user : input.getUsers()) {
                        if (user.getUsername().equals(input.getCommands().get(i).getUsername())) {
                            BasicUser.bestunseed(id, user, movieratings, showratings, input, arrayResult, fileWriter);
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        JSONObject result = new JSONObject();
                        try {
                            result = fileWriter.writeFile(id, "message",
                                    "BestRatedUnseenRecommendation cannot be applied!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        arrayResult.add(result);
                    }
                }
                if (input.getCommands().get(i).getType().equals("search")) {
                    boolean ok = false;
                    for (UserInputData user : input.getUsers()) {
                        if (user.getUsername().equals(input.getCommands().get(i).getUsername())) {
                            if (user.getSubscriptionType().equals("PREMIUM")) {
                                PremiumUser.search(id, user, input.getCommands().get(i).getGenre(),
                                        movieratings, showratings, input, arrayResult, fileWriter);
                                ok = true;
                                break;
                            }
                        }
                    }
                    if (!ok) {
                        JSONObject result = new JSONObject();
                        try {
                            result = fileWriter.writeFile(id, "message",
                                    "SearchRecommendation cannot be applied!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        arrayResult.add(result);
                    }
                }
                if (input.getCommands().get(i).getType().equals("favorite")) {
                    boolean ok = false;
                    for (UserInputData user : input.getUsers()) {
                        if (user.getUsername().equals(input.getCommands().get(i).getUsername())) {
                            if (user.getSubscriptionType().equals("PREMIUM")) {
                                PremiumUser.favorite(id, user, input, arrayResult, fileWriter);
                                ok = true;
                                break;
                            }
                        }
                    }
                    if (!ok) {
                        JSONObject result = new JSONObject();
                        try {
                            result = fileWriter.writeFile(id, "message",
                                    "FavoriteRecommendation cannot be applied!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        arrayResult.add(result);
                    }
                }
                if (input.getCommands().get(i).getType().equals("popular")) {
                    boolean ok = false;
                    for (UserInputData user : input.getUsers()) {
                        if (user.getUsername().equals(input.getCommands().get(i).getUsername())) {
                            if (user.getSubscriptionType().equals("PREMIUM")) {
                                PremiumUser.popular(id, user, input, arrayResult, fileWriter);
                                ok = true;
                                break;
                            }
                        }
                    }
                    if (!ok) {
                        JSONObject result = new JSONObject();
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

        }

        fileWriter.closeJSON(arrayResult);
    }
}
