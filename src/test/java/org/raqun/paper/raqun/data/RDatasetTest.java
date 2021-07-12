package org.raqun.paper.raqun.data;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class RDatasetTest {
    String pathToSimpleDataset = Paths.get("src", "test", "resources", "datasets", "simple_dataset.txt").toString();;

    @Test
    void simpleDatasetLoadsCorrectly() {
        RDataset simpleDataset = new RDataset("SimpleDataset");
        simpleDataset.loadFileContent(pathToSimpleDataset);

        assert simpleDataset.getName().equals("SimpleDataset");
        assert simpleDataset.getNumberOfModels() == 3;

        List<RModel> models = simpleDataset.getModels();
        assert models.size() == 3;
        RModel modelA = models.get(0);
        RModel modelB = models.get(1);
        RModel modelC = models.get(2);

        assert modelA.getModelID().equals("A");
        assert modelB.getModelID().equals("B");
        assert modelC.getModelID().equals("C");

        Set<RElement> elementsOfA = modelA.getElements();
        Set<RElement> elementsOfB = modelB.getElements();
        Set<RElement> elementsOfC = modelC.getElements();

        assert elementsOfA.size() == 2;
        assert elementsOfB.size() == 3;
        assert elementsOfC.size() == 1;

        for (RElement element : elementsOfA) {
            assert element.getModelID().equals("A");
            if (element.getName().equals("elementA")) {
                assert element.getUUID().equals("1");
                List<String> properties = element.getProperties();
                assert properties.size() == 1;
                assert properties.get(0).equals("prop1");
            } else if (element.getName().equals("elementB")) {
                assert element.getUUID().equals("2");
                List<String> properties = element.getProperties();
                assert properties.size() == 2;
                assert properties.get(0).equals("prop3");
                assert properties.get(1).equals("prop4");
            } else {
                throw new AssertionError("Invalid element laoded!");
            }
        }

        for (RElement element : elementsOfB) {
            assert element.getModelID().equals("B");
            if (element.getName().equals("elementD")) {
                assert element.getUUID().equals("4");
                assert element.getProperties() != null;
                assert element.getProperties().size() == 0;
            }
        }
    }
}
