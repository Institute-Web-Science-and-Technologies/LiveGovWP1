package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.data.CallbackSet;
import eu.liveandgov.wp1.data.Stoppable;
import eu.liveandgov.wp1.pipeline.Pipeline;

import java.util.concurrent.*;

/**
 * Created by Lukas Härtel on 01.03.14.
 */
public class Player<Item> extends Pipeline<Item, Item> implements Stoppable {
    /**
     * The service executing the playback
     */
    public final ExecutorService executorService;

    /**
     * The limit of items to store in the queue or -1 for no limit
     */
    public final int limit;

    /**
     * All items of this player
     */
    private final BlockingQueue<Item> queue;

    /**
     * The task handling the playback
     */
    private final Future<?> task;

    /**
     * Creates a new player with the given parameters
     *
     * @param executorService The executor service used to run the playback
     * @param limit           The limit of items to store in the queue or -1 for no limit
     */
    public Player(ExecutorService executorService, int limit) {
        this.executorService = executorService;
        this.limit = limit;

        queue = new LinkedBlockingQueue<Item>();
        task = executorService.submit(playerJob);
    }

    @Override
    public void push(final Item item) {
        if (limit == -1 || queue.size() < limit)
            try {
                queue.put(item);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
    }

    private final Runnable playerJob = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    produce(queue.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    };


    @Override
    public void stop() {
        task.cancel(true);
    }
}