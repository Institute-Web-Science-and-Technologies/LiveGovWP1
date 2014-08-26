package eu.liveandgov.wp1.sensor_collector.persistence;

import android.os.Environment;

import org.apache.log4j.Logger;

import java.io.File;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.Filter;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;

;

/**
 * Created by hartmann on 11/12/13.
 */
public class PublicationPipeline implements Consumer<Item> {
    private final Logger log = LogPrincipal.get();

    public static final String PUBLISH_FILENAME = "published.ssf";

    private Filter<Item> filter;
    private FilePersistor persistor;

    public PublicationPipeline() {
        File publishFile = new File(Environment.getExternalStorageDirectory(), PUBLISH_FILENAME);

        log.debug("Writing to " + publishFile.getAbsolutePath());
        persistor = new FilePersistor(publishFile, Persistor.REGULAR_SERIALIZATION);

        filter = new Filter<Item>() {
            @Override
            protected boolean filter(Item item) {
                if (DataCommons.TYPE_GPS.equals(item.getType())) return true;
                if (DataCommons.TYPE_ACTIVITY.equals(item.getType())) return true;
                if (DataCommons.TYPE_TAG.equals(item.getType())) return true;

                return false;
            }
        };

        filter.setConsumer(persistor);
    }

    @Override
    public void push(Item item) {
        filter.push(item);
    }

    public void deleteSamples() {
        persistor.deleteSamples();
    }
}
