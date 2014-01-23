package eu.liveandgov.wp1.sensor_collector.pps.api.helsinki;

import android.content.Context;
import android.util.Log;

import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import eu.liveandgov.wp1.sensor_collector.pps.api.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.api.gi.GridIndexPS;

/**
 * Helsinki Indexed Platform Proximity Service
 *
 * @author lukashaertel
 */
public class HelsinkiIPPS extends GridIndexPS {
    private final static String LOG_TAG = "HIPS";
    private final Context context;

    private final String asset;

    private final double distance;

    public HelsinkiIPPS(double horizontalResultion, double verticalResulution, boolean byCentroid, int storeDegree, Context context, String asset, double distance) {
        super(horizontalResultion, verticalResulution, byCentroid, storeDegree);

        this.context = context;
        this.asset = asset;
        this.distance = distance;
    }

    private static double haversine(final double lon1, double lat1, final double lon2, double lat2) {
        final double R = 6371000.785;
        final double dLat = Math.toRadians(lat2 - lat1);
        final double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    protected Proximity calculateContains(double lon, double lat) {
        try {
            final InputStream inputStream = context.getAssets().open(asset);
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            CSVParser p = new CSVParser(inputStreamReader);
            String[] line;
            while ((line = p.getLine()) != null) {
                final double clon = Double.valueOf(line[4]);
                final double clat = Double.valueOf(line[5]);

                if (haversine(lon, lat, clon, clat) < distance) {

                    inputStreamReader.close();
                    inputStream.close();
                    return Proximity.IN_PROXIMITY;
                }
            }
            inputStreamReader.close();
            inputStream.close();
            return Proximity.NO_DECISION; //TODO: Negative-tests
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in calculation of proximity", e);
            return Proximity.ERROR;
        }
    }
}
