package de.variantsync.matching.raqun.data;

import de.variantsync.matching.testhelper.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.util.*;

public class RMatchTest {
    private static final IValidityConstraint validityConstraint = new OneToOneValidity();

    @Test
    public void isValidFindsDuplicateModelsInTuple() {
        RElement firstElement = TestDataFactory.getSimpleRElement();
        RElement secondElement = TestDataFactory.getSimpleRElement();
        RElement invalidElement = new RElement(
                firstElement.getModelID(),
                "Invalid",
                "22",
                firstElement.getProperties());

        RMatch validTuple = new RMatch(firstElement);
        assert validTuple.getElements().size() == 1;
        assert validityConstraint.isValid(validTuple);

        validTuple = new RMatch(firstElement, secondElement);
        assert validTuple.getElements().size() == 2;
        assert validityConstraint.isValid(validTuple);

        // Now check that invalid tuples can be found
        RMatch invalidTuple = new RMatch(invalidElement, firstElement, secondElement);
        assert invalidTuple.getElements().size() == 3;
        assert !validityConstraint.isValid(invalidTuple);
    }

    @Test
    public void mergeOneElementTuple() {
        RElement firstElement = new RElement("modelA", "ABC", "0", new ArrayList<>());
        firstElement.getProperties().add("n_ABC");
        firstElement.getProperties().add("property1");
        firstElement.getProperties().add("property2");
        RMatch firstTuple = new RMatch(firstElement);

        RElement secondElement = new RElement("modelB", "CBE", "0", new ArrayList<>());
        secondElement.getProperties().add("n_CBE");
        secondElement.getProperties().add("property2");
        secondElement.getProperties().add("property3");
        RMatch secondTuple = new RMatch(secondElement);

        Set<RMatch> tuples = new HashSet<>();
        tuples.add(firstTuple);
        tuples.add(secondTuple);

        RMatch mergedTuple = RMatch.getMergedTuple(tuples);
        assert mergedTuple.getElements().size() == 2;
        assert validityConstraint.isValid(mergedTuple);
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
        RMatch firstTuple = new RMatch(firstElement, secondElement);

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
        RMatch secondTuple = new RMatch(thirdElement, fourthElement, fifthElement);

        RElement sixthElement = new RElement("modelF", "DFG", "0", new ArrayList<>());
        sixthElement.getProperties().add("n_DFG");
        sixthElement.getProperties().add("property2");
        sixthElement.getProperties().add("property3");
        RMatch thirdTuple = new RMatch(sixthElement);

        Set<RMatch> tuples = new HashSet<>();
        tuples.add(firstTuple);
        tuples.add(secondTuple);
        tuples.add(thirdTuple);

        RMatch mergedTuple = RMatch.getMergedTuple(tuples);
        assert mergedTuple.getElements().size() == 6;
        assert validityConstraint.isValid(mergedTuple);
        assert mergedTuple.contains(secondElement);
        assert mergedTuple.contains(sixthElement);
    }
}