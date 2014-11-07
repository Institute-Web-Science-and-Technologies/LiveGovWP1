package eu.liveandgov.wp1.sensor_collector;

import android.content.Intent;
import android.os.IBinder;

import roboguice.service.RoboService;

/**
 * Created by Pazuzu on 07.11.2014.
 */
public abstract class BaseMoraService extends RoboService {
    private int connections = 0;

    protected abstract void activateProvision();

    protected abstract void activateStandAlone();

    protected abstract IBinder getBinder();

    @Override
    public IBinder onBind(Intent intent) {
        if (connections++ == 0)
            activateProvision();

        // Return the API binder
        return getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        if (connections++ == 0)
            activateProvision();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (connections-- == 1)
            activateStandAlone();

        return true;
    }
}
