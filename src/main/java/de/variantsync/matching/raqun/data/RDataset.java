package de.variantsync.matching.raqun.data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * Representation of a dataset containing multiple models that should be matched. Provides methods for loading a Dataset
 * from a csv-file.
 */
public class RDataset {
    private final String name;
    private ArrayList<RModel> models;

    /**
     * Initialize a new dataset with the given name
     * @param name of the dataset
     */
    public RDataset(final String name) {
        this.name = name;
    }

    /**
     *
     * @return The models of this dataset
     */
    public ArrayList<RModel> getModels() {
        return this.models;
    }

    /**
     *
     * @return name of this dataset
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return number of models in this dataset
     */
    public int getNumberOfModels() {
        return models.size();
    }

    /**
     * Load and parse the content of the csv file that can be found under the given path
     * @param pathToFile The path to the file that is to be loaded
     */
    public void loadFileContent(final Path pathToFile) {
        final Set<String> contentLines = new HashSet<>();

        try (final Stream<String> lines = Files.lines(pathToFile)) {
            lines.forEach(contentLines::add);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        this.models = new ArrayList<>(parseFileContentToModels(contentLines));
    }

    /**
     * Parse the content of the csv file
     * @param content The lines in the file
     * @return Map of model-ids to models
     */
    private Collection<RModel> parseFileContentToModels(final Set<String> content) {
        final Map<String, RModel> modelsInFile = new HashMap<>();
        for (final String modelLine : content) {
            final String inputSeparator = ",";
            final String[] parts = modelLine.split(inputSeparator);

            if (parts.length < 3) {
                throw new RuntimeException("Each line in the file that describes the elements of a model must at least" +
                        " contain 'ModelID, ElementID, ElementName', otherwise it cannot be parsed!");
            }

            int partIndex = 0;
            // Always 1st
            final String modelID = parts[partIndex++].trim();
            // Always 2nd
            final String elementID = parts[partIndex++].trim();
            // Always 3rd
            final String elementName = parts[partIndex++].trim();

            final List<String> properties = new ArrayList<>();
            if (parts.length > 3) {
                final String propertiesString = parts[partIndex];
                final String propertySeparator = ";";
                if (propertiesString.contains(propertySeparator)) {
                    final String[] tempProperties = propertiesString.split(propertySeparator);
                    for (final String property : tempProperties) {
                        properties.add(property.trim());
                    }
                } else {
                    properties.add(propertiesString.trim());
                }
            }

            // Has the model of this element already been initialized?
            final RModel currentModel;
            if (modelsInFile.containsKey(modelID)) {
                currentModel = modelsInFile.get(modelID);
            } else {
                currentModel = new RModel(modelID);
                modelsInFile.put(modelID, currentModel);
            }

            // Create the instance of the element and add it to the current model
            final RElement element = new RElement(currentModel.getModelID(), elementID, elementName, properties);
            currentModel.addElement(element);
        }

        return modelsInFile.values();
    }

}