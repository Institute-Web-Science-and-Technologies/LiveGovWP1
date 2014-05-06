package eu.liveandgov.wp1.sensor_collector.monitor;

import java.util.HashSet;
import java.util.Set;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;

/**
 * Monitors objects that implement the {@link eu.liveandgov.wp1.sensor_collector.monitor.Monitorable}
 * interface.
 *
 * Each object is periodically polled for status updates. These updates are distributed
 * using the GlobalContext.sendLog(String) method.
 *
 * Created by hartmann on 9/22/13.
 */
public class MonitorThread implements Runnable {
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
        s.append("User ID: " + GlobalContext.getUserId());
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
