package de.variantsync.matching.experiments.baseline;

import de.variantsync.matching.experiments.EAlgorithm;
import de.variantsync.matching.experiments.common.*;
import de.variantsync.matching.nwm.alg.merge.ChainingOptimizingMerger;
import de.variantsync.matching.nwm.common.AlgoUtil;
import de.variantsync.matching.nwm.domain.Element;
import de.variantsync.matching.nwm.domain.Model;
import de.variantsync.matching.nwm.domain.Tuple;
import de.variantsync.matching.nwm.execution.RunResult;
import de.variantsync.matching.pairwise.HungarianPairwiseMatcher;
import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RMatch;

import java.util.*;

import static de.variantsync.matching.experiments.common.ExperimentHelper.executeWithTimeout;

/**
 * Adapter for running NwM and Pairwise matchers in our experiments.
 */
public class BaselineAlgoAdapter implements MatcherAdapter {
    private final EAlgorithm algorithmToApply;

    public BaselineAlgoAdapter(final EAlgorithm algorithm) {
        this.algorithmToApply = algorithm;
    }

    @Override
    public boolean run(final ExperimentSetup setup) {
        for (int runID = 0; runID < setup.numberOfRepeats; runID++) {
            // Here, we use the Model class of Rubin and Chechik
            final ArrayList<Model> models = Model.readModelsFile(setup.datasetFile);
            final List<ArrayList<Model>> chunks = ExperimentHelper.getDatasetChunks(models, setup.chunkSize);

            for (final ArrayList<Model> chunk : chunks) {
                final int sizeOfLargestModel = getSizeOfLargestModel(chunk);
                final int numberOfModels = chunk.size();
                final MatchStatistic matchStatistic = new MatchStatistic(0, setup.datasetName, setup.name,
                        numberOfModels, sizeOfLargestModel);

                AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = false;

                final List<Tuple> solution;
                final RunResult runResult;
                final ArrayList<Model> modelSubList = new ArrayList<>(chunk);

                if(algorithmToApply == EAlgorithm.NwM) {
                    // To the best of our knowledge, this is the prototype implementation of nwm used in the work of
                    // Rubin and Chechik
                    // it achieves the matching weights that were presented in their publication
                    final ChainingOptimizingMerger mmm = new ChainingOptimizingMerger(modelSubList, new Stopped());
                    final Object result = executeWithTimeout(mmm::run, setup, mmm);
                    if (result==null) {
                        if (mmm.stopped()) {
                            // The result is null, if a timeout occurred. In this case we abort the experiment without starting additional runs.
                            return false;
                        } else {
                            throw new NullPointerException("Matching result must not be null if NwM was stopped.");
                        }
                    }
                    solution = mmm.getTuplesInMatch();
                    runResult = mmm.getRunResult(numberOfModels);
                } else {
                    final HungarianPairwiseMatcher matcher = new HungarianPairwiseMatcher(modelSubList, algorithmToApply);
                    final Object result = executeWithTimeout(matcher::run, setup, matcher);
                    if (result == null) {
                        if (matcher.stopped()) {
                            // The result is null, if a timeout occurred. In this case we abort the experiment without starting additional runs.
                            return false;
                        } else {
                            throw new NullPointerException("Matching result must not be null if Pairwise was stopped.");
                        }
                    }
                    solution = matcher.getResult();
                    runResult = matcher.getRunResult();
                    matchStatistic.setNumberOfComparisonsActuallyDone(matcher.getNumberOfComparisons());
                }

                if (solution == null || runResult == null) {
                    return false;
                }

                // We parse the matching returned by NwM to our own data format, in order to do the evaluation
                final Set<RMatch> mergedModel = parseSolution(solution, chunk);

                if (setup.printVerbose) {
                    final int numberOfClasses = countClasses(mergedModel);
                    System.out.println("Number of Classes: " + numberOfClasses);
                    for (final RMatch tuple : mergedModel) {
                        System.out.println(tuple.getLongString());
                    }
                }
                // Save the results
                matchStatistic.calculateStatistics(mergedModel, getTimeInSeconds(runResult.execTime));
                System.out.println(matchStatistic);
                System.out.println();

                matchStatistic.writeAsJSON(setup.resultFile, true);
            }
        }
        return true;
    }

    private int getSizeOfLargestModel(final List<Model> models) {
        int size = 0;
        for(final Model model : models) {
            if (model.getElements().size() > size) {
                size = model.getElements().size();
            }
        }
        return size;
    }

    private double getTimeInSeconds(final double time) {
        return time / 1000;
    }

    private Set<RMatch> parseSolution(final List<Tuple> solution, final ArrayList<Model> chunk) {
        // Create a set of all elements to later find isolated ones
        final Set<Element> allElements = new HashSet<>();
        for (final Model model : chunk) {
            allElements.addAll(model.getElements());
        }

        final Set<RMatch> parsedSet = new HashSet<>();

        // Create RMatch for all tuple in the solution
        for (final Tuple tuple : solution) {
            final List<RElement> nodes = new ArrayList<>();
            for (final Element e : tuple.getRealElements()) {
                // Remove the current element from the set of all elements
                if(!allElements.remove(e)) {
                    throw new RuntimeException("ERROR: Element not in set of all elements...");
                }
                nodes.add(parseElement(e));
            }
            parsedSet.add(new RMatch(nodes.toArray(new RElement[0])));
        }

        // Create RMatch for all elements that were not part of the solution, they have to be counted as FN and FP
        for (final Element element : allElements) {
            parsedSet.add(new RMatch(parseElement(element)));
        }

        return parsedSet;
    }

    private RElement parseElement(final Element element) {
        return new RElement(element.getModelId(), element.getUUID(), element.getLabel(), element.sortedProperties());
    }

    private int countClasses(final Set<RMatch> set) {
        final ArrayList<RElement> classes = new ArrayList<>();
        for (final RMatch tuple : set) {
            classes.addAll(tuple.getElements());
        }
        return classes.size();
    }

}