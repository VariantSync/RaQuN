package de.variantsync.matching.raqun.tree;

import de.variantsync.matching.raqun.data.RElement;

/**
 * Representation of a neighboring element in a k-d-tree. A TreeNeighbor comprises the neighboring element and the distance
 * to it.
 */
public class TreeNeighbor {
	private final RElement element;
	private final double distance;

	/**
	 * Initialize a new neighboring element
	 * @param element the neighboring element
	 * @param distance the distance to it
	 */
	public TreeNeighbor(RElement element, double distance) {
		this.element = element;
		this.distance = distance;
	}

	/**
	 *
	 * @return The distance to the neighbor
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 *
	 * @return The neighboring element
	 */
	public RElement getElement() {
		return element;
	}
}