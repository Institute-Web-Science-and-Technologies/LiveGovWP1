package eu.liveandgov.wp1.pps.api.gi;

import java.io.Serializable;

/**
 * <p>Two-dimensional, serializable and comparable index</p>
 */
public class Field implements Comparable<Field>, Serializable
{
	private static final long serialVersionUID = -6051494591313716015L;

	/**
	 * Index in the horizontal dimension
	 */
	private long x;

	/**
	 * Index in the vertical dimension
	 */
	private long y;

	public Field(long x, long y)
	{
		this.x = x;
		this.y = y;
	}

    /**
     * Index in the horizontal dimension
     */
	public long getX()
	{
		return x;
	}

    /**
     * Index in the vertical dimension
     */
	public long getY()
	{
		return y;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (x ^ (x >>> 32));
		result = prime * result + (int) (y ^ (y >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Field)) return false;

		Field other = (Field) obj;
		if (x != other.x) return false;
		if (y != other.y) return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}

	@Override
	public int compareTo(Field o)
	{
		final int r = Long.signum(y - o.y);
		if (r != 0) return r;

		return Long.signum(x - o.x);
	}

}
