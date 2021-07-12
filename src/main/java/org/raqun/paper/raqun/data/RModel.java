package org.raqun.paper.raqun.data;

import java.util.HashSet;
import java.util.Set;

public class RModel {
    private final String modelID;
    private final Set<RElement> elements;

    public RModel(String modelID) {
        this.modelID = modelID;
        this.elements = new HashSet<>();
    }

    public void addElement(RElement element) {
        if (!element.getModelID().equals(this.modelID)) {
            throw new IllegalArgumentException("A model can only contain elements with the same model ID!");
        }
        this.elements.add(element);
    }

    public Set<RElement> getElements() {
        return this.elements;
    }

    public String getModelID() {
        return modelID;
    }

    @Override
    public String toString() {
        return modelID;
    }
}
