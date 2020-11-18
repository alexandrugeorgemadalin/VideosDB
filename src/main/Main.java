package main;

import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import fileio.Input;
import fileio.InputLoader;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

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
                    ArrayList<Rating.Movie> movieratings = new ArrayList<Rating.Movie>();
                    ArrayList<Rating.Show> showratings = new ArrayList<Rating.Show>();
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
                        commands.ratingshow(id, title, season, user, rating,
                                showratings, input.getUsers(), arrayResult, fileWriter);
                    }
                }
            }

        }

        fileWriter.closeJSON(arrayResult);
    }
}
