package de.variantsync.matching.raqun;

import de.variantsync.matching.raqun.data.*;
import de.variantsync.matching.raqun.similarity.SimilarityFunction;
import de.variantsync.matching.raqun.similarity.WeightMetric;
import de.variantsync.matching.raqun.tree.KDTree;
import de.variantsync.matching.raqun.tree.PropertyBasedVectorization;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.*;

public class RaQuNWorkflowTest {
    String pathToSimpleDataset = Paths.get("src", "test", "resources",
            "datasets", "workflow_test_models.txt").toString();
    private static final MatchValidityConstraint validityConstraint = MatchValidityConstraint.ONE_TO_ONE;

    @Test
    public void testRaQunWorkflowWithNwMWeight() {
        testRaQuNWorkflow(new WeightMetric());
    }

    private void testRaQuNWorkflow(SimilarityFunction similarityFunction) {
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
        KDTree kDTree = new KDTree(models, PropertyBasedVectorization.class);

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