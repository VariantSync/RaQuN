package de.variantsync.matching.raqun;

import de.variantsync.matching.raqun.data.CandidatePair;
import de.variantsync.matching.raqun.data.RModel;
import de.variantsync.matching.raqun.vectorization.IVectorization;
import de.variantsync.matching.raqun.tree.KDTree;
import de.variantsync.matching.raqun.tree.TreeNeighbor;
import de.variantsync.matching.raqun.validity.IValidityConstraint;
import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.similarity.ISimilarityFunction;
import de.variantsync.matching.raqun.data.RMatch;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Our RaQuN matching algorithm as presented in the paper (Algorithm 1)
 */
public class RaQuN {
    protected final IVectorization vectorizationFunction;
    protected final IValidityConstraint validityConstraint;
    protected final ISimilarityFunction similarityFunction;
    protected final int nNearestNeighbors;
    protected Set<CandidatePair> candidatePairs;
    protected int numProcessedElements;

    /**
     * Create a new configuration of RaQuN.
     *
     * @param vectorizationFunction used to map elements to point the the kd-trees vector space
     * @param validityConstraint used to assess whether a match is valid
     * @param similarityFunction used to determine the similarity of elements and their match confidence
     * @param nNearestNeighbors retrieved during the nearest neighbor search
     */
    public RaQuN(IVectorization vectorizationFunction, IValidityConstraint validityConstraint, ISimilarityFunction similarityFunction, int nNearestNeighbors) {
        this.vectorizationFunction = vectorizationFunction;
        this.validityConstraint = validityConstraint;
        this.similarityFunction = similarityFunction;
        this.nNearestNeighbors = nNearestNeighbors;
    }

    /**
     * Create a new configuration of RaQuN.
     *
     * @param vectorizationFunction used to map elements to point the the kd-trees vector space
     * @param validityConstraint used to assess whether a match is valid
     * @param similarityFunction used to determine the similarity of elements and their match confidence
     */
    public RaQuN(IVectorization vectorizationFunction, IValidityConstraint validityConstraint, ISimilarityFunction similarityFunction) {
        this.vectorizationFunction = vectorizationFunction;
        this.validityConstraint = validityConstraint;
        this.similarityFunction = similarityFunction;
        this.nNearestNeighbors = 0;
    }

    /**
     * This is algorithm 1 in the paper.
     * @param models The models that should be matched.
     * @return A matching of the given models.
     */
    public Set<RMatch> match(Collection<RModel> models) {
        // Initialize the vectorization function
        vectorizationFunction.initialize(models);

        Set<RElement> elements = models.stream().flatMap((m) -> m.getElements().stream()).collect(Collectors.toSet());
        // Phase 1: Candidate Initialization (Algorithm 1, lines 2-7)
        KDTree kdTree = new KDTree(vectorizationFunction);
        elements.forEach(kdTree::add);

        // Phase 2: Candidate Search (Algorithm 1, lines 8-17)
        int k_prime = nNearestNeighbors == 0 ? models.size() : nNearestNeighbors;
        this.candidatePairs = findAllCandidates(kdTree, k_prime);
        Set<RElement> allElements = new HashSet<>(kdTree.getElementsInTree());
        this.numProcessedElements = allElements.size();

        // Phase 3: Matching (Algorithm 1, lines 18-27)
        return computeMatching(candidatePairs, allElements);
    }

    /**
     * Create a set of candidate pairs for the matching phase, by first searching for the k-nearest neighbors of each
     * element in the tree, and creating a CandidatePair for each element-neighbor pair.
     * @param k The number of neighbors to consider.
     * @return The set of candidate pairs
     */
    protected Set<CandidatePair> findAllCandidates(KDTree tree, int k) {
        Set<CandidatePair> candidatePairs = new HashSet<>();
        for (RElement element : tree.getElementsInTree()) {
            List<TreeNeighbor> treeNeighbors = tree.collectNearestNeighbors(element, k);
            for (TreeNeighbor result : treeNeighbors) {
                double distance = result.getDistance();
                RElement similarElement = result.getElement();
                if (validityConstraint.isValid(new RMatch(element, similarElement))) {
                    // We only add elements as candidates if the resulting match is considered valid
                    candidatePairs.add(new CandidatePair(element, similarElement, distance));
                }
            }
        }
        return candidatePairs;
    }

    /**
     * Start the matching phase with the given match candidates.
     * @param candidatePairs comprise pairs of elements that might be matched
     * @param allElements should comprise all elements to determine which elements are missing ich the candidatePairs
     * @return The matching for the dataset
     */
    protected Set<RMatch> computeMatching(Set<CandidatePair> candidatePairs, Set<RElement> allElements) {
        // Calculate the match confidence of the pairs, filter and sort (Algorithm 1, line 18
        PairSortTree sortedPairs = filterAndSort(candidatePairs);

        // Initialize the set of tuples (Algorithm 1, line 19)
        Set<RMatch> initialMatches = new HashSet<>();
        allElements.forEach(e -> initialMatches.add(new RMatch(e)));
        Set<RMatch> resultSet = new HashSet<>(initialMatches);

        // Algorithm 1, lines 20-27
        while (sortedPairs.size() > 0) {
            // M <- get next pair and remove it from the set of pairs
            CandidatePair match = Objects.requireNonNull(sortedPairs.pollFirst());
            RElement firstT = match.getFirst();
            RElement secondT = match.getSecond();

            Set<RMatch> selectedTuples = new HashSet<>();
            for (RMatch tuple : resultSet) {
                if (tuple.contains(firstT) || tuple.contains(secondT)) {
                    selectedTuples.add(tuple);
                }
            }

            if (similarityFunction.shouldMatch(selectedTuples)) {
                RMatch mergedTuple = RMatch.getMergedMatch(selectedTuples);
                if (mergedTuple != null && validityConstraint.isValid(mergedTuple)) {
                    // Remove all selected tuple and add their merged result instead
                    resultSet.removeAll(selectedTuples);
                    resultSet.add(mergedTuple);
                }
            }
        }

        return resultSet;
    }

    /**
     * Filter candidate pairs with a match confidence of 0 and sort them descending by confidence
     * @param candidatePairs that are to be filtered and sorted
     * @return A tree that contains the sorted pairs
     */
    protected PairSortTree filterAndSort(Set<CandidatePair> candidatePairs) {
        // The pairs are sorted descending by match confidence
        PairSortTree pairSortTree = new PairSortTree();
        for (CandidatePair candidatePair : candidatePairs) {
            double matchConfidence = similarityFunction.getMatchConfidence(candidatePair);
            if (matchConfidence > 0.0) {
                candidatePair.setMatchConfidence(matchConfidence);
                pairSortTree.add(candidatePair);
            }
        }
        return pairSortTree;
    }

    /**
     *
     * @return Set of candidate pairs of the last computed matching
     */
    public Set<CandidatePair> getCandidatePairs() {
        return candidatePairs;
    }

    /**
     *
     * @return the number of elements that were processed during the last matching
     */
    public int getNumProcessedElements() {
        return numProcessedElements;
    }

    /**
     * A TreeSet that can be used to sort CandidatePairs by match confidence.
     */
    protected static class PairSortTree extends TreeSet<CandidatePair> {
        private PairSortTree() {
            super((pairA, pairB) -> {
                int compDouble = Double.compare(pairB.getMatchConfidence(), pairA.getMatchConfidence());
                if (compDouble == 0) {
                    return pairA.toString().compareTo(pairB.toString());
                } else {
                    return compDouble;
                }
            });
        }
    }

}