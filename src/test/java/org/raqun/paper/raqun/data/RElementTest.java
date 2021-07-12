package org.raqun.paper.raqun.data;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RElementTest {
    private String tModelID = "ModelA";
    private String tName = "MyElement";
    private String tUUID = "xfc01";

    private RElement getSimpleRElement() {
        List<String> properties = new ArrayList<>();
        properties.add("prop1");
        return new RElement(tModelID, tName, tUUID, properties);
    }

    @Test
    void testCorrectDataMapping() {
        RElement myElement = getSimpleRElement();

        assert myElement.getModelID().equals(tModelID);
        assert myElement.getName().equals(tName);
        assert myElement.getUUID().equals(tUUID);
        assert myElement.getProperties() != null;
        assert myElement.getProperties() instanceof ArrayList;
        assert myElement.getProperties().size() == 1;
    }

    @Test
    void testCorrectToString() {
        RElement myElement = getSimpleRElement();
        String expectedString = "ModelA:MyElement:(prop1)";

        assert myElement.toString().equals(expectedString);

        myElement.getProperties().add("prop2");
        expectedString = "ModelA:MyElement:(prop1,prop2)";

        assert myElement.toString().equals(expectedString);
    }
}
