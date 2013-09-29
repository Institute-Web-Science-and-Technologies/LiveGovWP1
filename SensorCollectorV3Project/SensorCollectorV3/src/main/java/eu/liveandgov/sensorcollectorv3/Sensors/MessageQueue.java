package eu.liveandgov.sensorcollectorv3.Sensors;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import eu.liveandgov.sensorcollectorv3.Monitor.MonitorThread;

/**
 * Created by hartmann on 9/29/13.
 */
public class MessageQueue {

    public  static final int capacity = 1000;
    private static int size = 0;
    public  static final Queue<String> Q = new ConcurrentLinkedQueue<String>();

    /**
     * Push message m to the queue.
     * Drop m if the queue is full.
     *
     * @param m
     */
    public static void push(String m){
        if (size++ < capacity) {
            Q.add(m);
            MonitorThread.sampleCount++;
        }
    }

    /**
     * Pull message from queue.
     * Return null if queue is empty.
     * @return m
     */
    public static String pull(){
        size = Math.max(size - 1, 0);
        return Q.poll();
    }

    public static String blockingPull(){
        String m;

        while (true) {
            m = pull();

            if (m != null) break;

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return m;
    }

    public static int getSize() {
        return size;
    }
}
