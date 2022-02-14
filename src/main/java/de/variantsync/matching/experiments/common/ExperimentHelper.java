package de.variantsync.matching.experiments.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Utility class that provides methods for setting up and running the experiments.
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
    public static <T> List<ArrayList<T>> getDatasetChunks(final ArrayList<T> inputModels, final int chunkSize) {
        // Shuffle the models for randomness
        Collections.shuffle(inputModels);
        final List<ArrayList<T>> chunks = new ArrayList<>();
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
     * @param setup the setup of the experiment
     * @param dataset the name of the dataset
     */
    public static boolean runExperiment(final MatcherAdapter adapter, final ExperimentSetup setup, final String baseResultsDir, final String dataset) {
        final String name = setup.name;
        boolean success;
        try {
            System.out.println("Running " + name + " on " + dataset + "...");
            success = adapter.run(setup);
        } catch (final Error | Exception error) {
            success = false;
            final LocalDateTime localDateTime = LocalDateTime.now();
            final String errorText = "+++++++++++++++++++++++\n"
                    + localDateTime
                    + ": ERROR for " + name + " on " + dataset + "\n"
                    + error
                    + "\n+++++++++++++++++++++++\n";

            final File errorLogFile = Paths.get(baseResultsDir, "ERRORLOG.txt").toFile();
            try (final FileWriter fw = new FileWriter(errorLogFile, true)) {
                fw.write(errorText);
                fw.write("\n");
            } catch (final IOException e) {
                System.err.println("WARNING: Not possible to write to ERRORLOG!\n" + e);
            }

            System.err.println("ERROR for " + name + " on " + dataset + "\n" + error);
            error.printStackTrace();
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        return success;
    }


     public static <V> V executeWithTimeout(final Callable<V> callable, final ExperimentSetup setup, IKillableLongTask task) {
         final ExecutorService executor = Executors.newSingleThreadExecutor();
         final Future<V> future = executor.submit(callable);
         try {
             return future.get(setup.timeout, setup.timeoutUnit);
         } catch (final TimeoutException e) {
             handleTimeout(setup, task);
             return null;
         } catch (final ExecutionException | InterruptedException e) {
             throw new RuntimeException(e);
         } finally {
             executor.shutdownNow();
         }
    }

    private static void handleTimeout(final ExperimentSetup setup, final IKillableLongTask task) {
        System.err.println("Timeout after " + setup.timeout + " " + setup.timeoutUnit + "...Attempting to stop " + task);
        task.kill();
        final String name = setup.name;
        final LocalDateTime localDateTime = LocalDateTime.now();
        final String errorText = "+++++++++++++++++++++++\n"
                + localDateTime
                + ": TIMEOUT for " + name + " on " + setup.datasetName + "\n"
                + "\n+++++++++++++++++++++++\n";

        final File errorLogFile = Paths.get(setup.baseResultsDir, "TIMEOUT_LOG.txt").toFile();
        try (final FileWriter fw = new FileWriter(errorLogFile, true)) {
            fw.write(errorText);
            fw.write("\n");
        } catch (final IOException ex) {
            System.err.println("WARNING: Not possible to write to TIMEOUT_LOG!\n" + ex);
        }
    }
}