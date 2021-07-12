package org.raqun.paper.experiments.baseline;

import org.raqun.paper.experiments.common.MatchStatistic;
import org.raqun.paper.experiments.common.ExperimentHelper;
import org.raqun.paper.experiments.common.ExperimentSetup;
import org.raqun.paper.experiments.common.MethodAdapter;
import org.raqun.paper.pairwise.HungarianPairwiseMatcher;
import org.raqun.paper.raqun.data.RElement;
import org.raqun.paper.raqun.data.RMatch;
import org.raqun.paper.nwm.alg.merge.ChainingOptimizingMerger;
import org.raqun.paper.nwm.alg.merge.MultiModelMerger;
import org.raqun.paper.nwm.common.AlgoUtil;
import org.raqun.paper.nwm.domain.Element;
import org.raqun.paper.nwm.domain.Model;
import org.raqun.paper.nwm.domain.Tuple;
import org.raqun.paper.nwm.execution.RunResult;
import org.raqun.paper.raqun.similarity.WeightMetric;

import java.util.*;

import static org.raqun.paper.ExperimentRunner.PRINT_MATCH;

public class BaselineAlgoAdapter implements MethodAdapter {
    private final ExperimentSetup setup;
    private WeightMetric weightCalculator;
    private final EBaselineImplementation algorithmToApply;

    public BaselineAlgoAdapter(ExperimentSetup setup, EBaselineImplementation algorithm) {
        this.setup = setup;
        this.algorithmToApply = algorithm;
    }

    public void run() {
        for (int runID = 0; runID < setup.numberOfRepeats; runID++) {
            // Here, we use the Model class of Rubin an Chechik
            ArrayList<Model> models = Model.readModelsFile(setup.datasetFile);
            List<ArrayList<Model>> chunks = ExperimentHelper.getDatasetChunks(models, setup.chunkSize);

            for (ArrayList<Model> chunk : chunks) {
                int sizeOfLargestModel = getSizeOfLargestModel(chunk);
                int numberOfModels = chunk.size();
                MatchStatistic matchStatistic = new MatchStatistic(0, setup.datasetName, setup.name,
                        numberOfModels, sizeOfLargestModel);
                weightCalculator = new WeightMetric(numberOfModels);

                AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = false;

                List<Tuple> solution;
                RunResult runResult;
                ArrayList<Model> modelSubList = new ArrayList<>(chunk);

                if(algorithmToApply == EBaselineImplementation.NwM) {
                    // To the best of our knowledge, this is the prototype implementation of nwm used in the work of
                    // Rubin and Chechik
                    // it achieves the matching weights that were presented in their publication
                    MultiModelMerger mmm = new ChainingOptimizingMerger(modelSubList);
                    mmm.run();
                    solution = mmm.getTuplesInMatch();
                    runResult = mmm.getRunResult(numberOfModels);
                } else {
                    HungarianPairwiseMatcher matcher = new HungarianPairwiseMatcher(modelSubList, algorithmToApply);
                    matcher.run();
                    solution = matcher.getResult();
                    runResult = matcher.getRunResult();
                    matchStatistic.setNumberOfComparisonsActuallyDone(matcher.getNumberOfComparisons());
                }

                // We parse the matching returned by NwM to our own data format, in order to do the evaluation
                Set<RMatch> mergedModel = parseSolution(solution, chunk);

                if (PRINT_MATCH) {
                    int numberOfClasses = countClasses(mergedModel);
                    System.out.println("Number of Classes: " + numberOfClasses);
                    for (RMatch tuple : mergedModel) {
                        System.out.println(tuple.getLongString());
                    }
                }
                // Save the results
                matchStatistic.calculateStatistics(mergedModel, getTimeInSeconds(runResult.execTime));
                System.out.println(matchStatistic);
                System.out.println();

                matchStatistic.writeAsJSON(setup.resultFile, true);
                //executionStatistic.writeModel(setup.mergeResultFile);
            }
        }
    }

    private int getSizeOfLargestModel(List<Model> models) {
        int size = 0;
        for(Model model : models) {
            if (model.getElements().size() > size) {
                size = model.getElements().size();
            }
        }
        return size;
    }

    private double getTimeInSeconds(double time) {
        return time / 1000;
    }

    private Set<RMatch> parseSolution(List<Tuple> solution, ArrayList<Model> chunk) {
        // Create a set of all elements to later find isolated ones
        Set<Element> allElements = new HashSet<>();
        for (Model model : chunk) {
            allElements.addAll(model.getElements());
        }

        Set<RMatch> parsedSet = new HashSet<>();

        // Create RMatch for all tuple in the solution
        for (Tuple tuple : solution) {
            List<RElement> nodes = new ArrayList<>();
            for (Element e : tuple.getRealElements()) {
                // Remove the current element from the set of all elements
                if(!allElements.remove(e)) {
                    throw new RuntimeException("ERROR: Element not in set of all elements...");
                }
                nodes.add(parseElement(e));
            }
            parsedSet.add(new RMatch(weightCalculator, setup.validityConstraint, nodes.toArray(new RElement[0])));
        }

        // Create RMatch for all elements that were not part of the solution, they have to be counted as FN and FP
        for (Element element : allElements) {
            parsedSet.add(new RMatch(weightCalculator, setup.validityConstraint, parseElement(element)));
        }

        return parsedSet;
    }

    private RElement parseElement(Element element) {
        return new RElement(element.getModelId(), element.getLabel(), element.getUUID(), element.sortedProperties());
    }

    private int countClasses(Set<RMatch> set) {
        ArrayList<RElement> classes = new ArrayList<>();
        for (RMatch tuple : set) {
            classes.addAll(tuple.getElements());
        }
        return classes.size();
    }

}
