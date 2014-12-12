package eu.liveandgov.wp1.sensor_collector.api;

import eu.liveandgov.wp1.data.Callback;

/**
 * <p>
 * </p>
 * <p>
 * Created on 12.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
public class Callbacks {
    public static final Callback<Object> NO_OP_CALLBACK = new Callback<Object>() {
        @Override
        public void call(Object o) {
        }
    };
}
