package eu.liveandgov.wp1.sensor_collector.pps.api;

import java.io.Serializable;

/**
 * Created by lukashaertel on 03.02.14.
 */
public class Proximity implements Serializable
{
	private static final long serialVersionUID = 4852472745702439402L;

	private ProximityType proximityType;

	private String objectIdentity;

	public Proximity(ProximityType proximityType, String objectIdentity)
	{
		this.proximityType = proximityType;
		this.objectIdentity = objectIdentity;
	}

	public ProximityType getProximityType()
	{
		return proximityType;
	}

	public String getObjectIdentity()
	{
		return objectIdentity;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Proximity proximity = (Proximity) o;

		if (objectIdentity != null ? !objectIdentity.equals(proximity.objectIdentity) : proximity.objectIdentity != null) return false;
		if (proximityType != proximity.proximityType) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = proximityType != null ? proximityType.hashCode() : 0;
		result = 31 * result + (objectIdentity != null ? objectIdentity.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "Proximity{" + "proximityType=" + proximityType + ", objectIdentity='" + objectIdentity + '\'' + '}';
	}
}
