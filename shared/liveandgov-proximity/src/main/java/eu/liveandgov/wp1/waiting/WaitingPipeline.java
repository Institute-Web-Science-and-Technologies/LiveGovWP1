package eu.liveandgov.wp1.waiting;

import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.data.impl.Waiting;
import eu.liveandgov.wp1.pipeline.Pipeline;

/**
 * Created by lukashaertel on 04.02.14.
 */
public class WaitingPipeline extends Pipeline<Proximity, Waiting> {
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
    public void push(Proximity proximity) {
        // If not a proximity for this kind of waiting event, skip
        if (!key.equals(proximity.key)) return;

        // If unordered event, skip (questionable, usually all events ordered in time)
        if (proximity.getTimestamp() < lastTime) return;

        if (proximity.in) {
            if (lastIdentity != null) {
                if (!lastIdentity.equals(proximity.of)) {
                    if (proximity.getTimestamp() - lastTime > waitTreshold) {
                        produce(new Waiting(lastTime, proximity.getDevice(), key, proximity.getTimestamp() - lastTime, lastIdentity));
                    }

                    lastTime = proximity.getTimestamp();
                    lastIdentity = proximity.of;
                }
            } else {
                // If old is null, set this PE to be the next to test
                lastTime = proximity.getTimestamp();
                lastIdentity = proximity.of;
            }
        } else {
            if (lastIdentity != null) {
                if (proximity.getTimestamp() - lastTime > waitTreshold) {
                    produce(new Waiting(lastTime, proximity.getDevice(), key, proximity.getTimestamp() - lastTime, lastIdentity));
                }

                lastTime = Long.MAX_VALUE;
                lastIdentity = null;
            }
        }
    }
}
