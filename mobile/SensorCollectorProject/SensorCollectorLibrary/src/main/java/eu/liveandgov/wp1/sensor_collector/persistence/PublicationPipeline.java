package eu.liveandgov.wp1.sensor_collector.persistence;

import android.os.Environment;
import android.util.Log;

import java.io.File;
;
import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.pipeline.impl.StartsWith;

/**
 * Created by hartmann on 11/12/13.
 */
public class PublicationPipeline implements Consumer<String> {

    public static final String PUBLISH_FILENAME = "published.ssf";

    private StartsWith filter;
    private FilePersistor persistor;

    public PublicationPipeline() {
        File publishFile = new File(Environment.getExternalStorageDirectory(), PUBLISH_FILENAME);

        Log.d("WRITING TO", publishFile.getAbsolutePath());
        persistor = new FilePublisher(publishFile);

        filter = new StartsWith();
        filter.addPrefix(DataCommons.TYPE_GPS);
        filter.addPrefix(DataCommons.TYPE_ACTIVITY);
        filter.addPrefix(DataCommons.TYPE_TAG);

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
