package eu.liveandgov.wp1.sensor_collector.components;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSink;
import com.google.inject.Inject;

import org.apache.log4j.Logger;

import java.io.IOException;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.os.SampleTarget;

/**
 * Created by Pazuzu on 07.10.2014.
 */
public class Writer implements SampleTarget {
    private Logger logger = LogPrincipal.get();

    private CharSink sink;

    public void setSink(CharSink sink) {
        this.sink = sink;
    }

    @Override
    public void handle(Item item) {
        if(sink==null)
            return;

        try {
            sink.writeLines(ImmutableList.of(item.toSerializedForm()));
        } catch (IOException e) {
            logger.error("Error writing sample " + item, e);
        }
    }
}
