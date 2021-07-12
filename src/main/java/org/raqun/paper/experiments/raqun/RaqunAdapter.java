package org.raqun.paper.experiments.raqun;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.raqun.paper.experiments.common.MatchStatistic;
import org.raqun.paper.experiments.common.ExperimentHelper;
import org.raqun.paper.experiments.common.MethodAdapter;
import org.raqun.paper.raqun.data.*;
import org.raqun.paper.raqun.RaQuNMatcher;
import org.raqun.paper.raqun.tree.RKDTree;

public class RaqunAdapter implements MethodAdapter {
    private final RaqunSetup setup;
    private final List<Integer> Ks;
    private List<Measurement> experimentSpecifics;

    public RaqunAdapter(RaqunSetup setup) {
        this.setup = setup;
        Ks = IntStream.rangeClosed(setup.startK, setup.maxK).boxed().collect(Collectors.toList());
    }

    public void run() {
        // Load the dataset
        RDataset dataset = new RDataset(setup.datasetName);
        dataset.loadFileContent(setup.datasetFile);
        ArrayList<RModel> models = dataset.getModels();
        for (int i = 0; i < setup.numberOfRepeats; i++) {
            // Get random chunks from the dataset
            List<ArrayList<RModel>> datasetChunks = ExperimentHelper.getDatasetChunks(models, setup.chunkSize);
            setupTestCases(datasetChunks.size());
            int startID = 0;
            int endID = experimentSpecifics.size();

            for (int runID = startID; runID < endID; runID++) {
                // Load the data for the next chunk execution
                Measurement measurement = experimentSpecifics.get(runID);
                // Get the next subset of models that are to be matched
                ArrayList<RModel> modelSubset = datasetChunks.get(measurement.chunkNumber - 1);

                // Build the K-D-Tree
                Instant startedAt = Instant.now();
                RKDTree RKDTree = new RKDTree(modelSubset, setup.vectorization);
                Instant indexBuiltAt = Instant.now();

                // Calculate the match candidates in the K-D-Tree, based on radius or nearest neighbor search
                Set<CandidatePair> allCandidatePairs = RKDTree.findKCandidates(measurement.k);
                Instant foundSimilaritiesAt = Instant.now();

                // Get all elements
                Set<RElement> allElements = new HashSet<>(RKDTree.getElementsInTree());

                // Set the number of models in the evaluator
                setup.similarityFunction.setNumberOfModels(RKDTree.getNumberOfInputModels());

                // Merge the models
                Set<RMatch> mergedModel = RaQuNMatcher.match(allCandidatePairs, allElements, setup.similarityFunction, setup.validityConstraint);
                Instant finishedAt = Instant.now();

                // Convert the runtime of this run
                measurement.resultIndexTimeElapsedSec = toSeconds(Duration.between(startedAt, indexBuiltAt));
                measurement.resultSearchTimeElapsedSec = toSeconds(Duration.between(indexBuiltAt, foundSimilaritiesAt));
                measurement.resultMatchTimeElapsedSec = toSeconds(Duration.between(foundSimilaritiesAt, finishedAt));

                // Initialize the statistic for saving the results
                int tempId = (setup.chunkSize == Integer.MAX_VALUE) ? runID : (runID/setup.chunkSize);
                tempId++;
                int sizeOfLargestModel = getSizeOfLargestModel(modelSubset);

                MatchStatistic matchStatistic = new MatchStatistic(tempId, measurement.testCase, setup.name,
                        RKDTree.getNumberOfInputModels(), sizeOfLargestModel, measurement.k);
                matchStatistic.calculateStatistics(mergedModel,
                        measurement.resultIndexTimeElapsedSec
                                + measurement.resultSearchTimeElapsedSec
                                + measurement.resultMatchTimeElapsedSec);
                matchStatistic.setNumberOfComparisonsActuallyDone(allCandidatePairs.size());
                matchStatistic.calculateNumberOfNWayComparisonsTheoreticallyNeeded(
                        calculateNumberOfComparisonsTheoreticallyNeeded(modelSubset, allElements)
                );

                System.out.println(matchStatistic);
                System.out.println();

                matchStatistic.writeAsJSON(setup.resultFile, true);
                //executionStatistic.writeModel(setup.mergeResultFile);
            }
        }
    }

    private int getSizeOfLargestModel(List<RModel> models) {
        int size = 0;
        for(RModel model : models) {
            if (model.getElements().size() > size) {
                size = model.getElements().size();
            }
        }
        return size;
    }

    private void setupTestCases(int numberOfChunks) {
        experimentSpecifics = new ArrayList<>();
        int runId = 0;
        for (int k : Ks) {
            runId = addMeasurement(runId, k, numberOfChunks);
        }
    }

    private int addMeasurement(int runId, int k, int numberOfChunks) {
        for (int chunkNumber = 1; chunkNumber <= numberOfChunks; chunkNumber++) {
            experimentSpecifics.add(new Measurement(runId++, setup.datasetName, k, numberOfChunks, chunkNumber));
        }
        return runId;
    }

    private double toSeconds(Duration duration) {
        return duration.toMillis() / 1000d;
    }

    private int calculateNumberOfComparisonsTheoreticallyNeeded(ArrayList<RModel> models, Set<RElement> allElements) {
        // Initialize the number of unique comparisons with the number of elements in total
        int numberOfUniqueComparisons = allElements.size();
        int numberOfComparisonsTheoreticallyNeeded = 0;
        for (RModel model : models) {
            int numberOfElements = model.getElements().size();
            // Retract the number of elements in this model from the number of unique comparisons, because we only
            // compare elements of different models. This number now represents how many comparisons have to be done
            // for each element in this model
            numberOfUniqueComparisons -= numberOfElements;

            // Now calculate how many comparisons are required for the elements in this model and add the number
            numberOfComparisonsTheoreticallyNeeded += (numberOfElements * numberOfUniqueComparisons);
        }
        return numberOfComparisonsTheoreticallyNeeded;
    }
}