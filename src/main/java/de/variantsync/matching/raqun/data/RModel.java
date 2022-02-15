package de.variantsync.matching.raqun.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Representation of a element-property model. Each model is expected to have a unique id and a set of elements.
 */
public class RModel {
    private final String modelID;
    private final Set<RElement> elements;

    /**
     * Initialize a new model with the given id.
     */
    public RModel(final String modelID) {
        this.modelID = modelID;
        this.elements = new HashSet<>();
    }

    /**
     *
     * @param element that is added to the model. The element's modelID has to be the ID of this model.
     */
    public void addElement(final RElement element) {
        if (!element.getModelID().equals(this.modelID)) {
            throw new IllegalArgumentException("A model can only contain elements with the same model ID!");
        }
        this.elements.add(element);
    }

    /**
     *
     * @return elements in this model
     */
    public Set<RElement> getElements() {
        return this.elements;
    }

    /**
     *
     * @return id of this model
     */
    public String getModelID() {
        return modelID;
    }

    @Override
    public String toString() {
        return modelID;
    }
}