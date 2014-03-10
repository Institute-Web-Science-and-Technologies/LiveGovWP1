package eu.liveandgov.wp1.pps.api.csv;

import eu.liveandgov.wp1.pps.api.CalculationResult;
import eu.liveandgov.wp1.pps.api.gi.GridIndexPS;
import org.apache.commons.csv.CSVParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

/**
 * <p>Static Indexed ProximityType Service, indexes by a given CSV file proxmities</p>
 *
 * @author lukashaertel
 */

public class StaticIPS extends GridIndexPS {
    /**
     * Producer of the input stream used to define the CSV file
     */
    private final Callable<InputStream> inputStreams;

    /**
     * True if this static IPS can make assumptions on every coordinate of the world, usually false
     */
    private final boolean universal;

    /**
     * The index of the ID column
     */
    private final int idField;

    /**
     * The index of the latitude column
     */
    private final int latField;

    /**
     * The index of the longitude column
     */
    private final int lonField;

    /**
     * The distance that limits proximity
     */
    private final double distance;

    /**
     * Constructs a new instance with the given values
     *
     * @param horizontalResolution Horizontal resolution of the grid in degrees (division of longitude)
     * @param verticalResolution  Vertical resolution of the grid in degrees (division of latitude)
     * @param byCentroid          Specify this as true to calculate the value of an index by its centroid rather than its first calling parameters
     * @param storeDegree         Storage level of the index, maximum of stored indices
     * @param inputStreams        Producer of the input stream used to define the CSV file
     * @param universal           True if this static IPS can make assumptions on every coordinate of the world, usually false
     * @param idField             The index of the ID column
     * @param latField            The index of the latitude column
     * @param lonField            The index of the longitude column
     * @param distance            The distance that limits proximity
     */
    public StaticIPS(double horizontalResolution, double verticalResolution, boolean byCentroid, int storeDegree, Callable<InputStream> inputStreams, boolean universal, int idField, int latField, int lonField, double distance) {
        super(horizontalResolution, verticalResolution, byCentroid, storeDegree);

        this.inputStreams = inputStreams;
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
    protected CalculationResult calculateContains(double lat, double lon) {
        try {
            // Open asset and stream thereon
            final InputStream inputStream = inputStreams.call();
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
                    return new CalculationResult(CalculationResult.CalculationType.IN_PROXIMITY, line[idField].trim());
                }
            }
            // Else return value corresponding to the exhaustiveness of this PS
            inputStreamReader.close();
            inputStream.close();

            if (universal) {
                return new CalculationResult(CalculationResult.CalculationType.NOT_IN_PROXIMITY, "");
            } else {
                return new CalculationResult(CalculationResult.CalculationType.NO_DECISION, "");
            }
        } catch (Exception e) {
            return new CalculationResult(CalculationResult.CalculationType.ERROR, "");
        }
    }

    @Override
    public boolean isUniversal() {
        return universal;
    }
}
