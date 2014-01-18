package eu.liveandgov.wp1.sensor_collector.pps.gi;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import eu.liveandgov.wp1.sensor_collector.pps.Proximity;
import eu.liveandgov.wp1.sensor_collector.pps.ProximityService;

/**
 * Grid Index Proximity Service
 *
 * @author lukashaertel
 */
public abstract class GridIndexPS implements ProximityService {
    private final LinkedHashMap<Field, Proximity> calculated;

    private final double horizontalResultion;

    private final double verticalResulution;

    private int storeDegree;

    public GridIndexPS(double horizontalResultion, double verticalResulution, int storeDegree) {
        this.calculated = new LinkedHashMap<Field, Proximity>();
        this.horizontalResultion = horizontalResultion;
        this.verticalResulution = verticalResulution;
        this.storeDegree = storeDegree;
    }

    public double getHorizontalResultion() {
        return horizontalResultion;
    }

    public double getVerticalResulution() {
        return verticalResulution;
    }

    public int getStoreDegree() {
        return storeDegree;
    }

    public void setStoreDegree(int storeDegree) {
        assert storeDegree > 0;

        this.storeDegree = storeDegree;

        assertStoreDegree();
    }

    private final void assertStoreDegree() {
        final Iterator<Entry<Field, Proximity>> it = calculated.entrySet().iterator();

        while (calculated.size() > storeDegree) {
            it.next();
            it.remove();
        }
    }

    @Override
    public Proximity calculate(double lon, double lat) {
        final Field at = new Field((long) Math.round(lon / horizontalResultion), (long) Math.round(lat / verticalResulution));

        Proximity result = calculated.get(at);
        if (result == null) {
            calculated.put(at, result = calculateContains(lon, lat));

            assertStoreDegree();
        }

        return result;
    }

    protected abstract Proximity calculateContains(double lon, double lat);
}
