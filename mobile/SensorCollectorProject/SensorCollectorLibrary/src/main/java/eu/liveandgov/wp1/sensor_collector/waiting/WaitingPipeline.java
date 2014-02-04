package eu.liveandgov.wp1.sensor_collector.waiting;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Pipeline;
import eu.liveandgov.wp1.sensor_collector.monitor.Monitorable;
import eu.liveandgov.wp1.sensor_collector.pps.ProximityEvent;

/**
 * Created by lukashaertel on 04.02.14.
 */
public class WaitingPipeline extends Pipeline<ProximityEvent, WaitingEvent> implements Monitorable {
    private static final String LOG_TAG = "WPL";

    private final String key;

    private final long waitTreshold;

    private long lastTime;

    private String lastIdentity;

    public WaitingPipeline(String key, long waitTreshold) {
        this.key = key;
        this.waitTreshold = waitTreshold;
    }

    @Override
    public void push(ProximityEvent proximityEvent) {
        // If not a proximity for this kind of waiting event, skip
        if (!key.equals(proximityEvent.key)) return;

        // If unordered event, skip (questionable, usually all events ordered in time)
        if (proximityEvent.time < proximityEvent.time) return;

        // Switch on the new proximity type
        switch (proximityEvent.proximity.getProximityType()) {
            // If new proximity is in, old must be replaced
            case IN_PROXIMITY:
                // If old proximity is not null and identites differ, optionally push wating event, else keep old
                if (lastIdentity != null) {
                    if (!lastIdentity.equals(proximityEvent.proximity.getObjectIdentity())) {
                        if (proximityEvent.time - lastTime > waitTreshold) {
                            if (consumer != null)
                                consumer.push(new WaitingEvent(lastTime, key, proximityEvent.time - lastTime, lastIdentity));
                        }

                        lastTime = proximityEvent.time;
                        lastIdentity = proximityEvent.proximity.getObjectIdentity();
                    }
                } else {
                    // If old is null, set this PE to be the next to test
                    lastTime = proximityEvent.time;
                    lastIdentity = proximityEvent.proximity.getObjectIdentity();
                }
                break;

            // IF new proximity is not in, check if old one existed and if it surpasses wating treshold
            default:
                if (lastIdentity != null) {
                    if (proximityEvent.time - lastTime > waitTreshold) {
                        if (consumer != null)
                            consumer.push(new WaitingEvent(lastTime, key, proximityEvent.time - lastTime, lastIdentity));
                    }

                    lastTime = Long.MAX_VALUE;
                    lastIdentity = null;
                }
                break;
        }
    }

    @Override
    public String getStatus() {
        if (lastIdentity == null) {
            return "No waiting in check";
        } else {
            return "Waiting at " + lastIdentity + " in check, since " + (System.currentTimeMillis() - lastTime) / 1000 + " seconds";
        }
    }
}
