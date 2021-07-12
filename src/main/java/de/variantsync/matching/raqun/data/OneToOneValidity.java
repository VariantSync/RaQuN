package de.variantsync.matching.raqun.data;

import java.util.HashSet;

public class OneToOneValidity implements IValidityConstraint {

    @Override
    public boolean isValid(RMatch match) {
        HashSet<String> modelSet = new HashSet<>();
        for (RElement element : match.getElements()) {
            if (modelSet.contains(element.getModelID())) {
                return false;
            } else {
                modelSet.add(element.getModelID());
            }
        }
        return true;
    }
}
