package eu.liveandgov.wp1.sensor_miner.persistence;

import android.os.Environment;
import android.util.Log;

import java.io.File;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.sensor_miner.configuration.SsfFileFormat;
import eu.liveandgov.wp1.sensor_miner.connectors.implementations.PrefixFilter;

/**
 * Created by hartmann on 11/12/13.
 */
public class PublicationPipeline implements Consumer<String> {

    public static final String PUBLISH_FILENAME = "published.ssf";

    private PrefixFilter filter;
    private FilePersistor persistor;

    public PublicationPipeline() {
        File publishFile = new File(Environment.getExternalStorageDirectory(), PUBLISH_FILENAME);

        Log.d("WRITING TO", publishFile.getAbsolutePath());
        persistor = new FilePublisher(publishFile);

        filter = new PrefixFilter();
        filter.addFilter(SsfFileFormat.SSF_GPS);
        filter.addFilter(SsfFileFormat.SSF_ACTIVITY);
        filter.addFilter(SsfFileFormat.SSF_TAG);

        filter.setConsumer(persistor);
    }

    @Override
    public void push(String message) {
        filter.push(message);
    }

    public void deleteSamples() {
        persistor.deleteSamples();
    }
}
