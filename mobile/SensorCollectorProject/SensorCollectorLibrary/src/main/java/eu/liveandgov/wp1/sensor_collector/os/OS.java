package eu.liveandgov.wp1.sensor_collector.os;


import android.os.Bundle;

import java.util.List;
import java.util.Set;

/**
 * Created by lukashaertel on 08.09.2014.
 */
public interface OS {
    /**
     * The field of the report bundle that contains the list of identities
     */
    public static final String IDENTIITES = "identities";

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
    Set<Bundle> getReports();
}
