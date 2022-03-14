package de.variantsync.matching.experiments.common;

import com.google.gson.Gson;
import de.variantsync.matching.raqun.similarity.WeightMetric;
import de.variantsync.matching.raqun.data.RMatch;
import de.variantsync.matching.raqun.data.RElement;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MatchStatistic stores the statistics of a specific experiment run. All fields that are not transient are saved to disk
 * in JSON format.
 */
public class MatchStatistic {
    // Basic information
    private final int runID;
    private final String method;
    private final String dataset;

    // Result information
    private double runtime = -1;
    private int numberOfModels = -1;
    private int numberOfElements = -1;
    private int numberOfTuples = -1;
    private int sizeOfLargestModel = -1;

    // Raqun specific information
    private int k = -1;

    // Oracle specific information
    private int tp;
    private int fp;
    private int fn;
    private double precision;
    private double recall;
    private double fMeasure;
    private double weight = -1;

    // Information about the number of comparisons that were required

    private int numberOfNWayComparisonsTheoreticallyNeeded = -1;
    private int numberOfComparisonsActuallyDone = -1;
    // Additional information about the tuples

    // These values are stored in the JSON result as well
    private double mergeSizeFactor = -1;
    private double averageMatchedElementFit = -1;
    private double[] matchedElementFitPerTuple;

    // The matching
    private transient Set<RMatch> matching = null;

    public MatchStatistic(final int runID, final String dataset, final String method, final int numberOfModels, final int sizeOfLargestModel) {
        this.runID = runID;
        this.dataset = dataset;
        this.method = method;
        this.numberOfModels = numberOfModels;
        this.sizeOfLargestModel = sizeOfLargestModel;
    }

    public MatchStatistic(final int runID, final String dataset, final String method, final int numberOfModels, final int sizeOfLargestModel,
                          final int k) {
        this.runID = runID;
        this.dataset = dataset;
        this.method = method;
        this.numberOfModels = numberOfModels;
        this.sizeOfLargestModel = sizeOfLargestModel;
        this.k = k;
    }

    public void calculateStatistics(final Set<RMatch> matching, final double runtime) {
        this.matching = matching;
        this.numberOfElements = countElements(matching);
        this.numberOfTuples = matching.size();

        this.mergeSizeFactor = ((double) matching.size() / (double) sizeOfLargestModel);
        this.matchedElementFitPerTuple = calculateElementFitPerTuple(matching);

        // Calculate the average matched element fit
        double fitSum = 0.0d;
        for (final double v : this.matchedElementFitPerTuple) {
            fitSum += v;
        }
        if (fitSum > 0) {
            this.averageMatchedElementFit = fitSum / (double) this.matchedElementFitPerTuple.length;
        } else {
            this.averageMatchedElementFit = 0;
        }
        if (this.averageMatchedElementFit > 1.0) {
            System.err.println("It looks like there are duplicate properties in an element of the dataset!");
        }

        this.runtime = runtime;
        final WeightMetric weightMetric = new WeightMetric(this.numberOfModels);
        this.weight = weightMetric.getQualityOfMatching(matching);

        // Calculate the oracle results
        final ExperimentOracle oracle = new ExperimentOracle(matching);
        this.tp = (int) oracle.getTp();
        this.fp = (int) oracle.getFp();
        this.fn = (int) oracle.getFn();
        this.precision = oracle.getPrecision();
        this.recall = oracle.getRecall();
        this.fMeasure = oracle.getFMeasure();
    }

    private double[] calculateElementFitPerTuple(final Set<RMatch> tuples) {
        // First, get only the tuple with at least two elements that have been matched to each other
        final Set<RMatch> matchTuple = tuples.stream().filter(t -> t.getElements().size() > 1).collect(Collectors.toSet());
        final double[] elementFitPerTuple = new double[matchTuple.size()];

        // Iterate over each tuple, calculate its fit and add it to the sum
        int tupleId = 0;
        for (final RMatch tuple : matchTuple) {
            final Set<String> uniquePropertiesInTuple = new HashSet<>();
            int numberOfPropertiesInTuple = 0;
            // Get the unique properties in the tuple and the total number of properties across elements
            for (final RElement element : tuple.getElements()) {
                uniquePropertiesInTuple.addAll(element.getProperties());
                numberOfPropertiesInTuple += element.getProperties().size();
            }
            // The numerator counts how many properties overlap across elements, properties that only occur in one
            // element should not be counted, therefore we deduct the number of unique properties
            final double numerator = numberOfPropertiesInTuple - uniquePropertiesInTuple.size();
            // The denominator represents the total number of possible overlaps, we deduct the number of unique
            // properties to achieve a 0-1 normalization
            final double denominator = ((double) uniquePropertiesInTuple.size() * (double) tuple.getElements().size())
                    - uniquePropertiesInTuple.size();

            final double tupleFit = numerator / denominator;
            if (Double.isNaN(tupleFit)) {
                // There is something wrong, probably with the dataset
                System.err.println("The tuple fit is NaN for some reason!");
            }
            if (tupleFit > 1.0) {
                System.err.println("It looks like there are duplicate properties in an element of the dataset!");
            }
            elementFitPerTuple[tupleId] = tupleFit;
            tupleId++;
        }

        return elementFitPerTuple;
    }

    public void writeModel(final String pathToFile) {
        try (final PrintWriter writer = new PrintWriter(new FileOutputStream(new File(pathToFile), true))) {
            for (final RMatch tuple : matching) {
                writer.print(tuple.toString());
                writer.print(";");
            }
            writer.println("\n");
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void calculateNumberOfNWayComparisonsTheoreticallyNeeded(final int numberOfComparisons) {
        this.numberOfNWayComparisonsTheoreticallyNeeded = numberOfComparisons;
    }

    public void setNumberOfComparisonsActuallyDone(final int numberOfComparisonsActuallyDone) {
        this.numberOfComparisonsActuallyDone = numberOfComparisonsActuallyDone;
    }

    private int countElements(final Set<RMatch> set) {
        final ArrayList<RElement> elements = new ArrayList<>();
        for (final RMatch tuple : set) {
            elements.addAll(tuple.getElements());
        }
        return elements.size();
    }

    public void writeAsJSON(final String pathToFile, final boolean append) {
        final Path path = Paths.get(pathToFile);
        for (int i = 1; i < path.getNameCount(); i++) {
            final File f;
            if (path.getRoot() != null) {
                f = Paths.get(path.getRoot().toString(), path.subpath(0, i).toString()).toFile();
            } else {
                f = Paths.get(path.subpath(0, i).toString()).toFile();
            }
            if (!f.exists()) {
                f.mkdir();
            }
        }

        final Gson gson = new Gson();
        try (final PrintWriter writer = new PrintWriter(new FileOutputStream(new File(pathToFile), append))) {
            final String json = gson.toJson(this);
            writer.println(json);
        } catch (final Exception e) {
            System.err.println(this.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Dataset: " + this.dataset + "\n" +
                String.format(Locale.ENGLISH,
                "#%d\t" +
                        "%s  \t--  \t" +
                        "weight: %.4f\t" +
                        "runtimeTotal: %.5f\t" +
                        "k: %d\n" +
                        "tp: %d\t" +
                        "fp: %d\t" +
                        "fn: %d\t" +
                        "precision: %f\t" +
                        "recall: %f\t" +
                        "f-measure: %f\n",
                runID,
                method,
                weight,
                runtime,
                k,
                tp,
                fp,
                fn,
                precision,
                recall,
                fMeasure)
                + "Number of Models " +
                numberOfModels +
                "  \t--  \tNumber of Elements: " +
                numberOfElements +
                "\n" +
                "Number of Tuples: " +
                numberOfTuples +
                "  \t--  \tNumber of Elements in Largest Model: " +
                sizeOfLargestModel +
                "\n" +
                "Number of Comparisons Theoretically Needed for a Complete N-Way Comparison: " +
                numberOfNWayComparisonsTheoreticallyNeeded +
                "\n" +
                "Number of Comparisons Actually Performed by the Algorithm: " +
                numberOfComparisonsActuallyDone
                + "\n";
    }

}