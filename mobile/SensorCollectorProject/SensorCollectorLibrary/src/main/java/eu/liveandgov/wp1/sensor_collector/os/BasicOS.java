package eu.liveandgov.wp1.sensor_collector.os;

import android.os.Bundle;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

/**
 * <p>The basic OS maintains sets of sample sources, sample targets and reporters and controls the
 * activation of the sample sources.</p>
 * <p>The status of all reporters may be queried with the {@link #getReports()} function.</p>
 * Created by lukashaertel on 08.09.2014.
 */
@Singleton
public class BasicOS implements OS {
    /**
     * Logger interface
     */
    private final Logger logger = LogPrincipal.get();

    /**
     * The set of sample sources
     */
    private Set<SampleSource> sampleSources = Sets.newConcurrentHashSet();

    /**
     * The set of sample targets
     */
    private Set<SampleTarget> sampleTargets = Sets.newConcurrentHashSet();

    /**
     * The set of all reportes
     */
    private Set<Reporter> reporters = Sets.newConcurrentHashSet();

    /**
     * <p>Adds a sample source to the OS, activates it if there are potential targets</p>
     *
     * @param sampleSource The sample source
     */
    @Override
    public synchronized void add(SampleSource sampleSource) {
        logger.info("Adding sample source to OS: " + sampleSource);

        // Try to add the sample source, if already contained, do nothing more
        if (sampleSources.add(sampleSource))
            // If targets are non-empty sample source is required to be active
            if (!sampleTargets.isEmpty())
                sampleSource.activate();
    }

    /**
     * <p>Adds a sample target to the OS, activates all sample sources if this is the first target</p>
     *
     * @param sampleTarget The sample target
     */
    @Override
    public synchronized void add(SampleTarget sampleTarget) {
        logger.info("Adding sample target to OS: " + sampleTarget);

        if (sampleTargets.isEmpty()) {
            if (sampleTargets.add(sampleTarget))
                for (SampleSource s : sampleSources)
                    s.activate();
        } else
            sampleTargets.add(sampleTarget);
    }

    /**
     * <p>Adds a reporter to the OS</p>
     *
     * @param reporter The reporter
     */
    @Override
    public synchronized void add(Reporter reporter) {
        logger.info("Adding reporter to OS: " + reporter);

    }

    /**
     * <p>Removes a sample source from the OS, deactivates it if there were potential targets</p>
     *
     * @param sampleSource The sample source
     */
    @Override
    public synchronized void remove(SampleSource sampleSource) {
        logger.info("Removing sample source from OS: " + sampleSource);

        if (sampleSources.remove(sampleSource))
            if (!sampleTargets.isEmpty())
                sampleSource.deactivate();
    }

    /**
     * <p>Removes a sample target from the OS, deactivates all sources if it was the last target</p>
     *
     * @param sampleTarget The sample target
     */
    @Override
    public synchronized void remove(SampleTarget sampleTarget) {
        logger.info("Removing sample target from OS: " + sampleTarget);

        if (sampleTargets.remove(sampleTarget))
            if (sampleTargets.isEmpty())
                for (SampleSource s : sampleSources)
                    s.deactivate();
    }

    /**
     * <p>Removes a reporter from the OS</p>
     *
     * @param reporter The reporter
     */
    @Override
    public synchronized void remove(Reporter reporter) {
        logger.info("Removing reporter from OS: " + reporter);

    }

    @Override
    public Set<Bundle> getReports() {
        return null;
    }
}
