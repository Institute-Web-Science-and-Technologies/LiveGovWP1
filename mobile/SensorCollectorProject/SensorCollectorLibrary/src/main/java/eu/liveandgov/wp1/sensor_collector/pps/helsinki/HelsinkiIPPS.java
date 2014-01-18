package eu.liveandgov.wp1.sensor_collector.pps.helsinki;

import eu.liveandgov.wp1.sensor_collector.pps.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.gi.GridIndexPS;

/**
 * Helsinki Indexed Platform Proximity Service
 *
 * @author lukashaertel
 */
public class HelsinkiIPPS extends GridIndexPS {
    public HelsinkiIPPS(double horizontalResultion, double verticalResulution, int storeDegree) {
        super(horizontalResultion, verticalResulution, storeDegree);
    }

    @Override
    protected Proximity calculateContains(double lon, double lat) {
        // TODO: Implement Stub
        return Proximity.NO_DECISION;
    }

}
