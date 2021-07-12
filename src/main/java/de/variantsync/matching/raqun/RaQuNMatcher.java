package de.variantsync.matching.raqun;

import de.variantsync.matching.raqun.data.CandidatePair;
import de.variantsync.matching.raqun.data.MatchValidityConstraint;
import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.similarity.SimilarityFunction;
import de.variantsync.matching.raqun.data.RMatch;

import java.util.*;

// TODO: Give RaQuN the interface which is presented in the paper.
public class RaQuNMatcher {

    public static Set<RMatch> match(Set<CandidatePair> candidatePairs,
                                    Set<RElement> allElements,
                                    SimilarityFunction similarityFunction,
                                    MatchValidityConstraint validityConstraint) {
        // Calculate the match confidence of the pairs and sort them accordingly
        for (CandidatePair candidatePair : candidatePairs) {
            candidatePair.setMatchConfidence(similarityFunction.getMatchConfidence(candidatePair));
        }

        // The pairs are sorted descending by match confidence
        PairSortTree pairSortTree = new PairSortTree();
        pairSortTree.addAll(candidatePairs);

        // Initialize the set of tuples
        Set<RMatch> initialTuple = new HashSet<>();
        allElements.forEach(e -> initialTuple.add(new RMatch(similarityFunction, validityConstraint, e)));

        return RaQuNMatcher.match(initialTuple, pairSortTree, similarityFunction);
    }

    protected static Set<RMatch> match(Set<RMatch> initialTuple, PairSortTree pairSortTree,
                                       SimilarityFunction similarityFunction) {
        // All elements are added to the result
        Set<RMatch> resultSet = new HashSet<>(initialTuple);

        while (pairSortTree.size() > 0) {
            // M <- get next pair and remove it from the set of pairs
            CandidatePair match = Objects.requireNonNull(pairSortTree.pollFirst());
            RElement firstT = match.getFirst();
            RElement secondT = match.getSecond();

            Set<RMatch> selectedTuples = new HashSet<>();
            for (RMatch tuple : resultSet) {
                if (tuple.contains(firstT) || tuple.contains(secondT)) {
                    selectedTuples.add(tuple);
                }
            }

            if (similarityFunction.shouldMatch(selectedTuples)) {
                RMatch mergedTuple = RMatch.getMergedTuple(selectedTuples);
                if (mergedTuple != null && mergedTuple.isValid()) {
                    // Remove all selected tuple and add their merged result instead
                    resultSet.removeAll(selectedTuples);
                    resultSet.add(mergedTuple);
                }
            }
        }

        return resultSet;
    }

    private static class PairSortTree extends TreeSet<CandidatePair> {
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