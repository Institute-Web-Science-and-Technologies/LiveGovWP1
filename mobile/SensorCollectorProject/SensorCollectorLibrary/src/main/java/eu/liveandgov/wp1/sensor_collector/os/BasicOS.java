package eu.liveandgov.wp1.sensor_collector.os;

import android.os.Bundle;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.components.ItemBuffer;
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
    private static final Logger logger = LogPrincipal.get();

    @Inject
    ScheduledExecutorService scheduledExecutorService;

    @Inject
    ItemBuffer itemBuffer;


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

    private ScheduledFuture<?> connector = null;

    @Override
    public void startConnector() {
        // If already connected, return
        if (connector != null) {
            logger.warn("Repetitive call to start connector, maybe inconsistent state transitions");
            return;
        }

        // Start runnable and store handle
        connector = scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                // While connector is running
                while (connector != null && !connector.isCancelled()) {
                    // Poll an item
                    Item item = itemBuffer.poll();

                    // If no item could be polled during the timeout, skip this round
                    if (item == null)
                        continue;

                    // Offer item to all targets
                    for (SampleTarget t : sampleTargets)
                        t.handle(item);
                }
            }
        }, 0L, TimeUnit.SECONDS);
    }

    @Override
    public void stopConnector() {
        if (connector == null) {
            logger.warn("Repetitive call to stop connector, maybe inconsistent state transitions");
            return;
        }

        connector.cancel(false);
        connector = null;
    }

    @Override
    public boolean isActive() {
        return !sampleTargets.isEmpty();
    }

    /**
     * <p>Adds a sample source to the OS, activates it if there are potential targets</p>
     *
     * @param sampleSource The sample source
     */
    @Override
    public synchronized void addSource(SampleSource sampleSource) {
        logger.info("Adding sample source to OS: " + sampleSource);

        // Try to add the sample source, if already contained, do nothing more
        if (sampleSources.add(sampleSource))
            // If targets are non-empty sample source is required to be active
            if (!isOnlySilentRemaining())
                sampleSource.activate();
    }

    /**
     * <p>Adds a sample target to the OS, activates all sample sources if this is the first target</p>
     *
     * @param sampleTarget The sample target
     */
    @Override
    public synchronized void addTarget(SampleTarget sampleTarget) {
        logger.info("Adding sample target to OS: " + sampleTarget);

        if (isOnlySilentRemaining() && !sampleTarget.isSilent()) {
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
    public synchronized void addReporter(Reporter reporter) {
        logger.info("Adding reporter to OS: " + reporter);

        reporters.add(reporter);
    }

    /**
     * <p>Removes a sample source from the OS, deactivates it if there were potential targets</p>
     *
     * @param sampleSource The sample source
     */
    @Override
    public synchronized void removeSource(SampleSource sampleSource) {
        logger.info("Removing sample source from OS: " + sampleSource);

        if (sampleSources.remove(sampleSource))
            if (!isOnlySilentRemaining())
                sampleSource.deactivate();
    }

    /**
     * <p>Removes a sample target from the OS, deactivates all sources if it was the last target</p>
     *
     * @param sampleTarget The sample target
     */
    @Override
    public synchronized void removeTarget(SampleTarget sampleTarget) {
        logger.info("Removing sample target from OS: " + sampleTarget);

        if (sampleTargets.remove(sampleTarget))
            if (isOnlySilentRemaining())
                for (SampleSource s : sampleSources)
                    s.deactivate();
    }

    private boolean isOnlySilentRemaining() {
        for (SampleTarget target : sampleTargets)
            if (!target.isSilent())
                return false;

        return true;
    }

    /**
     * <p>Removes a reporter from the OS</p>
     *
     * @param reporter The reporter
     */
    @Override
    public synchronized void removeReporter(Reporter reporter) {
        logger.info("Removing reporter from OS: " + reporter);

        reporters.remove(reporter);
    }

    @Override
    public List<Bundle> getReports() {
        List<Bundle> res = Lists.newArrayList();

        for (Reporter r : reporters)
            res.add(r.getReport());

        return res;
    }
}
