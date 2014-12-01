package eu.liveandgov.wp1.sensor_collector.rec;

import android.os.Bundle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.helper.TimedQueue2;
import eu.liveandgov.wp1.sensor_collector.api.RecorderConfig;
import eu.liveandgov.wp1.sensor_collector.os.OS;
import eu.liveandgov.wp1.sensor_collector.os.SampleTarget;

/**
 * Created by lukashaertel on 30.11.2014.
 */
public class BasicRecorder implements Recorder {
    /**
     * Operating system is required to initialize listeners for the sensor types
     */
    @Inject
    OS os;

    private Map<RecorderConfig, TimedQueue2<Item>> recorders = Maps.newHashMap();

    /**
     * Recorder target intercepting all
     */
    private final SampleTarget recorderTarget = new SampleTarget() {
        @Override
        public void handle(Item item) {
            // TODO: Use implementation of a timed queue that actually regard the recording configs max limti
            for (Map.Entry<RecorderConfig, TimedQueue2<Item>> recorder : recorders.entrySet())
                // Push to recorders queue if this is a desired item type
                if (recorder.getKey().itemTypes.contains(item.getType())) {
                    // Offer to queue
                    recorder.getValue().push(item);
                    // Resize the recorders queue if exceeding the limit
                    while (recorder.getValue().items().size() > recorder.getKey().maximum) {
                        // Resize by iterator and break if empty
                        Iterator<Item> iterator = recorder.getValue().items().iterator();
                        if (iterator.hasNext()) {
                            iterator.next();
                            iterator.remove();
                        } else
                            break;
                    }

                }
        }
    };

    @Override
    public void registerRecorder(RecorderConfig config) {
        // Create a new timed queue for the new recorded items
        TimedQueue2<Item> current = new TimedQueue2<Item>(config.timeSpanMs) {
            @Override
            protected long getTime(Item item) {
                return item.getTimestamp();
            }
        };

        // Add to recorders, keep track of previous mapping
        TimedQueue2<Item> before = recorders.put(config, current);

        // If old was not empty, add all until maximum is reached
        if (before != null)
            for (Item item : before.items()) {
                current.push(item);
                if (current.items().size() > config.maximum)
                    break;
            }

        // If first entering, add consumer to OS
        if (recorders.size() == 1 && before == null)
            os.addTarget(recorderTarget);
    }

    @Override
    public void unregisterRecorder(RecorderConfig config) {
        TimedQueue2<Item> before = recorders.remove(config);

        // If last leaving, remove consumer from OS
        if (recorders.size() == 0 && before != null)
            os.removeTarget(recorderTarget);
    }

    @Override
    public List<RecorderConfig> getRecorders() {
        return ImmutableList.copyOf(recorders.keySet());
    }

    @Override
    public List<String> getRecorderItems(RecorderConfig config) {
        // Get queue
        TimedQueue2<Item> queue = recorders.get(config);

        // If no recorder, error in configuration
        if (queue == null)
            throw new IllegalStateException();

        // Make result list
        List<String> data = Lists.newArrayListWithExpectedSize(queue.items().size());

        // Add all serialized forms
        for (Item item : queue.items())
            data.add(item.toSerializedForm());

        // Return
        return data;
    }

    @Override
    public Bundle getReport() {
        Bundle report = new Bundle();
        report.putString(SPECIAL_KEY_ORIGINATOR, getClass().getSimpleName());

        report.putInt("recorders", recorders.size());

        long maxTimeSpanMs = Long.MIN_VALUE;
        long maxMaximum = Long.MIN_VALUE;
        Set<String> types = Sets.newHashSet();
        for (RecorderConfig config : recorders.keySet()) {
            types.addAll(config.itemTypes);

            maxTimeSpanMs = Math.max(maxTimeSpanMs, config.timeSpanMs);
            maxMaximum = Math.max(maxMaximum, config.maximum);
        }

        String[] typesArray = types.toArray(new String[types.size()]);

        report.putStringArray("types", typesArray);
        report.putLong("maxTimeSpanMs", maxTimeSpanMs);
        report.putLong("maxMaximum", maxMaximum);

        return report;
    }
}
