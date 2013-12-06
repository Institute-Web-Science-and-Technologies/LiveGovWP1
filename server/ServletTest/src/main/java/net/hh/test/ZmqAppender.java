package net.hh.test;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.jeromq.ZMQ;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/6/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ZmqAppender extends AppenderSkeleton {

    private ZMQ.Socket outSocket;
    private Layout layout;

    public ZmqAppender(String address, Layout layout) {
        outSocket = ZMQ.context().socket(ZMQ.PUB);
        outSocket.bind(address);
        this.layout = layout;
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
