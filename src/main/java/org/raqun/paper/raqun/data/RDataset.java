package org.raqun.paper.raqun.data;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class RDataset {
    private final String name;
    private Map<String, RModel> models;

    public RDataset(String name) {
        this.name = name;
    }

    public ArrayList<RModel> getModels() {
        return new ArrayList<>(this.models.values());
    }

    public String getName() {
        return this.name;
    }

    public int getNumberOfModels() {
        return models.size();
    }

    public void loadFileContent(String pathToFile) {
        Set<String> contentLines = new HashSet<>();

        try (Stream<String> lines = Files.lines(Paths.get(pathToFile))) {
            lines.forEach(contentLines::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.models = parseFileContentToModels(contentLines);
    }

    private Map<String, RModel> parseFileContentToModels(Set<String> content) {
        Map<String, RModel> modelsInFile = new HashMap<>();
        for (String modelLine : content) {
            String inputSeparator = ",";
            String[] parts = modelLine.split(inputSeparator);

            if (parts.length < 3) {
                throw new RuntimeException("Each line in the file that describes the elements of a model must at least" +
                        " contain 'ModelID, ElementID, ElementName', otherwise it cannot be parsed!");
            }

            int partIndex = 0;
            // Always 1st
            String modelID = parts[partIndex++].trim();
            // Always 2nd
            String elementID = parts[partIndex++].trim();
            // Always 3rd
            String elementName = parts[partIndex++].trim();

            List<String> properties = new ArrayList<>();
            if (parts.length > 3) {
                String propertiesString = parts[partIndex];
                String propertySeparator = ";";
                if (propertiesString.contains(propertySeparator)) {
                    String[] tempProperties = propertiesString.split(propertySeparator);
                    for (String property : tempProperties) {
                        properties.add(property.trim());
                    }
                } else {
                    properties.add(propertiesString.trim());
                }
            }

            // Has the model of this element already been initialized?
            RModel currentModel;
            if (modelsInFile.containsKey(modelID)) {
                currentModel = modelsInFile.get(modelID);
            } else {
                currentModel = new RModel(modelID);
                modelsInFile.put(modelID, currentModel);
            }

            // Create the instance of the element and add it to the current model
            RElement element = new RElement(currentModel.getModelID(), elementName, elementID, properties);
            currentModel.addElement(element);
        }

        return modelsInFile;
    }

}
