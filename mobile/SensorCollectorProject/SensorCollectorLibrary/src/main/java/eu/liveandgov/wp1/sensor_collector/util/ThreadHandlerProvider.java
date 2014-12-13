package eu.liveandgov.wp1.sensor_collector.util;

import android.os.Handler;
import android.os.Looper;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.concurrent.Exchanger;

/**
 * <p>
 * Provides a handler running in a new thread
 * </p>
 * <p>
 * Created on 13.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
@Singleton
public class ThreadHandlerProvider implements Provider<Handler> {

    @Override
    public Handler get() {
        // Make result exchanger
        final Exchanger<Handler> exchanger = new Exchanger<>();

        // Schedule a new thread
        new Thread() {
            @Override
            public void run() {
                // Prepare looper in this thread
                Looper.prepare();

                // Exchange handler on the new looper
                try {
                    exchanger.exchange(new Handler(Looper.myLooper()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Loop the looper
                Looper.loop();
            }
        }.start();

        try {
            // Return the exchanged handler
            return exchanger.exchange(null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
