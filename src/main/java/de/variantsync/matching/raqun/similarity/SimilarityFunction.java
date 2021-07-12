package de.variantsync.matching.raqun.similarity;

import de.variantsync.matching.raqun.data.CandidatePair;
import de.variantsync.matching.raqun.data.RMatch;
import de.variantsync.matching.raqun.data.RElement;

import java.util.Set;

public interface SimilarityFunction {

    boolean shouldMatch(Set<RMatch> tuples);

    void setNumberOfModels(int numberOfModels);

    double getMatchConfidence(RElement elementA, RElement elementB);

    default double getMatchConfidence(CandidatePair candidatePair) {
        return getMatchConfidence(candidatePair.getFirst(), candidatePair.getSecond());
    }

}