package de.variantsync.matching.raqun.data;

import java.util.List;

/**
 * Representation of a model element
 */
public class RElement {
    // ID of the model containing this element
    private final String modelID;
    // The name of this element, must be included additionally in the properties if it should be considered during matching
    private final String name;
    // The UUID of this element used in the evaluation.
    private final String UUID;
    // The list of properties of this element
    private final List<String> properties;

    public RElement(String modelID, String UUID, String name, List<String> properties) {
        this.modelID = modelID;
        this.name = name;
        this.UUID = UUID;
        this.properties = properties;
    }

    /**
     *
     * @return name of this element
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return id of the model that contains this element
     */
    public String getModelID() {
        return modelID;
    }

    /**
     *
     * @return UUID of this element
     */
    public String getUUID() {
        return UUID;
    }

    /**
     *
     * @return List of properties of this element
     */
    public List<String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(modelID + ":" + name + ":(");
        for (String property : properties) {
            sb.append(property);
            sb.append(",");
        }
        // Delete the last comma
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        return sb.toString();
    }
}