package de.variantsync.matching.experiments.raqun;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.variantsync.matching.experiments.common.ExperimentSetup;
import de.variantsync.matching.raqun.data.*;
import de.variantsync.matching.experiments.common.MatchStatistic;
import de.variantsync.matching.experiments.common.ExperimentHelper;
import de.variantsync.matching.experiments.common.MatcherAdapter;
import de.variantsync.matching.raqun.RaQuN;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.vectorization.IVectorization;

import static de.variantsync.matching.experiments.common.ExperimentHelper.executeWithTimeout;

/**
 * Experiment Adapter that calls RaQuN during an experiment.
 */
public class RaQuNAdapter implements MatcherAdapter {
    private List<Measurement> experimentSpecifics;
    private final ISimilarityFunction similarityFunction;
    private final IVectorization vectorization;
    private final IValidityConstraint validityConstraint;

    /**
     * Initialize a new Adapter with the given setup
     */
    public RaQuNAdapter(final ISimilarityFunction similarityFunction, final IVectorization vectorization, final IValidityConstraint validityConstraint) {
        this.similarityFunction = similarityFunction;
        this.vectorization = vectorization;
        this.validityConstraint = validityConstraint;
    }

    @Override
    public boolean run(final ExperimentSetup setup) {
        final List<Integer> Ks = IntStream.rangeClosed(setup.startK, setup.maxK).boxed().collect(Collectors.toList());
        // Load the dataset
        final RDataset dataset = new RDataset(setup.datasetName);
        dataset.loadFileContent(Paths.get(setup.datasetFile));
        final ArrayList<RModel> models = dataset.getModels();
        for (int i = 0; i < setup.numberOfRepeats; i++) {
            // Get random chunks from the dataset
            final List<ArrayList<RModel>> datasetChunks = ExperimentHelper.getDatasetChunks(models, setup.chunkSize);
            setupTestCases(datasetChunks.size(), Ks, setup.datasetName);
            final int startID = 0;
            final int endID = experimentSpecifics.size();

            for (int runID = startID; runID < endID; runID++) {
                // Load the data for the next chunk execution
                final Measurement measurement = experimentSpecifics.get(runID);
                // Get the next subset of models that are to be matched
                final ArrayList<RModel> modelSubset = datasetChunks.get(measurement.chunkNumber - 1);
                // Set the number of models in the evaluator
                this.similarityFunction.setNumberOfModels(modelSubset.size());

                final Instant startedAt = Instant.now();
                final RaQuN raqun = new RaQuN(this.vectorization, this.validityConstraint, this.similarityFunction, measurement.k);

                // Match the models
                final Set<RMatch> matchingResult = executeWithTimeout(() -> raqun.match(modelSubset), setup, raqun);

                if (matchingResult == null) {
                    if (raqun.stopped()) {
                        // The result is null, if a timeout occurred. In this case we abort the experiment without starting additional runs.
                        return false;
                    } else {
                        throw new NullPointerException("Matching result must not be null if RaQuN was stopped.");
                    }
                }

                final Instant finishedAt = Instant.now();
                // Convert the runtime of this run
                measurement.resultMatchTimeElapsedSec = toSeconds(Duration.between(startedAt, finishedAt));

                // Initialize the statistic for saving the results
                int tempId = (setup.chunkSize == Integer.MAX_VALUE) ? runID : (runID / setup.chunkSize);
                tempId++;
                final int sizeOfLargestModel = getSizeOfLargestModel(modelSubset);

                final MatchStatistic matchStatistic = new MatchStatistic(tempId, measurement.testCase, setup.name,
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
                    for (final RMatch match : matchingResult) {
                        System.out.println(match.getLongString());
                    }
                }

                matchStatistic.writeAsJSON(setup.resultFile, true);
            }
        }
        return true;
    }

    private int getSizeOfLargestModel(final List<RModel> models) {
        int size = 0;
        for(final RModel model : models) {
            if (model.getElements().size() > size) {
                size = model.getElements().size();
            }
        }
        return size;
    }

    private void setupTestCases(final int numberOfChunks, final List<Integer> Ks, final String datasetName) {
        experimentSpecifics = new ArrayList<>();
        int runId = 0;
        for (final int k : Ks) {
            runId = addMeasurement(runId, k, numberOfChunks, datasetName);
        }
    }

    private int addMeasurement(int runId, final int k, final int numberOfChunks, final String datasetName) {
        for (int chunkNumber = 1; chunkNumber <= numberOfChunks; chunkNumber++) {
            experimentSpecifics.add(new Measurement(runId++, datasetName, k, numberOfChunks, chunkNumber));
        }
        return runId;
    }

    private double toSeconds(final Duration duration) {
        return duration.toMillis() / 1000d;
    }

    private int calculateNumberOfComparisonsTheoreticallyNeeded(final ArrayList<RModel> models, final int numberOfAllElements) {
        // Initialize the number of unique comparisons with the number of elements in total
        int numberOfUniqueComparisons = numberOfAllElements;
        int numberOfComparisonsTheoreticallyNeeded = 0;
        for (final RModel model : models) {
            final int numberOfElements = model.getElements().size();
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