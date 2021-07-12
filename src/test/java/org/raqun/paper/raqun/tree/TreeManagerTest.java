package org.raqun.paper.raqun.tree;

import org.raqun.paper.raqun.data.CandidatePair;
import org.raqun.paper.raqun.data.RElement;
import org.raqun.paper.raqun.data.RModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TreeManagerTest {

    @Test
    public void initializationWithTwoModelsAndLargeVectorization() {
        RModel modelA = getSimpleModel("A");
        RModel modelB = getSimpleModel("B");

        List<RModel> modelList = new ArrayList<>();
        modelList.add(modelA);
        modelList.add(modelB);

        TreeManager manager = new TreeManager(modelList, TreeManager.EVectorization.PROPERTY_INDEX);
        assert manager.getNumberOfInputModels() == 2;
        assert manager.getNumberOfElementsInTree() == 2;
        assert manager.getElementsInTree().size() == 2;
        assert !(manager.getIndexVectorFactory() instanceof CharacterIndexVectorFactory);
    }

    @ParameterizedTest
    @EnumSource(TreeManager.EVectorization.class)
    public void allElementsAreFoundForQueryWithHighK(TreeManager.EVectorization vectorization) {
        // k >= number of elements
        TreeManager manager = initializeTreeManager(vectorization);
        assert manager.getNumberOfElementsInTree() == 5;
        List<RElement> elements = manager.getElementsInTree();

        RElement queryElement = findElement(elements, "A", "ele1");

        List<QueryResult> queryResultsKEquals = manager.queryElementsOfKNearestPoints(queryElement, 5);
        List<QueryResult> queryResultsKGreater = manager.queryElementsOfKNearestPoints(queryElement, 6);
        assert queryResultsKEquals.size() == 5;
        assert queryResultsKGreater.size() == 5;
        for (RElement element : elements) {
            assert queryResultsContainElement(queryResultsKEquals, element);
            assert queryResultsContainElement(queryResultsKGreater, element);
        }
    }

    @ParameterizedTest
    @EnumSource(TreeManager.EVectorization.class)
    public void onlyElementsAtSamePointAreFoundWithKeq1(TreeManager.EVectorization vectorization) {
        // k == 1
        TreeManager manager = initializeTreeManager(vectorization);
        assert manager.getNumberOfElementsInTree() == 5;
        List<RElement> elements = manager.getElementsInTree();

        RElement queryElement = findElement(elements, "A", "ele1");

        List<QueryResult> queryResults = manager.queryElementsOfKNearestPoints(queryElement, 1);
        assert queryResults.size() == 2;
        for (QueryResult result : queryResults) {
            assert Double.compare(result.getDistance(), 0.0d) == 0;

            RElement element = result.getElement();
            assert element.getUUID().equals("0");
            assert element.getName().equals("ele1");
        }
    }

    @ParameterizedTest
    @EnumSource(TreeManager.EVectorization.class)
    public void candidatesFromSameModelAreFiltered(TreeManager.EVectorization vectorization) {
        TreeManager manager = initializeTreeManager(vectorization);
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
    @EnumSource(TreeManager.EVectorization.class)
    public void allExpectedCandidatePairsAreFoundWithDynamicK(TreeManager.EVectorization vectorization) {
        TreeManager manager = initializeTreeManager(vectorization);

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

    private boolean queryResultsContainElement(List<QueryResult> results, RElement element) {
        boolean containsElement = false;
        for (QueryResult result : results) {
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

    private TreeManager initializeTreeManager(TreeManager.EVectorization vectorization) {
        return new TreeManager(generateModels(), vectorization);
    }
}
