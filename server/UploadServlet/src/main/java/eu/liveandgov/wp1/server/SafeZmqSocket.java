package eu.liveandgov.wp1.server;

import org.jeromq.ZMQ;

/**
 * User: hartmann
 * Date: 10/20/13
 */
public class SafeZmqSocket {
    private ZMQ.Socket s;

    public SafeZmqSocket(int type) {
        s = ZMQ.context().socket(type);
    }

    public synchronized final int bind(String addr){
        return s.bind(addr);
    }

    public synchronized final boolean connect(String addr){
        return s.connect(addr);
    }

    public synchronized boolean send(String message) {
        return s.send(message);
    }
}
