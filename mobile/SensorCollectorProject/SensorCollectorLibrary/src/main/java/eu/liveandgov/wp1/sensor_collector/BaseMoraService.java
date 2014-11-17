package eu.liveandgov.wp1.sensor_collector;

import android.content.Intent;
import android.os.IBinder;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import roboguice.service.RoboService;

/**
 * Created by Pazuzu on 07.11.2014.
 */
public abstract class BaseMoraService extends RoboService {
    /**
     * Logger interface
     */
    private static final Logger logger = LogPrincipal.get();

    private int connections = 0;

    public int getConnections() {
        return connections;
    }

    /**
     * Activates provision mode, i.e. the first connection was activated
     */
    protected void activateProvision() {
    }

    /**
     * Activates the stand alone mode of the service
     *
     * @return Returns true if a component is active, if false is returned, the service terminates
     */
    protected boolean activateStandAlone() {
        return false;
    }

    protected void startup() throws Throwable {
        // User may implement
    }

    protected void shutdown() throws Throwable {
        // User may implement
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            startup();
        } catch (Throwable throwable) {
            logger.error("Error starting up Mora service", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public void onDestroy() {
        try {
            shutdown();
        } catch (Throwable throwable) {
            logger.error("Error shutting down Mora service", throwable);
            throw new RuntimeException(throwable);
        }

        super.onDestroy();
    }

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
            if (!activateStandAlone())
                stopSelf();

        return true;
    }
}
