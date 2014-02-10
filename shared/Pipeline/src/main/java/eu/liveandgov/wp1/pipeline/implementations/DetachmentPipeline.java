package eu.liveandgov.wp1.pipeline.implementations;

import eu.liveandgov.wp1.pipeline.Pipeline;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * <p>The detachment pipeline element uses a given executor to
 * detach the input items calling thread from the continuation</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class DetachmentPipeline<Item> extends Pipeline<Item, Item> {
    public final Executor executor;

    /**
     * Creates the detachment pipeline element with the given executor
     */
    public DetachmentPipeline(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void push(final Item item) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                produce(item);
            }
        });
    }
}
