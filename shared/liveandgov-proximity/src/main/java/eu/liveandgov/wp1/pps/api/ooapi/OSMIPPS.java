package eu.liveandgov.wp1.pps.api.ooapi;

/**
 * Openstreetmaps Indexed Platform ProximityType Service
 *
 * @author lukashaertel
 */
public class OSMIPPS extends OSMIPS {
    private final double distance;

    public OSMIPPS(double horizontalResultion, double verticalResulution, boolean byCentroid, int storeDegree, String baseURL, double distance) {
        super(horizontalResultion, verticalResulution, byCentroid, storeDegree, baseURL);
        this.distance = distance;
    }

    @Override
    protected String createQueryString(double lat, double lon) {
        final double radiusDeg = distance / 111132.954;

        final double s = lat - radiusDeg;
        final double w = lon - radiusDeg;
        final double n = lat + radiusDeg;
        final double e = lon + radiusDeg;
        final String bbs = s + "," + w + "," + n + "," + e;

        return "interpreter?data=[out:json];(node[\"public_transport\"=\"platform\"](" + bbs + ");way[\"public_transport\"=\"platform\"](" + bbs
                + ");rel[\"public_transport\"=\"platform\"](" + bbs + "););(._;>;);out;";
    }

}
