package de.variantsync.matching.raqun.data;

import java.util.Objects;

/**
 * Pair of elements that are potential match candidates.
 */
public class CandidatePair {
    private final RElement first;
    private final RElement second;
    private final double distanceInTree;
    private double matchConfidence;

    /**
     * Initialize a new CandidatePair
     * @param first element to be matched
     * @param second element to be matched
     * @param distanceInTree of the two elements
     */
    public CandidatePair(final RElement first, final RElement second, final double distanceInTree) {
        this.first = first;
        this.second = second;
        this.distanceInTree = distanceInTree;
    }

    /**
     *
     * @return First element in this pair
     */
    public RElement getFirst() {
        return first;
    }

    /**
     *
     * @return Second element in this pair
     */
    public RElement getSecond() {
        return second;
    }

    /**
     *
     * @return Distance of the two elements in the vector space of the kd-tree
     */
    public double getDistanceInTree() {
        return distanceInTree;
    }

    /**
     *
     * @return Confidence that the two elements should be matched. Greater values mean greater confidence.
     */
    public double getMatchConfidence() {
        return matchConfidence;
    }

    /**
     * @param matchConfidence that the two elements should be matched. Greater values mean greater confidence.
     */
    public void setMatchConfidence(final double matchConfidence) {
        this.matchConfidence = matchConfidence;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CandidatePair candidatePair = (CandidatePair) o;

        return first.equals(candidatePair.first) && Objects.equals(second, candidatePair.second) ||
                Objects.equals(first, candidatePair.second) && Objects.equals(second, candidatePair.first);
    }

    @Override
    public int hashCode() {
        int baseHash = 0;
        if (first != null) {
            baseHash += first.hashCode();
        }
        if (second != null) {
            baseHash += second.hashCode();
        }
        return baseHash;
    }


}