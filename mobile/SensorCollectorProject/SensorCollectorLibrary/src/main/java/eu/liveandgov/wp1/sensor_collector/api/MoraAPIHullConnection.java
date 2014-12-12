package eu.liveandgov.wp1.sensor_collector.api;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * <p>
 * </p>
 * <p>
 * Created on 12.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
public class MoraAPIHullConnection extends MoraAPIHull implements ServiceConnection {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        setImplementation(MoraAPI.Stub.asInterface(service));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        setImplementation(null);
    }
}
