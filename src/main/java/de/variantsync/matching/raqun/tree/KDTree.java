package de.variantsync.matching.raqun.tree;

import java.util.*;

import com.savarese.spatial.NearestNeighbors;
import com.savarese.spatial.NearestNeighbors.Entry;
import de.variantsync.matching.raqun.data.RElement;

/**
 * A k-dimensional tree (kd-tree) in which the elements of a model are matched to points in a vector space
 * according to a provided vectorization function.
 */
public class KDTree {
    // The wrapped KDTree implementation of savarese
    private final com.savarese.spatial.KDTree<Double, RVector, List<RElement>> tree = new com.savarese.spatial.KDTree<>();
    // The vectorization function that is used to map elements to points in the vector space
    private final IVectorization vectorization;
    // A list of all elements that were added to the tree
    private final List<RElement> elementsInTree;
    // An instance of the nearest neighbor search implementation for com.savarese.spatiald.KDTree
    private final NearestNeighbors<Double, RVector, List<RElement>> nearestNeighbors = new NearestNeighbors<>();

    /**
     * Initialize a new KDTree and add all model elements according to the provided vectorization. The vectorization
     * is instantiated with the default constructor and then initialized with Vectorization::initialize.
     *
     * @param vectorization The vectorization function that maps a element to a point in the trees vector space.
     */
    public KDTree(IVectorization vectorization) {
         this.vectorization = vectorization;
         this.elementsInTree = new LinkedList<>();
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
     * @return number of stored elements
     */
    public int getNumberOfElementsInTree() {
        return tree.values().stream().mapToInt(List::size).sum();
    }

    /**
     *
     * @return the used Vectorization instance
     */
    public IVectorization getVectorization() {
        return this.vectorization;
    }

    /**
     * Add the given element to the tree
     * @param element that is to be added
     */
    public void add(RElement element) {
        RVector RVector = vectorization.vectorFor(element);
        if (tree.containsKey(RVector)) {
            tree.get(RVector).add(element);
        } else {
            List<RElement> elementsAtPoint = new ArrayList<>();
            elementsAtPoint.add(element);
            tree.put(RVector, elementsAtPoint);
        }
        this.elementsInTree.add(element);
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
                if (neighboringElement != element) {
                    // Map each element to a new QueryResult object and add it to the list of neighboring elements
                    allNeighboringElements.add(new TreeNeighbor(neighboringElement, entry.getDistance()));
                }
            }
        }

        return allNeighboringElements;
    }
}