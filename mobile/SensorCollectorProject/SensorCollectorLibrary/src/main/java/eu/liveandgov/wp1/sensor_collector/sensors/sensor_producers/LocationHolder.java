package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.location.Location;

import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_collector.sensors.SensorSerializer;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by lukashaertel on 04.12.13.
 */
public abstract class LocationHolder implements SensorHolder {
    private final SensorQueue sensorQueue;

    public LocationHolder(SensorQueue sensorQueue)
    {
        this.sensorQueue = sensorQueue;
    }

    protected  void receivedNewLocation(Location location)
    {
        sensorQueue.push(SensorSerializer.location.toSSFDefault(location));
    }

}
