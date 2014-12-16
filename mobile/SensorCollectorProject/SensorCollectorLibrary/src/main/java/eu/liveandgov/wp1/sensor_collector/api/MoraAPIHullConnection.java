package eu.liveandgov.wp1.sensor_collector.api;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.common.collect.Sets;

import java.util.Set;

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
    private final Set<Runnable> connected = Sets.newConcurrentHashSet();

    public void initConnected(Runnable runnable) {
        if (connected.add(runnable))
            if (getImplementation() != null)
                runnable.run();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        setImplementation(MoraAPI.Stub.asInterface(service));

        for (Runnable runnable : connected)
            runnable.run();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        setImplementation(null);
    }
}
