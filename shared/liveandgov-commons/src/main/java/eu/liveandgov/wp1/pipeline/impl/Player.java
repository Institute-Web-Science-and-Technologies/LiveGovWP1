package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.data.Stoppable;
import eu.liveandgov.wp1.pipeline.Pipeline;

import java.util.concurrent.*;

/**
 * Created by Lukas Härtel on 01.03.14.
 */
public class Player<Item> extends Pipeline<Item, Item> implements Stoppable {
    public final ScheduledExecutorService scheduledExecutorService;

    public final int limit;

    public final int playerLimit;

    public final long playerRate;

    private final ConcurrentLinkedQueue<Item> queue;

    private final ScheduledFuture<?> task;

    /**
     * Creates a new player with the given parameters
     *
     * @param scheduledExecutorService The executor service used to schedule the playback
     * @param limit                    The limit of items to store in the queue or -1 for no limit
     * @param playerLimit              The limit of items simultaneously played back or -1 to play all queued
     * @param playerRate               The rate of playback
     */
    public Player(ScheduledExecutorService scheduledExecutorService, int limit, int playerLimit, long playerRate) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.limit = limit;
        this.playerLimit = playerLimit;
        this.playerRate = playerRate;

        queue = new ConcurrentLinkedQueue<Item>();
        task = scheduledExecutorService.scheduleWithFixedDelay(playerJob, 0L, playerRate, TimeUnit.MILLISECONDS);
    }

    @Override
    public void push(final Item item) {
        if (queue.size() + 1 <= limit || limit == -1) {
            queue.offer(item);
        }
    }

    private final Runnable playerJob = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; !queue.isEmpty() && (i < playerLimit || playerLimit == -1); i++) {
                produce(queue.poll());
            }
        }
    };

    @Override
    public void stop() {
        task.cancel(true);
    }
}