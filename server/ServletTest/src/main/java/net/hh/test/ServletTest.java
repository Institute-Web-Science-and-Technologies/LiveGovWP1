package net.hh.test;

import org.apache.log4j.Logger;
import org.jeromq.ZMQ;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: hartmann
 * Date: 12/5/13
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServletTest extends HttpServlet {

    // public static final String ZMQ_ADDRESS = "ipc:///tmp/feed_0";
    public static final String ZMQ_ADDRESS = "tcp://*:51000";

    private static Logger Log = Logger.getLogger(ServletTest.class);

    private ZMQ.Socket zmqOut;

    public static Lock lock = new ReentrantLock();

    static {
        Log.debug("Lifecycle: static {} executed.");
    }

    public ServletTest(){
        Log.debug("Lifecycle: Constructor called.");
    }

    public void init(ServletConfig config) throws ServletException {
        Log.debug("Lifecycle: init() called.");
        connectZmq();
    }

    public void destroy() {
        Log.debug("Lifecycle: destroy() called.");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Log.debug("Lifecycle: doGet() called.");
        sendMessageSync("GET " + req.getRequestURI());
        resp.getWriter().write("Hello from ServletTest");
        resp.getWriter().close();
    }


    private void connectZmq() {
        Log.info("Binding PUSH socket on " + ZMQ_ADDRESS);
        // INIT ZMQ Socket
        zmqOut = ZMQ.context().socket(ZMQ.PUB);
        zmqOut.bind(ZMQ_ADDRESS);

        // small sleep to give subscribers time to connect back
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendMessageSync("Hello from UploadServlet");
    }

    public void sendMessageSync(String m) {
        try {
            // Wait for lock
            if (lock.tryLock(100, TimeUnit.MICROSECONDS)) {
                Log.info("Sending ZMQ message: " + m);
                zmqOut.send(m);
                lock.unlock();
            } else {
                Log.warn("Could not send message. " + m);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
