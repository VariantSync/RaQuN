package org.raqun.paper.experiments.common;

import com.google.gson.Gson;
import org.raqun.paper.raqun.data.RMatch;
import org.raqun.paper.raqun.data.RElement;
import org.raqun.paper.raqun.similarity.WeightMetric;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.raqun.paper.ExperimentRunner.*;

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

    private double mergeSizeFactor = -1;
    private double averageMatchedElementFit = -1;
    private double[] matchedElementFitPerTuple;
    // The merged model itself

    private transient Set<RMatch> resultModel = null;
    public MatchStatistic(int runID, String dataset, String method, int numberOfModels, int sizeOfLargestModel) {
        this.runID = runID;
        this.dataset = dataset;
        this.method = method;
        this.numberOfModels = numberOfModels;
        this.sizeOfLargestModel = sizeOfLargestModel;
    }

    public MatchStatistic(int runID, String dataset, String method, int numberOfModels, int sizeOfLargestModel,
                          int k) {
        this.runID = runID;
        this.dataset = dataset;
        this.method = method;
        this.numberOfModels = numberOfModels;
        this.sizeOfLargestModel = sizeOfLargestModel;
        this.k = k;
    }

    public void calculateStatistics(Set<RMatch> tuples, double runtime) {
        this.resultModel = tuples;
        this.numberOfElements = countElements(tuples);
        this.numberOfTuples = tuples.size();

        this.mergeSizeFactor = ((double) tuples.size() / (double) sizeOfLargestModel);
        this.matchedElementFitPerTuple = calculateElementFitPerTuple(tuples);

        // Calculate the average matched element fit
        double fitSum = 0.0d;
        for (double v : this.matchedElementFitPerTuple) {
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

        if (PRINT_MATCH) {
            for (RMatch tuple : tuples) {
                System.out.println(tuple.getLongString());
            }
        }

        this.runtime = runtime;
        WeightMetric weightMetric = new WeightMetric(this.numberOfModels);
        this.weight = weightMetric.getQualityOfMatching(tuples);

        // Calculate the oracle results
        ExperimentOracle oracle = new ExperimentOracle(tuples);
        this.tp = (int) oracle.getTp();
        this.fp = (int) oracle.getFp();
        this.fn = (int) oracle.getFn();
        this.precision = oracle.getPrecision();
        this.recall = oracle.getRecall();
        this.fMeasure = oracle.getFMeasure();
    }

    private double[] calculateElementFitPerTuple(Set<RMatch> tuples) {
        // First, get only the tuple with at least two elements that have been matched to each other
        Set<RMatch> matchTuple = tuples.stream().filter(t -> t.getElements().size() > 1).collect(Collectors.toSet());
        double[] elementFitPerTuple = new double[matchTuple.size()];

        // Iterate over each tuple, calculate its fit and add it to the sum
        int tupleId = 0;
        for (RMatch tuple : matchTuple) {
            Set<String> uniquePropertiesInTuple = new HashSet<>();
            int numberOfPropertiesInTuple = 0;
            // Get the unique properties in the tuple and the total number of properties across elements
            for (RElement element : tuple.getElements()) {
                uniquePropertiesInTuple.addAll(element.getProperties());
                numberOfPropertiesInTuple += element.getProperties().size();
            }
            // The numerator counts how many properties overlap across elements, properties that only occur in one
            // element should not be counted, therefore we deduct the number of unique properties
            double numerator = numberOfPropertiesInTuple - uniquePropertiesInTuple.size();
            // The denominator represents the total number of possible overlaps, we deduct the number of unique
            // properties to achieve a 0-1 normalization
            double denominator = ((double) uniquePropertiesInTuple.size() * (double) tuple.getElements().size())
                    - uniquePropertiesInTuple.size();

            double tupleFit = numerator / denominator;
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

    public void writeModel(String pathToFile) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(pathToFile), true))) {
            for (RMatch tuple : resultModel) {
                writer.print(tuple.toString());
                writer.print(";");
            }
            writer.println("\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void calculateNumberOfNWayComparisonsTheoreticallyNeeded(int numberOfComparisons) {
        this.numberOfNWayComparisonsTheoreticallyNeeded = numberOfComparisons;
    }

    public void setNumberOfComparisonsActuallyDone(int numberOfComparisonsActuallyDone) {
        this.numberOfComparisonsActuallyDone = numberOfComparisonsActuallyDone;
    }

    private int countElements(Set<RMatch> set) {
        ArrayList<RElement> elements = new ArrayList<>();
        for (RMatch tuple : set) {
            elements.addAll(tuple.getElements());
        }
        return elements.size();
    }

    public void writeAsJSON(String pathToFile, boolean append) {
        Path path = Paths.get(pathToFile);
        for (int i = 1; i < path.getNameCount(); i++) {
            File f;
            if (path.getRoot() != null) {
                f = Paths.get(path.getRoot().toString(), path.subpath(0, i).toString()).toFile();
            } else {
                f = Paths.get(path.subpath(0, i).toString()).toFile();
            }
            if (!f.exists()) {
                f.mkdir();
            }
        }

        Gson gson = new Gson();
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(pathToFile), append))) {
            String json = gson.toJson(this);
            writer.println(json);
        } catch (Exception e) {
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
