package eu.liveandgov.wp1.sensor_collector.configuration;

/**
 * Created by lukashaertel on 18.01.14.
 */
public class PPSOptions {
    public static final double PROXIMITY = 25.0;
    public static final double INDEX_PROXIMITY_SUBDIVISIONS = 5.0;

    public static final double INDEX_HORIZONTAL_RESOLUTION = (PROXIMITY / INDEX_PROXIMITY_SUBDIVISIONS) / 111132.954;
    public static final double INDEX_VERTICAL_RESOLUTION = (PROXIMITY / INDEX_PROXIMITY_SUBDIVISIONS) / 111132.954;
    public static final boolean INDEX_BY_CENTROID = true;

    public static final int INDEX_STORE_DEGREE = 8192;

    // public static final String OSMIPPS_BASE_URL = "http://overpass.osm.rambler.ru/cgi/";
    // public static final String OSMIPPS_INDEX_FILE = "osmipps.index";

    public static final String HELSINKIIPPS_ASSET = "helsinki-stops.csv";
    public static final int HELSINKI_ID_FIELD = 2;
    public static final int HELSINKI_LON_FIELD = 5;
    public static final int HELSINKI_LAT_FIELD = 4;
    public static final String HELSINKIIPPS_INDEX_FILE = "helsinkiipps.index";
}
