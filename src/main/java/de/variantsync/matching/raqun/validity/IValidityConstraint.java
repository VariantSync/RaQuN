package de.variantsync.matching.raqun.validity;

import de.variantsync.matching.raqun.data.RMatch;

/**
 * A ValidityConstraints determines whether a given match of elements is valid.
 */
public interface IValidityConstraint {
    /**
     * Determine whether the given match is valid according to the validity constraint defined by this class
     * @param match for which the validity is checked
     * @return true if the match is valid, false otherwise
     */
    boolean isValid(RMatch match);
}