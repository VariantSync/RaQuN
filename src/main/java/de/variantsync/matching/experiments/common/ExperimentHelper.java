package de.variantsync.matching.experiments.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A utility class that proved methods for setting up and running the experiments.
 */
public class ExperimentHelper {

    /**
     * Split the set of input models of a dataset into multiple subsets. This method is used for the three randomly
     * generated datasets.
     * @param inputModels The input models of the dataset
     * @param chunkSize The size of each subset
     * @param <T> Type of the input models
     * @return A list of model subsets
     */
    public static <T> List<ArrayList<T>> getDatasetChunks(ArrayList<T> inputModels, int chunkSize) {
        // Shuffle the models for randomness
        Collections.shuffle(inputModels);
        List<ArrayList<T>> chunks = new ArrayList<>();
        ArrayList<T> subList = null;
        for (int i = 0; i < inputModels.size(); i++) {
            if (i % chunkSize == 0) {
                subList = new ArrayList<>();
                chunks.add(subList);
            }
            subList.add(inputModels.get(i));
        }
        return chunks;
    }

    /**
     * Runs a specific experiment for the given matcher.
     * @param adapter of the matcher
     * @param baseResultsDir where the results are saved to
     * @param name the name of the matcher
     * @param dataset the name of the dataset
     */
    public static void runExperiment(MatcherAdapter adapter, String baseResultsDir, String name, String dataset) {
        try {
            System.out.println("Running " + name + " on " + dataset + "...");
            adapter.run();
        } catch (Error | Exception error) {
            LocalDateTime localDateTime = LocalDateTime.now();
            String errorText = "+++++++++++++++++++++++\n"
                    + localDateTime
                    + ": ERROR for " + name + " on " + dataset + "\n"
                    + error
                    + "\n+++++++++++++++++++++++\n";

            File errorLogFile = Paths.get(baseResultsDir, "ERRORLOG.txt").toFile();
            try (FileWriter fw = new FileWriter(errorLogFile, true)) {
                fw.write(errorText);
                fw.write("\n");
            } catch (IOException e) {
                System.err.println("WARNING: Not possible to write to ERRORLOG!\n" + e);
            }

            System.err.println("ERROR for " + name + " on " + dataset + "\n" + error);
            error.printStackTrace();
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }
}