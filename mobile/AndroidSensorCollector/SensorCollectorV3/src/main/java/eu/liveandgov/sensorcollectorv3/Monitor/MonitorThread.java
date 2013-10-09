package eu.liveandgov.sensorcollectorv3.Monitor;

import android.provider.Settings;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.microedition.khronos.opengles.GL;

import eu.liveandgov.sensorcollectorv3.Connector.ConnectorThread;
import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.sensorcollectorv3.SensorQueue.LinkedSensorQueue;
import eu.liveandgov.sensorcollectorv3.ServiceSensorControl;

/**
 * Created by hartmann on 9/22/13.
 */
public class MonitorThread implements Runnable {
    public static long sampleCount = 0;

    private static MonitorThread instance;
    private Thread thread;

    private Set<MonitorItem> observables = new HashSet<MonitorItem>();

    public MonitorThread(){
        thread = new Thread(this);
    }

    public void registerMonitorable(Monitorable m, String name){
        observables.add(new MonitorItem(m, name));
    }

    public void start() {
        thread.start();
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
        StringBuilder s = new StringBuilder();
        for (MonitorItem m : observables){
            s.append(m.render());
        }
        s.append("Androdi ID: " + GlobalContext.androidId);
        return s.toString();
    }

    private class MonitorItem {
        public Monitorable monitorable;
        public String name;
        public MonitorItem(Monitorable m, String n){
            this.monitorable = m;
            this.name = n;
        }

        public String render() {
            return name + ": " + monitorable.getStatus() + "\n";
        }
    }

}
