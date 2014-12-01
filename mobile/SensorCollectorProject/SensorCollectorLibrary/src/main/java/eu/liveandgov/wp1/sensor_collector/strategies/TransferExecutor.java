package eu.liveandgov.wp1.sensor_collector.strategies;

import java.io.IOException;

import eu.liveandgov.wp1.sensor_collector.api.Trip;
import eu.liveandgov.wp1.sensor_collector.fs.DataSource;

/**
 * <p>Strategy to execute a transfer</p>
 * Created by lukashaertel on 25.08.2014.
 */
public interface TransferExecutor {
    /**
     * <p>Transfers the the trip with the content specified in the data source</p>
     *
     * @param trip The trip to transfer
     * @param s    The byte source
     * @return Returns true if the transfer was successful
     * @throws IOException Throws an IO exception if the implementation has IO problems
     */
    boolean transfer(Trip trip, DataSource s) throws IOException;
}
