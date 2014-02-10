package eu.liveandgov.wp1.pps.api;

import eu.liveandgov.wp1.data.Tuple;

import java.util.LinkedList;
import java.util.List;

/**
 * Aggregating ProximityType Service
 * 
 * @author lukashaertel
 */
public class AggregatingPS implements ProximityService
{
	private final List<ProximityService> proximityServices;

	public AggregatingPS()
	{
		this.proximityServices = new LinkedList<ProximityService>();
	}

	public List<ProximityService> getProximityServices()
	{
		return proximityServices;
	}

	@Override
	public Tuple<Boolean, String> calculate(double lat, double lon)
	{
        Tuple<Boolean, String> result = null;

		for (ProximityService s : proximityServices)
		{
			result = s.calculate(lat, lon);

			if (result == null || !result.left)
			{
				continue;
			}

			break;
		}

		return result;
	}

	public static AggregatingPS create(ProximityService... from)
	{
		final AggregatingPS result = new AggregatingPS();
		for (ProximityService f : from)
		{
			result.getProximityServices().add(f);
		}

		return result;
	}

	@Override
	public boolean isUniversal()
	{
		for (ProximityService ps : proximityServices)
		{
			if (ps.isUniversal()) return true;
		}

		return false;
	}
}
