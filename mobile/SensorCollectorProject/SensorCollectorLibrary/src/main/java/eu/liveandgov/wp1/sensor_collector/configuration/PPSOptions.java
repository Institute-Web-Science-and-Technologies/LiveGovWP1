package eu.liveandgov.wp1.sensor_collector.configuration;

/**
 * Created by lukashaertel on 18.01.14.
 */
public class PPSOptions {
    public static final double PROXIMITY = 25.0;

    public static final String INDEX_FILE = "grid.index";

    public static final double INDEX_HORIZONTAL_RESOLUTION = PROXIMITY / 111132.954;
    public static final double INDEX_VERTICAL_RESOLUTION = PROXIMITY / 111132.954;
    public static final boolean INDEX_BY_CENTROID = true;

    public static final int INDEX_STORE_DEGREE = 2048;

    public static final String OSMIPPS_BASE_URL = "http://overpass.osm.rambler.ru/cgi/";
}
