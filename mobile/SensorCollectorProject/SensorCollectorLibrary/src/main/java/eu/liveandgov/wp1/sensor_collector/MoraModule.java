package eu.liveandgov.wp1.sensor_collector;

import android.os.Build;

import com.google.common.base.Charsets;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.components.LinkedItemBuffer;
import eu.liveandgov.wp1.sensor_collector.components.ItemBuffer;
import eu.liveandgov.wp1.sensor_collector.config.BasicConfigurator;
import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.fs.FS;
import eu.liveandgov.wp1.sensor_collector.fs.FolderFS;
import eu.liveandgov.wp1.sensor_collector.os.BasicOS;
import eu.liveandgov.wp1.sensor_collector.os.OS;

/**
 * Created by lukashaertel on 08.09.2014.
 */
public class MoraModule extends AbstractModule {
    @Override
    protected void configure() {
        // Basic configuration
        bind(DateFormat.class)
                .toInstance(new SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ssSSS"));
        bind(Charset.class)
                .toInstance(Charsets.UTF_8);
        bind(ScheduledExecutorService.class)
                .toInstance(new ScheduledThreadPoolExecutor(1));

        // Configure config manager
        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.config.configFile"))
                .to("mora/config.dat");

        bind(MoraConfig.class)
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.config.configDefault"))
                .toInstance(new MoraConfig(
                        "http://liveandgov.uni-koblenz.de:8080/UploadServlet/", // Upload
                        "liveandgov.uni-koblenz.de:5555", // Streaming
                        5000, // GPS
                        true, // Velocity of GPS
                        25 * 1000, // Acceleration
                        100 * 1000, // Linear acceleration
                        100 * 1000, // Gravity
                        25 * 1000, // Magnetometer
                        100 * 1000, // Rotation
                        5000, // WiFi
                        5000, // Bluetooth
                        5000, // GSM
                        true // Google Activity
                ));

        bind(Configurator.class)
                .to(BasicConfigurator.class);

        // Configure OS
        bind(OS.class)
                .to(BasicOS.class);


        // Configure FS and its parameters
        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.fs.root"))
                .to("mora/fs");

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.fs.metaextension"))
                .to(".meta");

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.fs.dataextension"))
                .to(".data");

        bind(FS.class)
                .to(FolderFS.class);

        // Configure the connectors
        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.itemBufferLimit"))
                .to(1024);
        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.itemBufferTimeout"))
                .to(100L);
        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.itemBufferTimeoutUnit"))
                .to(TimeUnit.MILLISECONDS);
        bind(ItemBuffer.class)
                .to(LinkedItemBuffer.class);

        // Configure the components
        long motionSensorCorrection = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1
                ? (long) (System.currentTimeMillis() - (System.nanoTime() / 1E6))
                : 0;

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.motionSensorCorrection"))
                .to(motionSensorCorrection);
    }
}
