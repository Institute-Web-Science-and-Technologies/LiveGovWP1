package eu.liveandgov.wp1.sensor_collector.components;

import android.os.Bundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import eu.liveandgov.wp1.data.impl.Tag;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;
import eu.liveandgov.wp1.sensor_collector.os.SampleSource;

/**
 * <p>Source for user entered tags</p>
 * Created by lukashaertel on 30.11.2014.
 */
@Singleton
public class TagSource implements SampleSource, Reporter {
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

    /**
     * Stores if this tag source is active, inactive tag sources won't offer tags to the item buffer
     */
    private boolean active = false;

    /**
     * <p>Annotates the item stream</p>
     *
     * @param annotation The annotation to write
     */
    public void annotate(String annotation) {
        if (isActive())
            itemBuffer.offer(new Tag(System.currentTimeMillis(), credentials.user, annotation));
    }

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void deactivate() {
        active = false;
    }

    @Override
    public Bundle getReport() {
        Bundle report = new Bundle();
        report.putString(SPECIAL_KEY_ORIGINATOR, getClass().getSimpleName());

        report.putBoolean("active", isActive());

        return report;
    }
}
