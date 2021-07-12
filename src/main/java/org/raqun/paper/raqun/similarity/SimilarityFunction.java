package org.raqun.paper.raqun.similarity;

import org.raqun.paper.raqun.data.CandidatePair;
import org.raqun.paper.raqun.data.RMatch;
import org.raqun.paper.raqun.data.RElement;

import java.util.Set;

public interface SimilarityFunction {

    boolean shouldMatch(Set<RMatch> tuples);

    void setNumberOfModels(int numberOfModels);

    double getMatchConfidence(RElement elementA, RElement elementB);

    default double getMatchConfidence(CandidatePair candidatePair) {
        return getMatchConfidence(candidatePair.getFirst(), candidatePair.getSecond());
    }

}
