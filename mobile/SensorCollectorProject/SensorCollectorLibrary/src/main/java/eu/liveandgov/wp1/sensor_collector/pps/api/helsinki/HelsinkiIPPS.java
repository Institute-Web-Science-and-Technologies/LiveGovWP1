package eu.liveandgov.wp1.sensor_collector.pps.api.helsinki;

import eu.liveandgov.wp1.sensor_collector.pps.api.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.api.gi.GridIndexPS;

/**
 * Helsinki Indexed Platform Proximity Service
 *
 * @author lukashaertel
 */
public class HelsinkiIPPS extends GridIndexPS {
    public HelsinkiIPPS(double horizontalResultion, double verticalResulution, boolean byCentroid, int storeDegree) {
        super(horizontalResultion, verticalResulution, byCentroid, storeDegree);
    }

    @Override
    protected Proximity calculateContains(double lon, double lat) {
        // TODO: Implement Stub
        return Proximity.NO_DECISION;
    }

}
