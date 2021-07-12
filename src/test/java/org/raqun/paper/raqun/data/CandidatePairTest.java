package org.raqun.paper.raqun.data;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class CandidatePairTest {
    private String tModelID = "model";
    private String tName = "MyElement";
    private String tUUID = "xfc123";
    private int elementCounter = 0;

    private RElement getSimpleRElement() {
        elementCounter++;
        List<String> properties = new ArrayList<>();
        properties.add("prop1" + elementCounter);
        return new RElement(tModelID + elementCounter, tName + elementCounter, tUUID + elementCounter, properties);
    }

    @Test
    void candidateAssignmentIsCorrect() {
        RElement firstElement = getSimpleRElement();
        RElement secondElement = getSimpleRElement();

        CandidatePair pair = new CandidatePair(firstElement, secondElement, 33);
        assert pair.getFirst() == firstElement;
        assert pair.getSecond() == secondElement;
        assert pair.getDistanceInTree() == 33;

        CandidatePair pair2 = new CandidatePair(secondElement, firstElement, 20);
        assert pair2.getFirst() == secondElement;
        assert pair2.getSecond() == firstElement;
        assert pair2.getDistanceInTree() == 20;
    }

    @Test
    void pairsWithSameElementsAreEqual() {
        RElement firstElement = getSimpleRElement();
        RElement secondElement = getSimpleRElement();
        assert !firstElement.equals(secondElement);
        assert firstElement.hashCode() != secondElement.hashCode();

        CandidatePair firstPair = new CandidatePair(firstElement, secondElement, 33);
        CandidatePair secondPair = new CandidatePair(secondElement, firstElement, 33);

        assert firstPair.equals(secondPair);
        assert firstPair.hashCode() == secondPair.hashCode();
    }

    @Test
    void matchConfidenceSetCorrectly() {
        double epsilon = 0.000_000_000_001;
        RElement firstElement = getSimpleRElement();
        RElement secondElement = getSimpleRElement();
        CandidatePair pair = new CandidatePair(firstElement, secondElement, -1);

        assert Math.abs(pair.getMatchConfidence() - 0d) < epsilon;
        pair.setMatchConfidence(0.76d);
        assert Math.abs(pair.getMatchConfidence() - 0.76d) < epsilon;
    }

}
