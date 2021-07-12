package de.variantsync.matching.raqun.validity;

import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RMatch;

import java.util.HashSet;

/**
 * ValidityConstraint for the validity of a match. This constraint expects that a valid match must only contain at most
 * one element of a specific model.
 */
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
