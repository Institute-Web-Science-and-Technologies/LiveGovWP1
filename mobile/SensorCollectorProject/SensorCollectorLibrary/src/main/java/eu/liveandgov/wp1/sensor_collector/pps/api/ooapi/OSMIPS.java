package eu.liveandgov.wp1.sensor_collector.pps.api.ooapi;

import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.liveandgov.wp1.sensor_collector.pps.api.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.api.gi.GridIndexPS;


/**
 * Openstreetmaps Indexed Proximity Service
 *
 * @author lukashaertel
 */
public abstract class OSMIPS extends GridIndexPS {
    private final static String LOG_TAG = "OIPS";

    private String baseURL;

    public OSMIPS(double horizontalResultion, double verticalResulution, boolean byCentroid, int storeDegree, String baseURL) {
        super(horizontalResultion, verticalResulution, byCentroid, storeDegree);
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
            Log.e(LOG_TAG, "Error in calculation of proximity", e);
            // On exception, set error as result
            return Proximity.ERROR;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error in calculation of proximity", e);
            // On exception, set error as result
            return Proximity.ERROR;
        }
    }

    protected abstract String createQueryString(double lat, double lon);
}
