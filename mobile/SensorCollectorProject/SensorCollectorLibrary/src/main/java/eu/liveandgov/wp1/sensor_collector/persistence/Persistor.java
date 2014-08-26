package eu.liveandgov.wp1.sensor_collector.persistence;

import com.google.common.base.Function;

import java.io.File;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.pipeline.Consumer;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;

/**
 * Interface for persistence providers.
 * <p/>
 * Created by hartmann on 9/20/13.
 */
public interface Persistor extends Consumer<Item>, Monitorable {
    /**
     * Exports all stored samples into a given File in the ssf format.
     *
     * @return successFlag
     */
    boolean exportSamples(File stageFile);

    /**
     * Return true if there are samples in store.
     */
    boolean hasSamples();

    /**
     * Delete all stored samples
     */
    void deleteSamples();

    /**
     * Free all resources used by the persistor.
     */
    void close();

    /**
     * Serialization method relying on the items {@link eu.liveandgov.wp1.data.Item#toSerializedForm()} method
     */
    public static final Function<Item, String> REGULAR_SERIALIZATION = new Function<Item, String>() {
        @Override
        public String apply(Item item) {
            return item.toSerializedForm();
        }
    };

    /**
     * Serialization method delegating to {@link eu.liveandgov.wp1.sensor_collector.persistence.JSONPersistor#serialize(eu.liveandgov.wp1.data.Item)}
     */
    public static final Function<Item, String> JSON_SERIALIZATION = new Function<Item, String>() {
        @Override
        public String apply(Item item) {
            return JSONPersistor.serialize(item);
        }
    };
}
