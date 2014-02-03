package eu.liveandgov.wp1.sensor_collector.pps.api.csv;

import android.content.Context;
import android.util.Log;

import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import eu.liveandgov.wp1.sensor_collector.pps.api.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.api.ProximityType;
import eu.liveandgov.wp1.sensor_collector.pps.api.gi.GridIndexPS;

/**
 * Static Indexed ProximityType Service, indexes by a given CSV file proxmities
 *
 * @author lukashaertel
 */
public class StaticIPS extends GridIndexPS {
    private final static String LOG_TAG = "SIPS";
    private final Context context;

    private final String asset;

    private final boolean universal;

    private final int idField;

    private final int lonField;

    private final int latField;

    private final double distance;

    public StaticIPS(double horizontalResultion, double verticalResulution, boolean byCentroid, int storeDegree, Context context, String asset, boolean universal, int idField, int latField, int lonField, double distance) {
        super(horizontalResultion, verticalResulution, byCentroid, storeDegree);

        this.context = context;
        this.asset = asset;
        this.universal = universal;
        this.idField = idField;
        this.lonField = lonField;
        this.latField = latField;
        this.distance = distance;
    }

    /**
     * We might have this functions everywhere around this project
     */
    private static double haversine(double lat1, final double lon1, double lat2, final double lon2) {
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
    protected Proximity calculateContains(double lat, double lon) {
        try {
            // Open asset and stream thereon
            final InputStream inputStream = context.getAssets().open(asset);
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final CSVParser p = new CSVParser(inputStreamReader);

            // Read every line
            String[] line;
            while ((line = p.getLine()) != null) {
                // Get item coordinates
                final double cLat = Double.valueOf(line[latField]);
                final double cLon = Double.valueOf(line[lonField]);

                // Test distance
                if (haversine(lat, lon, cLat, cLon) < distance) {
                    // If in range, close file and return in-proximityType
                    inputStreamReader.close();
                    inputStream.close();
                    return new Proximity(ProximityType.IN_PROXIMITY, line[idField].trim());
                }
            }
            // Else return value corresponding to the exhaustiveness of this PS
            inputStreamReader.close();
            inputStream.close();

            if (universal) {
                return new Proximity(ProximityType.NOT_IN_PROXIMITY, "");
            } else {
                return new Proximity(ProximityType.NO_DECISION, "");
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in calculation of proximityType", e);
            return null;
        }
    }

    @Override
    public boolean isUniversal() {
        return universal;
    }
}
