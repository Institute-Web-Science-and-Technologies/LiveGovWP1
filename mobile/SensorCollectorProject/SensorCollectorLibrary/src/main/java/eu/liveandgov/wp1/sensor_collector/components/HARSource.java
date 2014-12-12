package eu.liveandgov.wp1.sensor_collector.components;

import android.os.Bundle;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.HARPipeline;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.Triple;
import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.data.impl.Activity;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.components.Credentials;
import eu.liveandgov.wp1.sensor_collector.components.ItemBuffer;
import eu.liveandgov.wp1.sensor_collector.components.RegularSampleSource;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.os.OS;
import eu.liveandgov.wp1.sensor_collector.os.SampleTarget;

/**
 * <p>
 * Consumes acceleration values and produces activities from HAR
 * </p>
 * </p>
 * <p>
 * Created on 09.12.2014.
 * </p>
 *
 * @author lukashaertel
 * @author hartmann
 */
public class HARSource extends RegularSampleSource {
    /**
     * Logger for the transfer executor
     */
    private static final Logger log = LogPrincipal.get();

    @Inject
    OS os;

    /**
     * Central credentials store
     */
    @Inject
    Credentials credentials;

    /**
     * Central item buffer
     */
    @Inject
    ItemBuffer itemBuffer;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.harWindowLength")
    int windowLength;

    private HARPipeline harPipeline;

    private String lastActivity;

    @Inject
    public HARSource(Configurator configurator) {
        super(configurator);

        harPipeline = new HARPipeline(windowLength);
        harPipeline.setConsumer(new Consumer<Triple<Long, Long, String>>() {
            @Override
            public void push(Triple<Long, Long, String> triple) {
                // Set last activity
                lastActivity = triple.right;

                // Create activity item
                Activity activity = new Activity(
                        System.currentTimeMillis(),
                        credentials.user,
                        triple.right);

                // Offer back
                itemBuffer.offer(activity);
            }
        });
    }


    @Override
    protected void handleActivation() {
        os.addTarget(feedbackTarget);
    }

    @Override
    protected void handleDeactivation() {
        os.removeTarget(feedbackTarget);
    }

    @Override
    protected Integer getDelay(MoraConfig config) {
        return config.har ? 1 : null;
    }

    private final SampleTarget feedbackTarget = new SampleTarget() {
        @Override
        public void handle(Item item) {
            if (item instanceof Acceleration) {
                Acceleration acceleration = (Acceleration) item;
                harPipeline.push(acceleration);
            }
        }

        @Override
        public boolean isSilent() {
            return true;
        }
    };

    @Override
    public Bundle getReport() {
        Bundle report = super.getReport();
        report.putString("lastActivity", lastActivity);
        return report;
    }
}
