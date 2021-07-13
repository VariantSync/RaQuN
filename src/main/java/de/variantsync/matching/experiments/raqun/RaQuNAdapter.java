package de.variantsync.matching.experiments.raqun;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.variantsync.matching.raqun.data.*;
import de.variantsync.matching.experiments.common.MatchStatistic;
import de.variantsync.matching.experiments.common.ExperimentHelper;
import de.variantsync.matching.experiments.common.MethodAdapter;
import de.variantsync.matching.raqun.RaQuN;

public class RaQuNAdapter implements MethodAdapter {
    private final RaqunSetup setup;
    private final List<Integer> Ks;
    private List<Measurement> experimentSpecifics;

    public RaQuNAdapter(RaqunSetup setup) {
        this.setup = setup;
        Ks = IntStream.rangeClosed(setup.startK, setup.maxK).boxed().collect(Collectors.toList());
    }

    public void run() {
        // Load the dataset
        RDataset dataset = new RDataset(setup.datasetName);
        dataset.loadFileContent(Paths.get(setup.datasetFile));
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
                // Set the number of models in the evaluator
                setup.similarityFunction.setNumberOfModels(modelSubset.size());

                Instant startedAt = Instant.now();
                RaQuN raqun = new RaQuN(setup.vectorization, setup.validityConstraint, setup.similarityFunction, measurement.k);

                // Match the models
                Set<RMatch> matchingResult = raqun.match(modelSubset);
                Instant finishedAt = Instant.now();

                // Convert the runtime of this run
                measurement.resultMatchTimeElapsedSec = toSeconds(Duration.between(startedAt, finishedAt));

                // Initialize the statistic for saving the results
                int tempId = (setup.chunkSize == Integer.MAX_VALUE) ? runID : (runID/setup.chunkSize);
                tempId++;
                int sizeOfLargestModel = getSizeOfLargestModel(modelSubset);

                MatchStatistic matchStatistic = new MatchStatistic(tempId, measurement.testCase, setup.name,
                        modelSubset.size(), sizeOfLargestModel, measurement.k);
                matchStatistic.calculateStatistics(matchingResult,
                        measurement.resultIndexTimeElapsedSec
                                + measurement.resultSearchTimeElapsedSec
                                + measurement.resultMatchTimeElapsedSec);
                matchStatistic.setNumberOfComparisonsActuallyDone(raqun.getCandidatePairs().size());
                matchStatistic.calculateNumberOfNWayComparisonsTheoreticallyNeeded(
                        calculateNumberOfComparisonsTheoreticallyNeeded(modelSubset, raqun.getNumProcessedElements())
                );

                System.out.println(matchStatistic);
                System.out.println();

                if (setup.printVerbose) {
                    for (RMatch match : matchingResult) {
                        System.out.println(match.getLongString());
                    }
                }

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

    private int calculateNumberOfComparisonsTheoreticallyNeeded(ArrayList<RModel> models, int numberOfAllElements) {
        // Initialize the number of unique comparisons with the number of elements in total
        int numberOfUniqueComparisons = numberOfAllElements;
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