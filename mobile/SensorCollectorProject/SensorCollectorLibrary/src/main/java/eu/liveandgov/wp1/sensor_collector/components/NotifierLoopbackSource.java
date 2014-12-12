package eu.liveandgov.wp1.sensor_collector.components;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import eu.liveandgov.wp1.sensor_collector.os.SampleSource;

/**
 * <p>
 * This is not a sample source but behaves as a listener for source activation and deactivation
 * </p>
 * <p>
 * Created on 09.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
@Singleton
public class NotifierLoopBackSource implements SampleSource {
    @Inject
    Context context;

    @Inject
    NotificationManager notificationManager;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.notifierTitle")
    String notifierTitle;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.notifierText")
    String notifierText;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.notifierColor")
    int notifierColor;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.notifierIcon")
    int notifierIcon;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.notifierOnMs")
    int notifierOnMs;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.components.notifierOffMs")
    int notifierOffMs;

    private final int notifierId;

    boolean active;

    public NotifierLoopBackSource() {
        notifierId = Float.floatToIntBits((float) Math.random());
        active = false;
    }

    @Override
    public void activate() {
        if (active)
            return;

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(notifierTitle)
                .setContentText(notifierText)
                .setSmallIcon(notifierIcon)
                .setLights(notifierColor, notifierOnMs, notifierOffMs)
                .setOngoing(true)
                .setProgress(0, 0, true)
                .build();

        notificationManager.notify(notifierId, notification);

        active = true;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void deactivate() {
        if (!active)
            return;

        notificationManager.cancel(notifierId);

        active = false;
    }
}
