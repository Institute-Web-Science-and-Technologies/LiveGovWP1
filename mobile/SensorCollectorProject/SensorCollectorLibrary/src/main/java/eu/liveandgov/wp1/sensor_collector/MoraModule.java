package eu.liveandgov.wp1.sensor_collector;

import com.google.common.base.Charsets;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
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
                .toInstance(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssSSS"));
        bind(Charset.class)
                .toInstance(Charsets.UTF_8);
        bind(ScheduledExecutorService.class)
                .toInstance(new ScheduledThreadPoolExecutor(1));

        // Configure config manager
        bind(String.class)
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.config.configFile"))
                .toInstance("mora/config.dat");

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
        bind(String.class)
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.fs.root"))
                .toInstance("mora/fs");

        bind(String.class)
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.fs.metaextension"))
                .toInstance(".meta");

        bind(String.class)
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.fs.dataextension"))
                .toInstance(".data");

        bind(FS.class)
                .to(FolderFS.class);
    }
}
