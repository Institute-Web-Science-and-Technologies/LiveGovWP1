package eu.liveandgov.sensorcollectorv3.sensor_queue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import eu.liveandgov.sensorcollectorv3.monitor.MonitorThread;

/**
 * Created by hartmann on 9/29/13.
 */
public class LinkedSensorQueue implements SensorQueue {
    public  static final int capacity = 1000;

    private int size = 0;
    public  final Queue<String> Q = new ConcurrentLinkedQueue<String>();

    public void push(String m){
        if (size++ < capacity) {
            Q.add(m);
            MonitorThread.sampleCount++;
        }
    }

    public String pull(){
        size = Math.max(size - 1, 0);
        return Q.poll();
    }

    public String blockingPull(){
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

    public int size() {
        return size;
    }

    @Override
    public String getStatus() {
        return "Queue Size: " + Q.size();
    }
}
