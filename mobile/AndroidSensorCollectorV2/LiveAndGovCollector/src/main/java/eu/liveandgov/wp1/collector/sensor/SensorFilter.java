package eu.liveandgov.wp1.collector.sensor;

import android.hardware.SensorEvent;

import java.util.List;

/**
 * Created by cehlen on 9/12/13.
 */
public abstract class SensorFilter {

    private List<SensorFilter> children;

    public void pipe(SensorFilter filter) {
        children.add(filter);
    }

    protected void push(SensorEvent se) {
        for(SensorFilter filter : children) {
            filter.process(se);
        }
    }

    protected abstract void process(SensorEvent se);

}
