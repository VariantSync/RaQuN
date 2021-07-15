package de.variantsync.matching.raqun.tree;

import de.variantsync.matching.raqun.data.RModel;
import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.vectorization.CharacterBasedVectorization;
import de.variantsync.matching.raqun.vectorization.IVectorization;
import de.variantsync.matching.raqun.vectorization.PropertyBasedVectorization;
import org.junit.jupiter.api.Test;

import java.util.*;

public class KDTreeTest {

    @Test
    public void initializationWithTwoModelsAndLargeVectorization() {
        RModel modelA = getSimpleModel("A");
        RModel modelB = getSimpleModel("B");

        List<RModel> modelList = new ArrayList<>();
        modelList.add(modelA);
        modelList.add(modelB);

        Set<RElement> elementSet = new HashSet<>();
        modelList.forEach((m) -> elementSet.addAll(m.getElements()));

        IVectorization vectorization = new PropertyBasedVectorization();
        vectorization.initialize(modelList);
        KDTree tree = new KDTree(vectorization);
        elementSet.forEach(tree::add);
        assert tree.getNumberOfElementsInTree() == 2;
        assert tree.getElementsInTree().size() == 2;
        assert !(tree.getVectorization() instanceof CharacterBasedVectorization);
    }

    @Test
    public void onlyElementsAtSamePointAreFoundWithKeq1() {
        // k == 1
        KDTree tree = initializeTree();
        assert tree.getNumberOfElementsInTree() == 5;
        List<RElement> elements = tree.getElementsInTree();

        RElement queryElement = findElement(elements, "A", "ele1");

        List<TreeNeighbor> treeNeighbors = tree.collectNearestNeighbors(queryElement, 1);
        assert treeNeighbors.size() == 1;
        for (TreeNeighbor result : treeNeighbors) {
            assert Double.compare(result.getDistance(), 0.0d) == 0;

            RElement element = result.getElement();
            assert element.getUUID().equals("0");
            assert element.getName().equals("ele1");
        }
    }

    private RElement findElement(List<RElement> elements, String modelID, String elementName) {
        for (RElement element : elements) {
            if (element.getModelID().equals(modelID)) {
                if (element.getName().equals(elementName)) {
                    return element;
                }
            }
        }
        return null;
    }

    private RModel getSimpleModel(String modelID) {
        RModel model = new RModel(modelID);
        List<String> properties = new ArrayList<>();
        properties.add("n_cat");
        properties.add("property1");
        RElement element = new RElement(modelID, "0", "Cat", properties);
        model.addElement(element);
        return model;
    }

    private static List<RModel> generateModels() {
        RModel modelA = new RModel("A");
        RElement element1A = new RElement("A", "0", "ele1",
                Arrays.asList("n_ele1", "prop1", "prop2", "zzz"));
        RElement element2A = new RElement("A", "1", "ele2",
                Arrays.asList("n_ele2", "prop3", "prop4", "zzz"));
        modelA.addElement(element1A);
        modelA.addElement(element2A);

        RModel modelB = new RModel("B");
        RElement element1B = new RElement("B", "0", "ele1",
                Arrays.asList("n_ele1", "prop1", "prop2", "zzz"));
        RElement element2B = new RElement("B", "1", "ele2",
                Arrays.asList("n_ele2", "prop3", "xyz_abc", "zzz"));
        RElement element3B = new RElement("B", "2", "ele3",
                Arrays.asList("n_ele3", "prop1", "prop5", "prop6"));
        modelB.addElement(element1B);
        modelB.addElement(element2B);
        modelB.addElement(element3B);

        return Arrays.asList(modelA, modelB);
    }

    public static KDTree initializeTree() {
        List<RModel> models = generateModels();
        IVectorization vectorization = new PropertyBasedVectorization();
        vectorization.initialize(models);
        KDTree tree = new KDTree(vectorization);
        models.stream().flatMap((m) -> m.getElements().stream()).forEach(tree::add);
        return tree;
    }
}