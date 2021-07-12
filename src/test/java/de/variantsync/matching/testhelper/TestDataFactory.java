package de.variantsync.matching.testhelper;

import de.variantsync.matching.raqun.data.MatchValidityConstraint;
import de.variantsync.matching.raqun.data.RMatch;
import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RModel;
import de.variantsync.matching.raqun.similarity.SimilarityFunction;

import java.security.SecureRandom;
import java.util.*;

public class TestDataFactory {
    private static final RandomStringGenerator randomStringGenerator = new RandomStringGenerator();
    private static final MatchValidityConstraint validityConstraint = MatchValidityConstraint.ONE_TO_ONE;

    public static int getRandomInt(int min, int max) {
        Random random = new SecureRandom();
        int range = max - min + 1;
        return random.nextInt(range) + min;
    }

    public static double getRandomRatio() {
        Random random = new SecureRandom();
        return random.nextDouble();
    }

    public static RElement getSimpleRElement() {
        String modelID = randomStringGenerator.nextString();
        String elementName = randomStringGenerator.nextString();
        String UUID = randomStringGenerator.nextString();

        List<String> properties = new ArrayList<>();
        properties.add("n_" + elementName);

        return new RElement(modelID, elementName, UUID, properties);
    }

    public static List<RElement> getElementList() {
        List<RElement> elements = new ArrayList<>();

        RElement firstElement = getSimpleRElement();
        firstElement.getProperties().add("property1");
        firstElement.getProperties().add("property2");
        elements.add(firstElement);

        RElement secondElement = getSimpleRElement();
        secondElement.getProperties().add("property1");
        secondElement.getProperties().add("property2");
        secondElement.getProperties().add("property3");
        elements.add(secondElement);

        RElement thirdElement = getSimpleRElement();
        thirdElement.getProperties().add("property2");
        thirdElement.getProperties().add("property3");
        elements.add(thirdElement);

        return elements;
    }

    public static List<RModel> getModels(Collection<RElement> elements) {
        ArrayList<RModel> models = new ArrayList<>();
        for (RElement element : elements) {
            RModel model = new RModel(element.getModelID());
            model.addElement(element);
            models.add(model);
        }
        return models;
    }

    public static List<RElement> getElementListWithoutNames() {
        List<RElement> elements = new ArrayList<>();

        RElement firstElement = new RElement("A", "Simple1", "1", new ArrayList<>());
        firstElement.getProperties().add("property1");
        firstElement.getProperties().add("property2");
        elements.add(firstElement);

        RElement secondElement = new RElement("B", "Simple2", "1", new ArrayList<>());
        secondElement.getProperties().add("property1");
        secondElement.getProperties().add("property2");
        secondElement.getProperties().add("property3");
        elements.add(secondElement);

        RElement thirdElement = new RElement("C", "Simple3", "1", new ArrayList<>());
        thirdElement.getProperties().add("property2");
        thirdElement.getProperties().add("property3");
        elements.add(thirdElement);

        return elements;
    }

    public static Collection<RElement> getElementsWithSameProperties(int numberOfElements) {
        Collection<RElement> elements = new HashSet<>();
        int numberOfProperties = getRandomInt(1, 10);
        for (int i = 0; i < numberOfElements; i++) {
            List<String> properties = new ArrayList<>();
            for (int j = 0; j < numberOfProperties; j++) {
                properties.add("prop" + j);
            }
            elements.add(new RElement("model" + i, "i", "0", properties));
        }
        return elements;
    }

    public static RElement getDisplayA () {
        return new RElement("A", "Display", "0", Arrays.asList(
                "n_display", "ex_stationary", "room", "unit"
        ));
    }

    public static RElement getDisplayB () {
        return new RElement("B", "Display", "0", Arrays.asList(
                "n_display", "scanner", "room", "unit"
        ));
    }

    public static RElement getDisplayC () {
        return new RElement("C", "Display", "0", Arrays.asList(
                "n_display", "room", "unit"
        ));
    }

    public static RElement getMobileA () {
        return new RElement("A", "Mobile", "1", Arrays.asList(
                "n_mobile", "ex_equipment", "generalStorage"
        ));
    }

    public static RElement getMobileB () {
        return new RElement("B", "Mobile", "1", Arrays.asList(
                "n_mobile", "ex_equipment", "generalStorage"
        ));
    }

    public static RElement getIDCardA () {
        return new RElement("A", "IDCard", "2", Arrays.asList(
                "n_iDCard", "medicalTeam", "displayScanner"
        ));
    }

    public static RElement getCalendarB () {
        return new RElement("B", "Display", "3", Arrays.asList(
                "n_calendar", "medicalTeam", "database"
        ));
    }

    public static RMatch getDisplayTuple(SimilarityFunction similarityFunction) {
        RElement displayA = getDisplayA();
        RElement displayB = getDisplayB();
        RElement displayC = getDisplayC();
        // Expected Weight: 27/45
        return new RMatch(similarityFunction, validityConstraint, displayA, displayB, displayC);
    }

    public static RMatch getMobileTuple(SimilarityFunction similarityFunction) {
        RElement mobileA = getMobileA();
        RElement mobileB = getMobileB();
        // Expected Weight: 12/27
        return new RMatch(similarityFunction, validityConstraint, mobileA, mobileB);
    }

    public static RMatch getMixedTuple(SimilarityFunction similarityFunction) {
        RElement idCardA = getIDCardA();
        RElement calendarB = getCalendarB();
        // Expected Weight: 4/45
        return new RMatch(similarityFunction, validityConstraint, idCardA, calendarB);
    }

    public static Set<RMatch> getExampleMatching(SimilarityFunction similarityFunction) {
        Set<RMatch> matching = new HashSet<>();
        matching.add(getDisplayTuple(similarityFunction));
        matching.add(getMobileTuple(similarityFunction));
        matching.add(getMixedTuple(similarityFunction));
        return matching;
    }
}