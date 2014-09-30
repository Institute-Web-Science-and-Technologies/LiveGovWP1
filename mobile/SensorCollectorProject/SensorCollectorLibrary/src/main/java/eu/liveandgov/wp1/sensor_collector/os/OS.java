package eu.liveandgov.wp1.sensor_collector.os;


import android.os.Bundle;

import java.util.List;

/**
 * <p>Operating system</p>
 * Created by lukashaertel on 08.09.2014.
 */
public interface OS {
    void add(SampleSource sampleSource);

    void add(SampleTarget sampleTarget);

    void add(Reporter reporter);

    void remove(SampleSource sampleSource);

    void remove(SampleTarget sampleTarget);

    void remove(Reporter reporter);

    /**
     * TODO: WHAT HERE
     * @return
     */
    List<Bundle> getReports();
}
