package eu.liveandgov.wp1.sensor_collector.os;


import android.os.Bundle;

import java.util.List;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;

/**
 * <p>Operating system</p>
 * Created by lukashaertel on 08.09.2014.
 */
public interface OS {
    void startConnector();
    void stopConnector();

    boolean isActive();

    void add(SampleSource sampleSource);

    void add(SampleTarget sampleTarget);

    void add(Reporter reporter);

    void remove(SampleSource sampleSource);

    void remove(SampleTarget sampleTarget);

    void remove(Reporter reporter);

    /**
     * @return
     */
    List<Bundle> getReports();
}
