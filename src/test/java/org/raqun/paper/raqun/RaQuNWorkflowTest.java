package org.raqun.paper.raqun;

import org.raqun.paper.raqun.data.*;
import org.raqun.paper.raqun.similarity.WeightMetric;
import org.raqun.paper.raqun.similarity.SimilarityFunction;
import org.raqun.paper.raqun.tree.KDTree;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.file.Paths;
import java.util.*;

public class RaQuNWorkflowTest {
    String pathToSimpleDataset = Paths.get("src", "test", "resources",
            "datasets", "workflow_test_models.txt").toString();
    private static final MatchValidityConstraint validityConstraint = MatchValidityConstraint.ONE_TO_ONE;

    @ParameterizedTest
    @EnumSource(KDTree.EVectorization.class)
    public void testRaQunWorkflowWithNwMWeight(KDTree.EVectorization vectorization) {
        testRaQuNWorkflow(vectorization, new WeightMetric());
    }

    private void testRaQuNWorkflow(KDTree.EVectorization vectorization, SimilarityFunction similarityFunction) {
        // Load a simple test model
        RDataset dataset = new RDataset("SimpleDataset");
        dataset.loadFileContent(pathToSimpleDataset);
        ArrayList<RModel> models = dataset.getModels();

        // Simple validation of loaded models
        assert models.size() == 3;
        assert models.get(0).getModelID().equals("A");
        assert models.get(1).getModelID().equals("B");
        assert models.get(2).getModelID().equals("C");
        for (RModel model : models) {
            assert model.getElements().size() == 4;
        }

        // Shuffle the models for more randomness
        Collections.shuffle(models);

        // Initialize Tree
        KDTree kDTree = new KDTree(models, vectorization);

        // Get CandidatePairs from tree
        Set<CandidatePair> candidatePairs = kDTree.findKCandidates(-1);

        // run RaQuN merge algorithm
        Set<RElement> allElements = new HashSet<>(kDTree.getElementsInTree());
        similarityFunction.setNumberOfModels(3);
        Set<RMatch> matching = RaQuNMatcher.match(candidatePairs, allElements, similarityFunction, validityConstraint);

        // Validate result matching
        /*
        Expected Tuple:
        (Display-A, Display-B, Display-C)
        (Room-A, Room-B, Room-C)
        (Building-A, Building-C)
        (Staff-A, Staff-B)
        (Doctor-B, Nurse-C)
         */
        assert matching.size() == 5;
        // Assert that each of the tuples above is present
        for (RMatch tuple : matching) {
            Collection<RElement> elements = tuple.getElements();
            String tupleContent = null;
            for (RElement element : elements) {
                if (tupleContent == null) {
                    tupleContent = element.getName();
                } else {
                    switch (tupleContent) {
                        case "Display":
                        case "Room":
                        case "Building":
                        case "Staff":
                            assert tupleContent.equals(element.getName());
                            break;
                        case "Doctor":
                        case "Nurse":
                            assert element.getName().equals("Doctor") || element.getName().equals("Nurse");
                            break;
                        default:
                            throw new AssertionError("Forgot a name");
                    }
                }
            }
        }
    }
}