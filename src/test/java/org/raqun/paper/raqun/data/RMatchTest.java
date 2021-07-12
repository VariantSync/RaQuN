package org.raqun.paper.raqun.data;

import org.raqun.paper.raqun.similarity.WeightMetric;
import org.raqun.paper.raqun.similarity.SimilarityFunction;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.raqun.paper.testhelper.TestDataFactory.getSimpleRElement;

public class RMatchTest {
    private SimilarityFunction weightMetric = new WeightMetric();
    private static final MatchValidityConstraint validityConstraint = MatchValidityConstraint.ONE_TO_ONE;

    @Test
    public void isValidFindsDuplicateModelsInTuple() {
        RElement firstElement = getSimpleRElement();
        RElement secondElement = getSimpleRElement();
        RElement invalidElement = new RElement(
                firstElement.getModelID(),
                "Invalid",
                "22",
                firstElement.getProperties());

        RMatch validTuple = new RMatch(weightMetric, validityConstraint, firstElement);
        assert validTuple.getElements().size() == 1;
        assert validTuple.isValid();

        validTuple = new RMatch(weightMetric, validityConstraint, firstElement, secondElement);
        assert validTuple.getElements().size() == 2;
        assert validTuple.isValid();

        // Now check that invalid tuples can be found
        RMatch invalidTuple = new RMatch(weightMetric, validityConstraint, invalidElement, firstElement, secondElement);
        assert invalidTuple.getElements().size() == 3;
        assert !invalidTuple.isValid();
    }

    @Test
    public void mergeOneElementTuple() {
        RElement firstElement = new RElement("modelA", "ABC", "0", new ArrayList<>());
        firstElement.getProperties().add("n_ABC");
        firstElement.getProperties().add("property1");
        firstElement.getProperties().add("property2");
        RMatch firstTuple = new RMatch(weightMetric, validityConstraint, firstElement);

        RElement secondElement = new RElement("modelB", "CBE", "0", new ArrayList<>());
        secondElement.getProperties().add("n_CBE");
        secondElement.getProperties().add("property2");
        secondElement.getProperties().add("property3");
        RMatch secondTuple = new RMatch(weightMetric, validityConstraint, secondElement);

        Set<RMatch> tuples = new HashSet<>();
        tuples.add(firstTuple);
        tuples.add(secondTuple);

        RMatch mergedTuple = RMatch.getMergedTuple(tuples);
        assert mergedTuple.getElements().size() == 2;
        assert mergedTuple.isValid();
    }

    @Test
    public void mergeMultiElementTuple() {
        RElement firstElement = new RElement("modelA", "ABC", "0", new ArrayList<>());
        firstElement.getProperties().add("n_ABC");
        firstElement.getProperties().add("property1");
        firstElement.getProperties().add("property2");
        RElement secondElement = new RElement("modelB", "CBE", "0", new ArrayList<>());
        secondElement.getProperties().add("n_CBE");
        secondElement.getProperties().add("property2");
        secondElement.getProperties().add("property3");
        RMatch firstTuple = new RMatch(weightMetric, validityConstraint, firstElement, secondElement);

        RElement thirdElement = new RElement("modelC", "ABC", "0", new ArrayList<>());
        thirdElement.getProperties().add("n_ABC");
        thirdElement.getProperties().add("property2");
        thirdElement.getProperties().add("property3");
        thirdElement.getProperties().add("property4");
        RElement fourthElement = new RElement("modelD", "CBE", "0", new ArrayList<>());
        fourthElement.getProperties().add("n_CBE");
        fourthElement.getProperties().add("property2");
        fourthElement.getProperties().add("property1");
        RElement fifthElement = new RElement("modelE", "DFG", "0", new ArrayList<>());
        fifthElement.getProperties().add("n_DFG");
        fifthElement.getProperties().add("property2");
        fifthElement.getProperties().add("property3");
        fifthElement.getProperties().add("property4");
        RMatch secondTuple = new RMatch(weightMetric, validityConstraint, thirdElement, fourthElement, fifthElement);

        RElement sixthElement = new RElement("modelF", "DFG", "0", new ArrayList<>());
        sixthElement.getProperties().add("n_DFG");
        sixthElement.getProperties().add("property2");
        sixthElement.getProperties().add("property3");
        RMatch thirdTuple = new RMatch(weightMetric, validityConstraint, sixthElement);

        Set<RMatch> tuples = new HashSet<>();
        tuples.add(firstTuple);
        tuples.add(secondTuple);
        tuples.add(thirdTuple);

        RMatch mergedTuple = RMatch.getMergedTuple(tuples);
        assert mergedTuple.getElements().size() == 6;
        assert mergedTuple.isValid();
        assert mergedTuple.contains(secondElement);
        assert mergedTuple.contains(sixthElement);
    }
}