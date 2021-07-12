package org.raqun.paper.raqun.tree;

import com.savarese.spatial.GenericPoint;

public class IndexVector extends GenericPoint<Double>
{
	public IndexVector(int dimensions)
	{
		super(dimensions);
		for (int d = 0; d < dimensions; d++)
		{
			this.setCoord(d, 0.0);
		}
	}

	public void setCoord(int d, double v)
	{
		super.setCoord(d, v);
	}
}
