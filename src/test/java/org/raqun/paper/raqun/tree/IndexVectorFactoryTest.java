package org.raqun.paper.raqun.tree;

import org.raqun.paper.raqun.data.RElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.raqun.paper.testhelper.TestDataFactory.getElementList;
import static org.raqun.paper.testhelper.TestDataFactory.getSimpleRElement;

public class IndexVectorFactoryTest {
    @Test
    public void initializationWithNullElementsInvalid() {
        Assertions.assertThrows(NullPointerException.class, () -> new IndexVectorFactory(null));
    }

    @Test
    public void initializationWithZeroElementsInvalid() {
        List<RElement> elements = new ArrayList<>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> new IndexVectorFactory(elements));
    }

    @Test
    public void initializationWithExactlyOneElement() {
        List<RElement> elements = new ArrayList<>();
        RElement simpleElement = getSimpleRElement();
        simpleElement.getProperties().add("property1");
        simpleElement.getProperties().add("property2");

        elements.add(simpleElement);
        IndexVectorFactory factory = new IndexVectorFactory(elements);
        Map<String, Integer> childrenNamesDimension = factory.getChildrenNamesDimension();
        assert childrenNamesDimension.size() == 3;
        assert childrenNamesDimension.containsKey("n_" + simpleElement.getName());
        assert childrenNamesDimension.containsKey("property1");
        assert childrenNamesDimension.containsKey("property2");
    }

    @Test
    public void initializationWithSeveralElements() {
        List<RElement> elements = getElementList();
        IndexVectorFactory factory = new IndexVectorFactory(elements);
        Map<String, Integer> childrenNamesDimension = factory.getChildrenNamesDimension();
        assert childrenNamesDimension.size() == 6;
        assert factory.getNumberOfDimension() == 6;
    }

    @Test
    public void generationOfVectors() {
        List<RElement> elements = getElementList();
        // Add one element twice to check for this case as well
        elements.add(elements.get(0));
        IndexVectorFactory factory = new IndexVectorFactory(elements);
        Map<String, Integer> childrenNamesDimension = factory.getChildrenNamesDimension();

        for (RElement element : elements) {
            IndexVector vector = factory.vectorFor(element);
            assert vector.getDimensions() == 6;

            Set<String> propertiesNotInElement = new HashSet<>(childrenNamesDimension.keySet());
            for (String property : element.getProperties()) {
                assert childrenNamesDimension.containsKey(property);
                int propertyDimension = childrenNamesDimension.get(property);
                // Make sure that the dimension of the property is set to 1
                assert Double.compare(vector.getCoord(propertyDimension), 1) == 0;
                propertiesNotInElement.remove(property);
            }
            // Make sure that the dimensions of properties not in the element are set to 0
            for (String property : propertiesNotInElement) {
                int propertyDimension = childrenNamesDimension.get(property);
                // Make sure that the dimension of the property is set to 1
                assert Double.compare(vector.getCoord(propertyDimension), 0) == 0;
            }
        }
    }

    @Test
    public void elementWithoutPropertiesIsInvalid() {
        List<RElement> elements = getElementList();
        // Remove all properties of one element to check whether this case is recognized as invalid
        elements.get(0).getProperties().clear();
        IndexVectorFactory factory = new IndexVectorFactory(elements);
        Assertions.assertThrows(IllegalArgumentException.class, () -> factory.vectorFor(elements.get(0)));
    }
}
