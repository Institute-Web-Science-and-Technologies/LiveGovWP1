package eu.liveandgov.wp1.sensor_collector.pps.ooapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import eu.liveandgov.wp1.sensor_collector.pps.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.gi.GridIndexPS;


/**
 * Openstreetmaps Indexed Proximity Service
 *
 * @author lukashaertel
 */
public abstract class OSMIPS extends GridIndexPS {
    private String baseURL;

    public OSMIPS(double horizontalResultion, double verticalResulution, int storeDegree, String baseURL) {
        super(horizontalResultion, verticalResulution, storeDegree);
        this.baseURL = baseURL;

        if (!this.baseURL.endsWith("/")) {
            this.baseURL += '/';
        }
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;

        if (!this.baseURL.endsWith("/")) {
            this.baseURL += '/';
        }
    }

    @Override
    protected Proximity calculateContains(double lon, double lat) {
        try {
            // Create cache builder
            final StringBuilder builder = new StringBuilder();

            // Obtain URL
            final URL url = new URL(baseURL + createQueryString(lon, lat));

            // Process response
            final InputStreamReader isr = new InputStreamReader(url.openConnection().getInputStream());
            int c;
            while((c =isr.read()) != -1)
            {
                builder.append((char)c);
            }
            isr.close();

            final JSONObject response = new JSONObject(builder.toString());
            final JSONArray elements = response.getJSONArray("elements");

            // Find out if in range of element satisfying criteria
            final boolean result = elements.length() > 0;


            // Return result
            return result ? Proximity.IN_PROXIMITY : Proximity.NOT_IN_PROXIMITY;
        } catch (IOException e) {
            // On exception, set no decision as result
            return Proximity.NO_DECISION;
        } catch (JSONException e) {
            // On exception, set no decision as result
            return Proximity.NO_DECISION;
        }
    }

    protected abstract String createQueryString(double lat, double lon);
}
