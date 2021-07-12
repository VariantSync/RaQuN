package de.variantsync.matching.raqun.tree;

import de.variantsync.matching.raqun.data.CandidatePair;
import de.variantsync.matching.raqun.data.RModel;
import de.variantsync.matching.raqun.data.RElement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class KDTreeTest {

    @Test
    public void initializationWithTwoModelsAndLargeVectorization() {
        RModel modelA = getSimpleModel("A");
        RModel modelB = getSimpleModel("B");

        List<RModel> modelList = new ArrayList<>();
        modelList.add(modelA);
        modelList.add(modelB);

        KDTree manager = new KDTree(modelList, KDTree.EVectorization.PROPERTY_INDEX);
        assert manager.getNumberOfInputModels() == 2;
        assert manager.getNumberOfElementsInTree() == 2;
        assert manager.getElementsInTree().size() == 2;
        assert !(manager.getIndexVectorFactory() instanceof CharacterVectorFactory);
    }

    @ParameterizedTest
    @EnumSource(KDTree.EVectorization.class)
    public void allElementsAreFoundForQueryWithHighK(KDTree.EVectorization vectorization) {
        // k >= number of elements
        KDTree manager = initializeTreeManager(vectorization);
        assert manager.getNumberOfElementsInTree() == 5;
        List<RElement> elements = manager.getElementsInTree();

        RElement queryElement = findElement(elements, "A", "ele1");

        List<TreeNeighbor> queryResultsKEquals = manager.queryElementsOfKNearestPoints(queryElement, 5);
        List<TreeNeighbor> queryResultsKGreater = manager.queryElementsOfKNearestPoints(queryElement, 6);
        assert queryResultsKEquals.size() == 5;
        assert queryResultsKGreater.size() == 5;
        for (RElement element : elements) {
            assert queryResultsContainElement(queryResultsKEquals, element);
            assert queryResultsContainElement(queryResultsKGreater, element);
        }
    }

    @ParameterizedTest
    @EnumSource(KDTree.EVectorization.class)
    public void onlyElementsAtSamePointAreFoundWithKeq1(KDTree.EVectorization vectorization) {
        // k == 1
        KDTree manager = initializeTreeManager(vectorization);
        assert manager.getNumberOfElementsInTree() == 5;
        List<RElement> elements = manager.getElementsInTree();

        RElement queryElement = findElement(elements, "A", "ele1");

        List<TreeNeighbor> treeNeighbors = manager.queryElementsOfKNearestPoints(queryElement, 1);
        assert treeNeighbors.size() == 2;
        for (TreeNeighbor result : treeNeighbors) {
            assert Double.compare(result.getDistance(), 0.0d) == 0;

            RElement element = result.getElement();
            assert element.getUUID().equals("0");
            assert element.getName().equals("ele1");
        }
    }

    @ParameterizedTest
    @EnumSource(KDTree.EVectorization.class)
    public void candidatesFromSameModelAreFiltered(KDTree.EVectorization vectorization) {
        KDTree manager = initializeTreeManager(vectorization);
        List<RElement> elements = manager.getElementsInTree();

        for (RElement queryElement : elements) {
            Set<CandidatePair> queryResults = manager.findCandidatesForElement(queryElement, 1);
            for (CandidatePair candidatePair : queryResults) {
                RElement candidate = candidatePair.getSecond();
                assert !queryElement.getModelID().equals(candidate.getModelID());
            }
        }
    }

    @ParameterizedTest
    @EnumSource(KDTree.EVectorization.class)
    public void allExpectedCandidatePairsAreFoundWithDynamicK(KDTree.EVectorization vectorization) {
        KDTree manager = initializeTreeManager(vectorization);

        Set<CandidatePair> queryResults = manager.findKCandidates(-1);
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

    private boolean queryResultsContainElement(List<TreeNeighbor> results, RElement element) {
        boolean containsElement = false;
        for (TreeNeighbor result : results) {
            if (result.getElement() == element) {
                containsElement = true;
                break;
            }
        }
        return containsElement;
    }

    private RElement findElement(List<RElement> elements, String modelID, String elementName) {
        for (RElement element : elements) {
            if (element.getModelID().equals(modelID)) {
                if (element.getName().equals(elementName)) {
                    return element;
                }
            }
        }
        return null;
    }

    private RModel getSimpleModel(String modelID) {
        RModel model = new RModel(modelID);
        List<String> properties = new ArrayList<>();
        properties.add("n_cat");
        properties.add("property1");
        RElement element = new RElement(modelID, "Cat", "0", properties);
        model.addElement(element);
        return model;
    }

    private List<RModel> generateModels() {
        RModel modelA = new RModel("A");
        RElement element1A = new RElement("A", "ele1", "0",
                Arrays.asList("n_ele1", "prop1", "prop2", "zzz"));
        RElement element2A = new RElement("A", "ele2", "1",
                Arrays.asList("n_ele2", "prop3", "prop4", "zzz"));
        modelA.addElement(element1A);
        modelA.addElement(element2A);

        RModel modelB = new RModel("B");
        RElement element1B = new RElement("B", "ele1", "0",
                Arrays.asList("n_ele1", "prop1", "prop2", "zzz"));
        RElement element2B = new RElement("B", "ele2", "1",
                Arrays.asList("n_ele2", "prop3", "xyz_abc", "zzz"));
        RElement element3B = new RElement("B", "ele3", "2",
                Arrays.asList("n_ele3", "prop1", "prop5", "prop6"));
        modelB.addElement(element1B);
        modelB.addElement(element2B);
        modelB.addElement(element3B);

        return Arrays.asList(modelA, modelB);
    }

    private KDTree initializeTreeManager(KDTree.EVectorization vectorization) {
        return new KDTree(generateModels(), vectorization);
    }
}