package eu.liveandgov.wp1.sensor_collector.os;


import android.os.Bundle;

import java.util.List;

/**
 * <p>Operating system</p>
 * Created by lukashaertel on 08.09.2014.
 */
public interface OS {
    void startConnector();

    void stopConnector();

    boolean isActive();

    void addSource(SampleSource sampleSource);

    void addTarget(SampleTarget sampleTarget);

    void addReporter(Reporter reporter);

    void removeSource(SampleSource sampleSource);

    void removeTarget(SampleTarget sampleTarget);

    void removeReporter(Reporter reporter);

    /**
     * @return
     */
    List<Bundle> getReports();
}
