package de.variantsync.matching.testhelper;

import de.variantsync.matching.raqun.data.RMatch;
import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RModel;

import java.security.SecureRandom;
import java.util.*;

public class TestDataFactory {
    private static final RandomStringGenerator randomStringGenerator = new RandomStringGenerator();

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

        return new RElement(modelID, UUID, elementName, properties);
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

        RElement firstElement = new RElement("A", "1", "Simple1", new ArrayList<>());
        firstElement.getProperties().add("property1");
        firstElement.getProperties().add("property2");
        elements.add(firstElement);

        RElement secondElement = new RElement("B", "1", "Simple2", new ArrayList<>());
        secondElement.getProperties().add("property1");
        secondElement.getProperties().add("property2");
        secondElement.getProperties().add("property3");
        elements.add(secondElement);

        RElement thirdElement = new RElement("C", "1", "Simple3", new ArrayList<>());
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
            elements.add(new RElement("model" + i, "0", "i", properties));
        }
        return elements;
    }

    public static RElement getDisplayA () {
        return new RElement("A", "0", "Display", Arrays.asList(
                "n_display", "ex_stationary", "room", "unit"
        ));
    }

    public static RElement getDisplayB () {
        return new RElement("B", "0", "Display", Arrays.asList(
                "n_display", "scanner", "room", "unit"
        ));
    }

    public static RElement getDisplayC () {
        return new RElement("C", "0", "Display", Arrays.asList(
                "n_display", "room", "unit"
        ));
    }

    public static RElement getMobileA () {
        return new RElement("A", "1", "Mobile", Arrays.asList(
                "n_mobile", "ex_equipment", "generalStorage"
        ));
    }

    public static RElement getMobileB () {
        return new RElement("B", "1", "Mobile", Arrays.asList(
                "n_mobile", "ex_equipment", "generalStorage"
        ));
    }

    public static RElement getIDCardA () {
        return new RElement("A", "2", "IDCard", Arrays.asList(
                "n_iDCard", "medicalTeam", "displayScanner"
        ));
    }

    public static RElement getCalendarB () {
        return new RElement("B", "3", "Display", Arrays.asList(
                "n_calendar", "medicalTeam", "database"
        ));
    }

    public static RMatch getDisplayTuple() {
        RElement displayA = getDisplayA();
        RElement displayB = getDisplayB();
        RElement displayC = getDisplayC();
        // Expected Weight: 27/45
        return new RMatch(displayA, displayB, displayC);
    }

    public static RMatch getMobileTuple() {
        RElement mobileA = getMobileA();
        RElement mobileB = getMobileB();
        // Expected Weight: 12/27
        return new RMatch(mobileA, mobileB);
    }

    public static RMatch getMixedTuple() {
        RElement idCardA = getIDCardA();
        RElement calendarB = getCalendarB();
        // Expected Weight: 4/45
        return new RMatch(idCardA, calendarB);
    }

    public static Set<RMatch> getExampleMatching() {
        Set<RMatch> matching = new HashSet<>();
        matching.add(getDisplayTuple());
        matching.add(getMobileTuple());
        matching.add(getMixedTuple());
        return matching;
    }
}