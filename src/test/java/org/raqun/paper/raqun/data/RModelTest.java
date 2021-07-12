package org.raqun.paper.raqun.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RModelTest {
    private int elementCounter = 0;

    private RElement getSimpleRElement() {
        elementCounter++;
        List<String> properties = new ArrayList<>();
        properties.add("prop1" + elementCounter);
        String tModelID = "modelA";
        String tName = "MyElement";
        String tUUID = "xfc123";
        return new RElement(tModelID, tName + elementCounter, tUUID + elementCounter, properties);
    }

    @Test
    void fieldsHandledCorrectly(){
        RElement firstElement = getSimpleRElement();
        RElement secondElement = getSimpleRElement();

        RModel model = new RModel("modelA");

        assert model.getModelID().equals("modelA");
        assert model.toString().equals("modelA");
        assert model.getElements() != null;
        assert model.getElements().size() == 0;

        model.addElement(firstElement);
        assert model.getElements().size() == 1;

        model.addElement(secondElement);
        assert model.getElements().size() == 2;
        assert model.getElements().contains(firstElement);
        assert model.getElements().contains(secondElement);
    }

    @Test
    public void invalidModelIDOfAddedElement() {
        RModel model = new RModel("A");
        RElement elementOne = new RElement("B", "Cat", "0", new ArrayList<>());
        Assertions.assertThrows(IllegalArgumentException.class, () -> model.addElement(elementOne));
    }
}
