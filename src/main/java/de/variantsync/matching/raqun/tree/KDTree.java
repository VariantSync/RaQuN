package de.variantsync.matching.raqun.tree;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.savarese.spatial.NearestNeighbors;
import com.savarese.spatial.NearestNeighbors.Entry;
import de.variantsync.matching.raqun.data.CandidatePair;
import de.variantsync.matching.raqun.data.RModel;
import de.variantsync.matching.raqun.data.RElement;

/**
 * A k-dimensional tree (kd-tree) in which the elements of a model are matched to points in a vector space
 * according to a provided vectorization function.
 */
public class KDTree {
    // The wrapped KDTree implementation of savarese
    private final com.savarese.spatial.KDTree<Double, RVector, List<RElement>> tree = new com.savarese.spatial.KDTree<>();
    // The vectorization function that is used to map elements to points in the vector space
    private final Vectorization vectorization;
    // A list of all elements that were added to the tree
    private final List<RElement> elementsInTree;
    // The number of input models
    private final int numberOfInputModels;
    // An instance of the nearest neighbor search implementation for com.savarese.spatiald.KDTree
    private final NearestNeighbors<Double, RVector, List<RElement>> nearestNeighbors = new NearestNeighbors<>();

    /**
     * Initialize a new KDTree and add all model elements according to the provided vectorization. The vectorization
     * is instantiated with the default constructor and then initialized with Vectorization::initialize.
     *
     * @param models The input models of which the elements are added to the tree
     * @param vectorization The vectorization function that maps a element to a point in the trees vector space.
     */
    public KDTree(List<RModel> models, Class<? extends Vectorization> vectorization) {
        numberOfInputModels = models.size();
        List<RElement> elements = new ArrayList<>();
        for (RModel model : models) {
            elements.addAll(model.getElements());
        }
        try {
            this.vectorization = vectorization.getConstructor().newInstance();
            this.vectorization.initialize(models);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.err.println("Provided invalid implementation for Vectorization. A Vectorization with only a default" +
                    "constructor is expected.");
            throw new IllegalArgumentException(e);
        }
        elements.forEach(this::add);
        this.elementsInTree = elements;
    }

    /**
     *
     * @return elements in this tree
     */
    public List<RElement> getElementsInTree() {
        return elementsInTree;
    }

    /**
     *
     * @return number of input models
     */
    public int getNumberOfInputModels() {
        return numberOfInputModels;
    }

    /**
     * Create a set of candidate pairs for the matching phase, by first searching for the k-nearest neighbors of each
     * element in the tree, and creating a CandidatePair for each element-neighbor pair.
     * @param k The number of neighbors to consider.
     * @return The set of candidate pairs
     */
    // TODO: Move to RaQuN algorithm
    public Set<CandidatePair> findAllCandidates(int k) {
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

    /**
     *
     * @return number of stored elements
     */
    public int getNumberOfElementsInTree() {
        return tree.values().stream().mapToInt(List::size).sum();
    }

    /**
     *
     * @return the used Vectorization instance
     */
    public Vectorization getVectorization() {
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

    /**
     * Collect the candidate pairs for a specific element and a specific number of neighbors that are considered, by
     * first searching for the k-nearest neighbors of the element, and then creating a CandidatePair for each
     * element-neighbor pair.
     * @param element The element for which the match candidates are to be collected
     * @param k The number of neighbors that should be considered
     * @return set of candidate pairs for the given element
     */
    // TODO: Move this to common RaQuN algorithm?
    public Set<CandidatePair> findCandidatesForElement(RElement element, int k) {
        List<TreeNeighbor> treeNeighbors = collectNearestNeighbors(element, k);
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

    /**
     * Collect the nearest neighbors for the given element
     * @param element for which the neighbors are to be collected
     * @param k the number of neighbors that are to be considered
     * @return list of neighbors
     */
    public List<TreeNeighbor> collectNearestNeighbors(RElement element, int k) {
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