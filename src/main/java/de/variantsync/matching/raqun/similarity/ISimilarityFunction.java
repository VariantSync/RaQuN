package de.variantsync.matching.raqun.similarity;

import de.variantsync.matching.raqun.data.CandidatePair;
import de.variantsync.matching.raqun.data.RMatch;
import de.variantsync.matching.raqun.data.RElement;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A SimilarityFunction is applied to determine whether elements should be matched, and in order to sort pairs of match
 * candidates.
 */
public interface ISimilarityFunction {

    /**
     * Determine whether a set of given tuples should be matched
     * @param tuples the tuples that are considered
     * @return true, if the elements of all given tuples should be matched
     */
    boolean shouldMatch(Set<RMatch> tuples);

    /**
     *
     * @param numberOfModels The total number of models considered for matching
     */
    void setNumberOfModels(int numberOfModels);

    /**
     * Calculate the confidence that two elements should be matched to each other
     * @param elementA first element
     * @param elementB second element
     * @return A double value for the confidence. A greater value corresponds to a greater similarity of the two elements, hence,
     * a higher confidence that they should be matched
     */
    double getMatchConfidence(RElement elementA, RElement elementB);

    /**
     * Set parameters, such as a threshold for shouldMatch.
     * This method can be left empty, if no parameter is required for the SimilarityFunction
     * @param parameters The parameters for the similarity function
     */
    void setParameters(List<String> parameters);

    /**
     * Calculate the confidence that two elements in the CandidatePair should be matched
     * @param candidatePair pair of two possible match candidates
     * @return A double value for the confidence. A greater value corresponds to a greater similarity of the two elements, hence,
     * a higher confidence that they should be matched
     */
    default double getMatchConfidence(final CandidatePair candidatePair) {
        return getMatchConfidence(candidatePair.getFirst(), candidatePair.getSecond());
    }

}