package eu.liveandgov.wp1.sensor_collector.pps.api.ooapi;

import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.liveandgov.wp1.sensor_collector.pps.api.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.api.ProximityType;
import eu.liveandgov.wp1.sensor_collector.pps.api.gi.GridIndexPS;

/**
 * Openstreetmaps Indexed ProximityType Service
 * 
 * @author lukashaertel
 */
public abstract class OSMIPS extends GridIndexPS
{
	private final static String LOG_TAG = "OIPS";

	private String baseURL;

	public OSMIPS(double horizontalResultion, double verticalResulution, boolean byCentroid, int storeDegree, String baseURL)
	{
		super(horizontalResultion, verticalResulution, byCentroid, storeDegree);
		this.baseURL = baseURL;

		if (!this.baseURL.endsWith("/"))
		{
			this.baseURL += '/';
		}
	}

	public String getBaseURL()
	{
		return baseURL;
	}

	public void setBaseURL(String baseURL)
	{
		this.baseURL = baseURL;

		if (!this.baseURL.endsWith("/"))
		{
			this.baseURL += '/';
		}
	}

	@Override
	protected Proximity calculateContains(double lat, double lon)
	{
		try
		{
			// Create cache builder
			final StringBuilder builder = new StringBuilder();

			// Obtain URL
			final URL url = new URL(baseURL + createQueryString(lat, lon));

			// Process response
			final InputStreamReader isr = new InputStreamReader(url.openConnection().getInputStream());
			int c;
			while ((c = isr.read()) != -1)
			{
				builder.append((char) c);
			}
			isr.close();

			final JSONObject response = new JSONObject(builder.toString());
			final JSONArray elements = response.getJSONArray("elements");

			Log.v(LOG_TAG, url + " ~> " + response);

			if (elements.length() > 0)
			{
				String id = String.format(Locale.ENGLISH, "(lat:%f; lon:%f)", lat, lon);

				for (int i = 0; i < elements.length(); i++)
				{
					final JSONObject element = elements.getJSONObject(i);
					if (element.has("tags") && element.getJSONObject("tags").has("name"))
					{
						id = element.getJSONObject("tags").getString("name");
						break;
					}
				}

				// Find out if in range of element satisfying criteria
				return new Proximity(ProximityType.IN_PROXIMITY, id);
			}
			else
			{
				// Find out if in range of element satisfying criteria
				return new Proximity(ProximityType.NOT_IN_PROXIMITY, "");
			}

		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "Error in calculation of proximityType", e);
			// On exception, set error as result
			return null;
		}
		catch (JSONException e)
		{
			Log.e(LOG_TAG, "Error in calculation of proximityType", e);
			// On exception, set error as result
			return null;
		}
	}

	@Override
	public boolean isUniversal()
	{
		// Assume that OSM contains the complete list of stations and is thereby
		// universal
		return true;
	}

	protected abstract String createQueryString(double lat, double lon);
}
