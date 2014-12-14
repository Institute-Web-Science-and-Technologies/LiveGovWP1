package eu.liveandgov.wp1.sensor_collector.components;

import android.os.Bundle;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSink;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.io.IOException;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;
import eu.liveandgov.wp1.sensor_collector.os.SampleTarget;
import eu.liveandgov.wp1.sensor_collector.serial.ItemSerializer;

/**
 * Created by Pazuzu on 07.10.2014.
 */
@Singleton
public class WriterTarget implements SampleTarget, Reporter {
    private static final Logger logger = LogPrincipal.get();

    @Inject
    ItemSerializer itemSerializer;

    private CharSink sink;

    public void setSink(CharSink sink) {
        this.sink = sink;
    }

    private long charsWritten = 0;

    @Override
    public void handle(Item item) {
        if (sink == null)
            return;

        CharSequence form = itemSerializer.serialize(item);
        charsWritten += form.length();
        try {
            sink.writeLines(ImmutableList.of(form));
        } catch (IOException e) {
            logger.error("Exception writing sample " + item, e);
        }
    }

    @Override
    public Bundle getReport() {
        Bundle report = new Bundle();
        report.putString(SPECIAL_KEY_ORIGINATOR, getClass().getSimpleName());

        report.putBoolean("sinkPresent", sink != null);
        report.putLong("charsWritten", charsWritten);
        return report;
    }

    @Override
    public boolean isSilent() {
        return false;
    }
}
