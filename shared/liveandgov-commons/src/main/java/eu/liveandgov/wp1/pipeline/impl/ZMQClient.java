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
    /**
     * Maximum number of elements to receive in one pull
     */
    private static final int BULK_SIZE = 512;

    /**
     * Time between checking if a new address is desired
     */
    public static long TAXI_INTERVAL = 2000L;

    /**
     * Default high water mark of the ZMQ socket
     */
    public static final int DEFAULT_HWM = 1000;

    /**
     * Executor service to schedule the tasks on
     */
    public final ScheduledExecutorService scheduledExecutorService;

    /**
     * Receive interval
     */
    public final long interval;

    /**
     * ZMQ mode
     */
    public final int mode;

    /**
     * Callback called when the address has updated
     */
    public final CallbackSet<String> addressUpdated = new CallbackSet<String>();

    /**
     * ZMQ context
     */
    private ZMQ.Context context;

    /**
     * ZMQ socket
     */
    private ZMQ.Socket socket;

    /**
     * Last connected address
     */
    private String lastAddress;

    /**
     * Future representing the completion of the connection method
     */
    private Future<?> connection;

    /**
     * Scheduled future representing the continuous address update
     */
    private ScheduledFuture<?> taxi;

    /**
     * Scheduled future representing the continuous response task
     */
    private ScheduledFuture<?> responder;

    /**
     * <p> Creates the ZMQ pipeline element with the given scheduled executor service, a delegator that polls the socket on a regular basis. </p>
     * <p>For this pipeline element, a socket is created with the given ZMQ mode, which in turn is bound to a given address. Sends are executed on the calling pipeline elements thread.</p>
     *
     * @param scheduledExecutorService Executor service to schedule the tasks on
     * @param interval                 Receive interval
     * @param mode                     ZMQ mode
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

                configure(socket);

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
     * Gets the address the client should connect to, may change to represent a new target
     *
     * @return The current target address
     */
    protected abstract String getAddress();

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
        if (taxi != null)
            taxi.cancel(true);
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
