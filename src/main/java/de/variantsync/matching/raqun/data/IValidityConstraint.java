package de.variantsync.matching.raqun.data;


public interface IValidityConstraint {
    /**
     * Determine whether the given match is valid according to the validity constraint defined by this class
     * @param match for which the validity is checked
     * @return true if the match is valid, false otherwise
     */
    boolean isValid(RMatch match);
}