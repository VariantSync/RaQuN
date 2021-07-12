package de.variantsync.matching.raqun.tree;

import java.util.*;

import com.savarese.spatial.NearestNeighbors;
import com.savarese.spatial.NearestNeighbors.Entry;
import de.variantsync.matching.raqun.data.CandidatePair;
import de.variantsync.matching.raqun.data.RModel;
import de.variantsync.matching.raqun.data.RElement;

public class KDTree {
    private final com.savarese.spatial.KDTree<Double, RVector, List<RElement>> tree = new com.savarese.spatial.KDTree<>();
    private final Vectorization vectorization;
    private final List<RElement> elementsInTree;
    private final int numberOfInputModels;
    private final NearestNeighbors<Double, RVector, List<RElement>> nearestNeighbors = new NearestNeighbors<>();

    public KDTree(List<RModel> models, Vectorization vectorization) {
        numberOfInputModels = models.size();
        List<RElement> elements = new ArrayList<>();
        for (RModel model : models) {
            elements.addAll(model.getElements());
        }
        this.vectorization = vectorization;
        elements.forEach(this::add);
        this.elementsInTree = elements;
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

    public Vectorization getIndexVectorFactory() {
        return this.vectorization;
    }

    private void add(RElement element) {
        RVector RVector = vectorization.vectorFor(element);
        if (tree.containsKey(RVector)) {
            tree.get(RVector).add(element);
        } else {
            List<RElement> elementsAtPoint = new ArrayList<>();
            elementsAtPoint.add(element);
            tree.put(RVector, elementsAtPoint);
        }
    }

    public Set<CandidatePair> findCandidatesForElement(RElement element, int k) {
        List<TreeNeighbor> treeNeighbors = queryElementsOfKNearestPoints(element, k);
        Set<CandidatePair> candidatePairs = new HashSet<>();
        for (TreeNeighbor result : treeNeighbors) {
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

    public List<TreeNeighbor> queryElementsOfKNearestPoints(RElement element, int k) {
        RVector queryPoint = vectorization.vectorFor(element);
        Entry<Double, RVector, List<RElement>>[] nearestNeighbors = this.nearestNeighbors.get(tree, queryPoint, k, false);

        List<TreeNeighbor> allNeighboringElements = new ArrayList<>();
        for (Entry<Double, RVector, List<RElement>> entry : nearestNeighbors) {
            List<RElement> elementsAtNeighboringPoint = entry.getNeighbor().getValue();
            // List of QueryResults, one result for each element at the neighboring point
            for(RElement neighboringElement : elementsAtNeighboringPoint) {
                // Map each element to a new QueryResult object and add it to the list of neighboring elements
                allNeighboringElements.add(new TreeNeighbor(neighboringElement, entry.getDistance()));
            }
        }

        return allNeighboringElements;
    }
}