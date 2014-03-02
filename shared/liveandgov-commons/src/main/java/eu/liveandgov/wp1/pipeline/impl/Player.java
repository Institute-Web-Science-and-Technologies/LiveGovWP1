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
    public final ScheduledExecutorService scheduledExecutorService;

    /**
     * The limit of items to store in the queue or -1 for no limit
     */
    public final int limit;

    /**
     * The limit of items simultaneously played back or -1 to play all queued
     */
    public final int playbackLimit;

    /**
     * The rate of playback
     */
    public final long playbackRate;

    /**
     * Event called with false when playback is suspended
     */
    public final CallbackSet<Boolean> suspended = new CallbackSet<Boolean>();

    /**
     * Event called with true when playback is resumed
     */
    public final CallbackSet<Boolean> resumed = new CallbackSet<Boolean>();

    /**
     * All items of this player
     */
    private final ConcurrentLinkedQueue<Item> queue;

    /**
     * The task handling the playback
     */
    private ScheduledFuture<?> task;

    /**
     * Creates a new player with the given parameters
     *
     * @param scheduledExecutorService The executor service used to schedule the playback
     * @param limit                    The limit of items to store in the queue or -1 for no limit
     * @param playbackLimit            The limit of items simultaneously played back or -1 to play all queued
     * @param playbackRate             The rate of playback
     */
    public Player(ScheduledExecutorService scheduledExecutorService, int limit, int playbackLimit, long playbackRate) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.limit = limit;
        this.playbackLimit = playbackLimit;
        this.playbackRate = playbackRate;

        queue = new ConcurrentLinkedQueue<Item>();
    }

    private void resume() {
        task = scheduledExecutorService.scheduleWithFixedDelay(playerJob, 0L, playbackRate, TimeUnit.MILLISECONDS);

        resumed.call(true);
    }

    private void suspend() {
        task.cancel(true);
        task = null;

        suspended.call(false);
    }

    @Override
    public void push(final Item item) {
        if (queue.size() + 1 <= limit || limit == -1) {
            queue.offer(item);
        }

        if (task == null) {
            resume();
        }
    }

    private final Runnable playerJob = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; !queue.isEmpty() && (i < playbackLimit || playbackLimit == -1); i++) {
                produce(queue.poll());
            }

            // If the queue is empty, stop this task if the queue has no more elements
            if (queue.isEmpty()) {
                suspend();
            }
        }
    };


    @Override
    public void stop() {
        suspend();
    }
}