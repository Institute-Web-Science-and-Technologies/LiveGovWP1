package eu.liveandgov.wp1.shared.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.jeromq.ZMQ;

/**
 * ZmqAppender for Log4j.
 *
 * Publishes log messages on a zmq socket.
 */
public class ZmqAppender extends AppenderSkeleton {

    public static final String DEFAULT_ADDRESS = "tcp://*:50110";

    private ZMQ.Socket outSocket;
    private Layout layout;

    public ZmqAppender(String address, Layout layout) {
        outSocket = ZMQ.context().socket(ZMQ.PUB);
        outSocket.bind(address);
        this.layout = layout;
    }

    public ZmqAppender() {
        this(DEFAULT_ADDRESS, new SimpleLayout());
    }

    @Override
    protected void append(LoggingEvent event) {
        outSocket.send(layout.format(event));
    }

    public void close() {
        outSocket.close();
    }

    public boolean requiresLayout() {
        return true;
    }
}
