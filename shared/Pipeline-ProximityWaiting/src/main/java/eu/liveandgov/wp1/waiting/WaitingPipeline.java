package eu.liveandgov.wp1.waiting;

import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Header;
import eu.liveandgov.wp1.data.implementation.Proximity;
import eu.liveandgov.wp1.data.implementation.Waiting;
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
        if (!key.equals(proximity.data.key)) return;

        // If unordered event, skip (questionable, usually all events ordered in time)
        if (proximity.header.timestamp < lastTime) return;

        // Switch on the new proximity type
        switch (proximity.data.proximityType) {
            // If new proximity is in, old must be replaced
            case IN_PROXIMITY:
                // If old proximity is not null and identites differ, optionally push wating event, else keep old
                if (lastIdentity != null) {
                    if (!lastIdentity.equals(proximity.data.objectIdentifier)) {
                        if (proximity.header.timestamp - lastTime > waitTreshold) {
                            produce(new Waiting(
                                    DataCommons.TYPE_WAITING,
                                    new Header(
                                        lastTime,
                                        proximity.header.device),
                                    new Waiting.WaitingStatus(
                                            key,
                                            proximity.header.timestamp-lastTime,
                                            lastIdentity
                                    )
                            ));
                        }

                        lastTime = proximity.header.timestamp;
                        lastIdentity = proximity.data.objectIdentifier;
                    }
                } else {
                    // If old is null, set this PE to be the next to test
                    lastTime = proximity.header.timestamp;
                    lastIdentity = proximity.data.objectIdentifier;
                }
                break;

            // IF new proximity is not in, check if old one existed and if it surpasses wating treshold
            default:
                if (lastIdentity != null) {
                    if (proximity.header.timestamp - lastTime > waitTreshold) {
                        produce(new Waiting(
                                DataCommons.TYPE_WAITING,
                                new Header(
                                        lastTime,
                                        proximity.header.device),
                                new Waiting.WaitingStatus(
                                        key,
                                        proximity.header.timestamp-lastTime,
                                        lastIdentity
                                )
                        ));
                    }

                    lastTime = Long.MAX_VALUE;
                    lastIdentity = null;
                }
                break;
        }
    }
}
