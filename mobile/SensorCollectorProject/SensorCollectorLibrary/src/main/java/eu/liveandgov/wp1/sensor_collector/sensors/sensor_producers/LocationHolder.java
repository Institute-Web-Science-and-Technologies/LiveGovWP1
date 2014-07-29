package eu.liveandgov.wp1.sensor_collector.sensors.sensor_producers;

import android.location.Location;

import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.data.impl.Velocity;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.sensor_collector.connectors.sensor_queue.SensorQueue;

/**
 * Created by lukashaertel on 04.12.13.
 */
public abstract class LocationHolder implements SensorHolder {
    private final SensorQueue sensorQueue;

    public LocationHolder(SensorQueue sensorQueue) {
        this.sensorQueue = sensorQueue;
    }

    protected void receivedNewLocation(Location location) {
        sensorQueue.push(new GPS(
                System.currentTimeMillis(),
                GlobalContext.getUserId(),
                location.getLatitude(),
                location.getLongitude(),
                location.hasAltitude() ? location.getAltitude() : null
        ));

        if (SensorCollectionOptions.REC_VEL && location.hasSpeed())
            sensorQueue.push(new Velocity(System.currentTimeMillis(), GlobalContext.getUserId(), location.getSpeed()));
    }

}
