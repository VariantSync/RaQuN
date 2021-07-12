package org.raqun.paper.raqun.data;

import java.util.Objects;

public class CandidatePair {
    private final RElement first;
    private final RElement second;
    private final double distanceInTree;
    private double matchConfidence;

    public CandidatePair(RElement first, RElement second, double distanceInTree) {
        this.first = first;
        this.second = second;
        this.distanceInTree = distanceInTree;
    }

    public RElement getFirst() {
        return first;
    }

    public RElement getSecond() {
        return second;
    }

    public double getDistanceInTree() {
        return distanceInTree;
    }

    public double getMatchConfidence() {
        return matchConfidence;
    }

    public void setMatchConfidence(double matchConfidence) {
        this.matchConfidence = matchConfidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidatePair candidatePair = (CandidatePair) o;

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