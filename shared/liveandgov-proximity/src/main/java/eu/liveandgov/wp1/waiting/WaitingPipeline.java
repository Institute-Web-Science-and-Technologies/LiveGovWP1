package eu.liveandgov.wp1.waiting;

import eu.liveandgov.wp1.data.annotations.Unit;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.data.impl.Waiting;
import eu.liveandgov.wp1.pipeline.Pipeline;

/**
 * <p>Pipeline that takes proximities and tries find longer sequences of continuous proximity to determine if the source stream is waiting at a given object</p>
 * Created by Lukas Härtel on 04.02.14.
 */
public class WaitingPipeline extends Pipeline<Proximity, Waiting> {
    /**
     * Key of the items to receive
     */
    public final String key;

    /**
     * Threshold in millisecond that is seen as minimum waiting time
     */
    @Unit("ms")
    public final long waitThreshold;

    /**
     * The base time to compare to
     */
    private long lastTime;

    /**
     * The last object identity
     */
    private String lastIdentity;

    /**
     * Creates a new instance with the given values
     *
     * @param key           Key of the items to receive
     * @param waitThreshold Threshold in millisecond that is seen as minimum waiting time
     */
    public WaitingPipeline(String key, @Unit("ms") long waitThreshold) {
        this.key = key;
        this.waitThreshold = waitThreshold;
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
                    if (proximity.getTimestamp() - lastTime > waitThreshold) {
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
                if (proximity.getTimestamp() - lastTime > waitThreshold) {
                    produce(new Waiting(lastTime, proximity.getDevice(), key, proximity.getTimestamp() - lastTime, lastIdentity));
                }

                lastTime = Long.MAX_VALUE;
                lastIdentity = null;
            }
        }
    }
}
