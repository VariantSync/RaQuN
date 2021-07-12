package org.raqun.paper.raqun.tree;

import java.util.*;

import com.savarese.spatial.KDTree;
import com.savarese.spatial.NearestNeighbors;
import com.savarese.spatial.NearestNeighbors.Entry;
import org.raqun.paper.raqun.data.CandidatePair;
import org.raqun.paper.raqun.data.RElement;
import org.raqun.paper.raqun.data.RModel;

public class TreeManager {
    private final KDTree<Double, IndexVector, List<RElement>> tree = new KDTree<>();
    private final IndexVectorFactory indexVectorFactory;
    private final List<RElement> elementsInTree;
    private final int numberOfInputModels;
    private final NearestNeighbors<Double, IndexVector, List<RElement>> nearestNeighbors = new NearestNeighbors<>();

    public TreeManager(List<RModel> models, EVectorization vectorization) {
        numberOfInputModels = models.size();
        List<RElement> elements = new ArrayList<>();
        for (RModel model : models) {
            elements.addAll(model.getElements());
        }
        if (vectorization == EVectorization.PROPERTY_INDEX) {
            indexVectorFactory = new IndexVectorFactory(elements);
        } else {
            throw new IllegalArgumentException("Vectorization type " + vectorization + " not yet implemented.");
        }
        elements.forEach(this::add);
        this.elementsInTree = elements;
    }

    public enum EVectorization {
        PROPERTY_INDEX
    }

    public List<RElement> getElementsInTree() {
        return elementsInTree;
    }

    public int getNumberOfInputModels() {
        return numberOfInputModels;
    }

    public Set<CandidatePair> findKCandidates(int k) {
        boolean useDynamicK = k < 1;
        Set<CandidatePair> candidatePairs = new HashSet<>();
        for (RElement element : elementsInTree) {
            if (useDynamicK) {
                // Set a heuristic value for k
                k = numberOfInputModels;
            }
            candidatePairs.addAll(findCandidatesForElement(element, k));
        }
        return candidatePairs;
    }

    public int getNumberOfElementsInTree() {
        return tree.values().stream().mapToInt(List::size).sum();
    }

    public IndexVectorFactory getIndexVectorFactory() {
        return this.indexVectorFactory;
    }

    private void add(RElement element) {
        IndexVector indexVector = indexVectorFactory.vectorFor(element);
        if (tree.containsKey(indexVector)) {
            tree.get(indexVector).add(element);
        } else {
            List<RElement> elementsAtPoint = new ArrayList<>();
            elementsAtPoint.add(element);
            tree.put(indexVector, elementsAtPoint);
        }
    }

    public Set<CandidatePair> findCandidatesForElement(RElement element, int k) {
        List<QueryResult> queryResults = queryElementsOfKNearestPoints(element, k);
        Set<CandidatePair> candidatePairs = new HashSet<>();
        for (QueryResult result : queryResults) {
            double distance = result.getDistance();
            RElement similarElement = result.getElement();
            if (element.getModelID().equals(similarElement.getModelID())) {
                // We only add elements as candidates if they are from another model
                continue;
            }
            candidatePairs.add(new CandidatePair(element, similarElement, distance));
        }
        return candidatePairs;
    }

    public List<QueryResult> queryElementsOfKNearestPoints(RElement element, int k) {
        IndexVector queryPoint = indexVectorFactory.vectorFor(element);
        Entry<Double, IndexVector, List<RElement>>[] nearestNeighbors = this.nearestNeighbors.get(tree, queryPoint, k, false);

        List<QueryResult> allNeighboringElements = new ArrayList<>();
        for (Entry<Double, IndexVector, List<RElement>> entry : nearestNeighbors) {
            List<RElement> elementsAtNeighboringPoint = entry.getNeighbor().getValue();
            // List of QueryResults, one result for each element at the neighboring point
            for(RElement neighboringElement : elementsAtNeighboringPoint) {
                // Map each element to a new QueryResult object and add it to the list of neighboring elements
                allNeighboringElements.add(new QueryResult(neighboringElement, entry.getDistance()));
            }
        }

        return allNeighboringElements;
    }
}
