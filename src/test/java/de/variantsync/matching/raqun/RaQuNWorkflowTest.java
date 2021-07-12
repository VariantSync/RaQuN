package de.variantsync.matching.raqun;

import de.variantsync.matching.raqun.data.*;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.similarity.WeightMetric;
import de.variantsync.matching.raqun.tree.KDTree;
import de.variantsync.matching.raqun.tree.PropertyBasedVectorization;
import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.validity.OneToOneValidity;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.variantsync.matching.raqun.tree.KDTreeTest.initializeTree;

public class RaQuNWorkflowTest {
    Path pathToSimpleDataset = Paths.get("src", "test", "resources",
            "datasets", "workflow_test_models.txt");
    private static final IValidityConstraint validityConstraint = new OneToOneValidity();

    @Test
    public void testRaQunWorkflowWithNwMWeight() {
        testRaQuNWorkflow(new WeightMetric());
    }

    private void testRaQuNWorkflow(ISimilarityFunction similarityFunction) {
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

        similarityFunction.setNumberOfModels(3);
        RaQuN raqun = new RaQuN(PropertyBasedVectorization.class, validityConstraint, similarityFunction, 3);
        Set<RMatch> matching = raqun.match(models);

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

    @Test
    public void allExpectedCandidatePairsAreFound() {
        RaQuN raqun = new RaQuN(PropertyBasedVectorization.class, new OneToOneValidity(), new WeightMetric(), 1);
        KDTree tree = initializeTree();

        Set<CandidatePair> queryResults = raqun.findAllCandidates(tree,2);
        // We expect 4 candidate pairs, because candidates from the same model are filtered and CandidatePairs do not
        // have a defined order
        assert queryResults.size() == 4;
        boolean ele1IsMatched = false;
        boolean ele1IsMatchedTwice = false;
        boolean ele2IsMatched = false;
        boolean ele3IsMatched = false;
        for (CandidatePair candidatePair : queryResults) {
            String nameOfFirst = candidatePair.getFirst().getName();
            String nameOfSecond = candidatePair.getSecond().getName();
            if (nameOfFirst.equals("ele1") && nameOfSecond.equals("ele1")) {
                ele1IsMatched = true;
            }
            if (nameOfFirst.equals("ele1") && nameOfSecond.equals("ele2")|| nameOfFirst.equals("ele2") && nameOfSecond.equals("ele1")) {
                ele1IsMatchedTwice = true;
            }
            if (nameOfFirst.equals("ele2") && nameOfSecond.equals("ele2")) {
                ele2IsMatched = true;
            }
            if (nameOfFirst.equals("ele3") || nameOfSecond.equals("ele3")) {
                ele3IsMatched = true;
            }
        }

        assert ele1IsMatched;
        assert ele1IsMatchedTwice;
        assert ele2IsMatched;
        assert ele3IsMatched;
    }
}