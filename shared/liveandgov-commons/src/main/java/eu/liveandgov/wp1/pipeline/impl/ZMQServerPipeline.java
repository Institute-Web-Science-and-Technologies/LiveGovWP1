package eu.liveandgov.wp1.pipeline.impl;


import org.zeromq.ZMQ;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Pipeline element that uses a ZMQ socket for Network transportation</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public class ZMQServerPipeline extends ZMQPipeline {

    public final ScheduledExecutorService scheduledExecutorService;

    public final long interval;

    public final int mode;

    public final String boundAddress;

    private final ZMQ.Context context;

    private final ZMQ.Socket socket;

    /**
     * Creates the ZMQ eu.liveandgov.wp1.pipeline element with the given scheduled executor service, a delegator that polls the socket
     * on a regular basis. For this eu.liveandgov.wp1.pipeline element, a socket is created with the given ZMQ mode, which in turn is
     * bound to a given address
     *
     * @param scheduledExecutorService
     * @param interval
     * @param mode
     * @param boundAddress
     */
    public ZMQServerPipeline(ScheduledExecutorService scheduledExecutorService, long interval, int mode, String boundAddress) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.interval = interval;
        this.mode = mode;
        this.boundAddress = boundAddress;

        context = ZMQ.context(1);
        socket = context.socket(mode);
        socket.setHWM(HWM);
        socket.bind(boundAddress);

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String item;
                while ((item = socket.recvStr(ZMQ.DONTWAIT)) != null) {
                    produce(item);
                }
            }
        }, 0L, interval, TimeUnit.MILLISECONDS);
    }

    @Override
    public void push(final String s) {
        scheduledExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                socket.send(s);
            }
        });
    }
}
