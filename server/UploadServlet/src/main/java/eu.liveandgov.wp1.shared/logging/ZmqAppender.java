package eu.liveandgov.wp1.shared.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.jeromq.ZMQ;

/**
 * ZmqAppender for Log4j.
 *
 * Publishes log messages on a zmq socket.
 */
public class ZmqAppender extends AppenderSkeleton {

    private Logger LogLog = Logger.getLogger(ZmqAppender.class);

    public static final String DEFAULT_ADDRESS = "tcp://*:50110";

    static private ZMQ.Socket outSocket = null;

    private Layout layout;

    public ZmqAppender(String address, Layout layout) {
        if (outSocket != null) return;
        LogLog.info("Bindindg ZMQ Logging PUB Socket on " + address);
        outSocket = ZMQ.context().socket(ZMQ.PUB);
        outSocket.bind(address);
        this.layout = layout;
    }

    public ZmqAppender() {
        this(DEFAULT_ADDRESS, new SimpleLayout());
    }

    @Override
    protected synchronized void append(LoggingEvent event) {
        System.out.println("Senging out ZMQ message.");
        outSocket.send(layout.format(event));
    }

    public void close() {
        System.out.println("Closing ZMQ socket.");
        outSocket.close();
        outSocket = null;
    }

    public boolean requiresLayout() {
        return true;
    }
}
