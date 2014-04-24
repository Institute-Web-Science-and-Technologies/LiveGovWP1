package eu.liveandgov.wp1.pipeline.impl;


import eu.liveandgov.wp1.data.Stoppable;
import eu.liveandgov.wp1.pipeline.Pipeline;
import org.zeromq.ZMQ;

import java.util.concurrent.*;

/**
 * <p>Pipeline element that uses a ZMQ socket for Network transportation</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class ZMQServer extends Pipeline<String, String> implements Stoppable {
    private static final int BULK_SIZE = 512;

    public static final int DEFAULT_HWM = 1000;

    public final ScheduledExecutorService scheduledExecutorService;

    public final long interval;

    public final int mode;

    public final String boundAddress;

    private ZMQ.Context context;

    private ZMQ.Socket socket;

    private Future<?> connection;

    private ScheduledFuture<?> responder;

    /**
     * Creates the ZMQ eu.liveandgov.wp1.pipeline element with the given scheduled executor service, a delegator that polls the socket
     * on a regular basis. For this eu.liveandgov.wp1.pipeline element, a socket is created with the given ZMQ mode, which in turn is
     * bound to a given address. Sends are executed on the calling pipeline elements thread.
     *
     * @param scheduledExecutorService
     * @param interval
     * @param mode
     * @param boundAddress
     */
    public ZMQServer(final ScheduledExecutorService scheduledExecutorService, final long interval, final int mode, final String boundAddress) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.interval = interval;
        this.mode = mode;
        this.boundAddress = boundAddress;

        connection = scheduledExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                context = ZMQ.context(1);
                socket = context.socket(mode);

                configure(socket);

                socket.bind(boundAddress);

                responder = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        String item;
                        int i;
                        for (i = 0; i < BULK_SIZE && ((item = socket.recvStr(ZMQ.DONTWAIT)) != null); i++) {
                            produce(item);
                        }
                    }
                }, 0L, interval, TimeUnit.MILLISECONDS);
            }
        });
    }

    /**
     * Configures the socket
     *
     * @param socket The socket to configure
     */
    protected void configure(ZMQ.Socket socket) {
        socket.setHWM(DEFAULT_HWM);
    }

    /**
     * Subscribes to a topic
     *
     * @param topic The string representing the topic
     */
    public void subscribe(final String topic) {
        try {
            connection.get();
            socket.subscribe(topic.getBytes());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Un-subscribes from a topic
     *
     * @param topic The string representing the topic
     */
    public void unsubscribe(final String topic) {
        try {
            connection.get();
            socket.unsubscribe(topic.getBytes());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void push(final String s) {
        try {
            connection.get();
            socket.send(s);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (responder != null)
            responder.cancel(true);
        if (connection != null)
            connection.cancel(true);

        if (socket != null)
            socket.close();

        if (context != null)
            context.term();
    }
}
