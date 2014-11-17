package eu.liveandgov.wp1.sensor_collector.fs;

import com.google.common.io.CharSink;
import com.google.common.io.CharSource;

import java.util.List;

import eu.liveandgov.wp1.sensor_collector.api.Trip;

/**
 * <p>File System</p>
 * Created by lukashaertel on 30.09.2014.
 */
public interface FS {
    /**
     * <p>Lists all the currently stored trips</p>
     *
     * @param complete True if only complete trips, false if only incomplete trips
     * @return Returns a list of trips
     */
    List<Trip> listTrips(boolean complete);


    /**
     * <p>Opens an existing trip for reading</p>
     *
     * @param trip The trip to read
     * @return Returns an input stream
     */
    CharSource readTrip(Trip trip);

    /**
     * @param trip
     * @return
     */
    CharSink writeTrip(Trip trip);

    /**
     * Renames a trip from the source name to the target name
     *
     * @param tripFrom The Source trip
     * @param tripTo   The target trip
     */
    void renameTrip(Trip tripFrom, Trip tripTo);

    void deleteTrip(Trip trip);
}
