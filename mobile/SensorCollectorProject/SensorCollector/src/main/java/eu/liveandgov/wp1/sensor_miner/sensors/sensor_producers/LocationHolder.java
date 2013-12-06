package eu.liveandgov.wp1.sensor_miner.sensors.sensor_producers;

import android.content.Intent;
import android.location.Location;
import android.provider.Settings;
import android.widget.Toast;

import eu.liveandgov.wp1.sensor_miner.GlobalContext;
import eu.liveandgov.wp1.sensor_miner.connectors.sensor_queue.SensorQueue;
import eu.liveandgov.wp1.sensor_miner.sensors.SensorSerializer;

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
        sensorQueue.push(SensorSerializer.fromLocation(location));
    }

    protected void checkEnableGPS(){
        String provider = Settings.Secure.getString(GlobalContext.context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        assertNotNull(provider);

        if(provider.equals("")){
            Toast toast = Toast.makeText(GlobalContext.context, "Please enable location services.", Toast.LENGTH_SHORT);
            toast.show();

            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            GlobalContext.context.startActivity(intent);
        }

    }
}
