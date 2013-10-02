package eu.liveandgov.sensorcollectorv3.Monitor;

import eu.liveandgov.sensorcollectorv3.Connector.ConnectorThread;
import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.sensorcollectorv3.SensorQueue.LinkedSensorQueue;

/**
 * Created by hartmann on 9/22/13.
 */
public class MonitorThread implements Runnable {
    public static long sampleCount = 0;

    private static MonitorThread instance;
    private Thread thread;

    /* Singleton Pattern */
    private MonitorThread(){
        thread = new Thread(this);
    }

    public static MonitorThread getInstance(){
        if (instance == null) instance = new MonitorThread();
        return instance;
    }

    @Override
    public void run() {
        while(true) {

        GlobalContext.sendLog(getLogMessage());


        // sleep 1 sec.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            }
        }
    }

    private String getLogMessage() {
        return "Sample count: " + sampleCount + "\n" +
               "Queue size:   " + LinkedSensorQueue.getSize() + "\n" +
               "File Size:    " + ConnectorThread.getInstance().getSize() + "";
    }

    public void start() {
        thread.start();
    }
}
