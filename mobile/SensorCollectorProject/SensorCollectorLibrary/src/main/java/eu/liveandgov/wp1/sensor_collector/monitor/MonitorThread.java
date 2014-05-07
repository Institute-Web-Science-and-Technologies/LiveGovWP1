package eu.liveandgov.wp1.sensor_collector.monitor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.data.Startable;
import eu.liveandgov.wp1.data.Stoppable;
import eu.liveandgov.wp1.sensor_collector.GlobalContext;
import eu.liveandgov.wp1.sensor_collector.configuration.SensorCollectionOptions;
import eu.liveandgov.wp1.util.LocalBuilder;

/**
 * Monitors objects that implement the {@link eu.liveandgov.wp1.sensor_collector.monitor.Monitorable}
 * interface.
 * <p/>
 * Each object is periodically polled for status updates. These updates are distributed
 * using the GlobalContext.sendLog(String) method.
 * <p/>
 * Created by hartmann on 9/22/13.
 */
public class MonitorThread implements Startable, Stoppable {
    private Set<MonitorItem> observables = new HashSet<MonitorItem>();

    private ScheduledFuture<?> reportTask = null;

    public MonitorThread() {

    }

    private final Runnable reportMethod = new Runnable() {
        @Override
        public void run() {
            GlobalContext.sendLog(getLogMessage());
        }
    };

    public void registerMonitorable(Monitorable m, String name) {
        observables.add(new MonitorItem(m, name));
    }

    @Override
    public void start() {
        if (reportTask == null) {
            reportTask = GlobalContext.getExecutorService().scheduleAtFixedRate(reportMethod, 0L, SensorCollectionOptions.MONITORING_RATE, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stop() {
        if (reportTask != null) {
            reportTask.cancel(true);
            reportTask = null;
        }
    }

    private String getLogMessage() {
        final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
        for (MonitorItem m : observables) {
            stringBuilder.append(m.render());
        }

        stringBuilder.append("User ID: ");
        stringBuilder.append(GlobalContext.getUserId());

        return stringBuilder.toString();
    }

    private class MonitorItem {
        public Monitorable monitorable;
        public String name;

        public MonitorItem(Monitorable m, String n) {
            this.monitorable = m;
            this.name = n;
        }

        public String render() {
            final StringBuilder stringBuilder = LocalBuilder.acquireBuilder();
            stringBuilder.append(name);
            stringBuilder.append(": ");
            stringBuilder.append(monitorable.getStatus());
            stringBuilder.append("\r\n");

            return stringBuilder.toString();
        }
    }

}
