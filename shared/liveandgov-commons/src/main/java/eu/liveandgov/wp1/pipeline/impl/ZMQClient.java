package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.data.CallbackSet;
import eu.liveandgov.wp1.data.Stoppable;
import eu.liveandgov.wp1.pipeline.Pipeline;
import org.zeromq.ZMQ;

import java.util.concurrent.*;

/**
 * <p>Pipeline element that uses a ZMQ socket for Network transportation</p>
 * Created by Lukas Härtel on 10.02.14.
 */
public abstract class ZMQClient extends Pipeline<String, String> implements Stoppable {
    private static final int BULK_SIZE = 512;

    public static long TAXI_INTERVAL = 5000L;

    public static int HWM = 1000;

    public final ScheduledExecutorService scheduledExecutorService;

    public final long interval;

    public final int mode;

    public final CallbackSet<String> addressUpdated = new CallbackSet<String>();

    public final CallbackSet<Integer> pulled = new CallbackSet<Integer>();

    public final CallbackSet<Boolean> sent = new CallbackSet<Boolean>();

    private ZMQ.Context context;

    private ZMQ.Socket socket;

    private String lastAddress;

    private Future<?> connection;

    private ScheduledFuture<?> taxi;

    private ScheduledFuture<?> responder;

    /**
     * Creates the ZMQ eu.liveandgov.wp1.pipeline element with the given scheduled executor service, a delegator that polls the socket
     * on a regular basis. For this eu.liveandgov.wp1.pipeline element, a socket is created with the given ZMQ mode, which in turn is
     * bound to a given address. Sends are executed on the calling pipeline elements thread.
     *
     * @param scheduledExecutorService
     * @param interval
     * @param mode
     */
    public ZMQClient(final ScheduledExecutorService scheduledExecutorService, final long interval, final int mode) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.interval = interval;
        this.mode = mode;

        connection = scheduledExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                context = ZMQ.context(1);
                socket = context.socket(mode);
                socket.setHWM(HWM);
                socket.connect(lastAddress = getAddress());
                addressUpdated.call(lastAddress);

                taxi = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        final String nextAddress = getAddress();

                        if (!nextAddress.equals(lastAddress)) {
                            socket.disconnect(lastAddress);
                            socket.connect(lastAddress = nextAddress);
                            addressUpdated.call(lastAddress);
                        }
                    }
                }, 0L, TAXI_INTERVAL, TimeUnit.MILLISECONDS);

                responder = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        String item;
                        int i;
                        for (i = 0; i < BULK_SIZE && ((item = socket.recvStr(ZMQ.DONTWAIT)) != null); i++) {
                            produce(item);
                        }
                        pulled.call(i);
                    }
                }, 0L, interval, TimeUnit.MILLISECONDS);
            }
        });
    }

    protected abstract String getAddress();

    @Override
    public void push(final String s) {
        try {
            connection.get();
            sent.call(socket.send(s));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (taxi != null)
            taxi.cancel(true);
        if (responder != null)
            responder.cancel(true);
        if (connection != null)
            connection.cancel(true);
    }
}
