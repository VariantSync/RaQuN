package de.variantsync.matching.raqun.tree;

import com.savarese.spatial.GenericPoint;

/**
 * The vector representation of an element.
 */
public class RVector extends GenericPoint<Double>
{
	/**
	 * Initialize a vector with the given number of dimensions.
	 * @param dimensions number of dimensions
	 */
	public RVector(int dimensions)
	{
		super(dimensions);
		for (int d = 0; d < dimensions; d++)
		{
			this.setCoord(d, 0.0);
		}
	}

	/**
	 * Set the value of a dimension
	 * @param d dimension index
	 * @param v value
	 */
	public void setCoord(int d, double v)
	{
		super.setCoord(d, v);
	}
}