package eu.liveandgov.wp1.sensor_collector;

import android.graphics.Color;
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
import eu.liveandgov.wp1.sensor_collector.rec.BasicRecorder;
import eu.liveandgov.wp1.sensor_collector.rec.Recorder;
import eu.liveandgov.wp1.sensor_collector.serial.ItemSerializer;
import eu.liveandgov.wp1.sensor_collector.serial.JSONSerializer;
import eu.liveandgov.wp1.sensor_collector.transfer.PostTransferExecutor;
import eu.liveandgov.wp1.sensor_collector.transfer.TransferExecutor;

/**
 * <p>
 * This modules configures all bindings necessary to run the MORA service
 * </p>
 * Created by lukashaertel on 08.09.2014.
 */
public class MoraModule extends AbstractModule {
    @Override
    protected void configure() {
        configureBasics();
        configureConfigManager();
        configureFS();
        configureOS();
        configureRecorder();
        configureStrategies();
        configureConnectors();
        configureComponents();
    }

    /**
     * <p>
     * Basics contain regular formats, common char sets and a shared executor allowing thread time
     * out
     * </p>
     */
    private void configureBasics() {
        // Basic configuration
        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.logfileName"))
                .to("mora");

        bind(DateFormat.class)
                .toInstance(new SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ssSSS"));
        bind(Charset.class)
                .toInstance(Charsets.UTF_8);

        // Make executor and configure accordingly
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(3);

        // If feature is available, enable core thread timeout with five seconds
        if (Build.VERSION.SDK_INT >= 9) {
            scheduledThreadPoolExecutor.setKeepAliveTime(5, TimeUnit.SECONDS);
            scheduledThreadPoolExecutor.allowCoreThreadTimeOut(true);
        }

        bind(ScheduledExecutorService.class)
                .toInstance(scheduledThreadPoolExecutor);
    }

    /**
     * <p>
     * The config manager stores, maintains and distributes the config, config defaults are listed here
     * </p>
     */
    private void configureConfigManager() {
        // Configure config manager
        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.config.configFile"))
                .to("mora/config.dat");

        bind(MoraConfig.class)
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.config.configDefault"))
                .toInstance(new MoraConfig(
                        "user", // User identity
                        5, // Secret length
                        "http://liveandgov.uni-koblenz.de/storage/upload/", // Upload
                        false, // Upload compressed
                        "tcp://liveandgov.uni-koblenz.de:5555", // Streaming
                        5000, // GPS
                        true, // Velocity of GPS
                        25 * 1000, // Acceleration
                        null, // Linear acceleration
                        null, // Gravity
                        25 * 1000, // Magnetometer
                        null, // Rotation
                        null, // WiFi
                        null, // Bluetooth
                        null, // GSM
                        20 * 1000, // Google Activity
                        true
                ));

        bind(Configurator.class)
                .to(BasicConfigurator.class);
    }

    /**
     * <p>
     * The file system stores, lists and accesses the trips
     * </p>
     */
    private void configureFS() {
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

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.fs.compressed"))
                .to(true);

        bind(FS.class)
                .to(FolderFS.class);
    }

    /**
     * <p>
     * The operating system maintains activation and deactivation of sample source as well as
     * report generation and item distribution
     * </p>
     */
    private void configureOS() {
        // Configure OS
        bind(OS.class)
                .to(BasicOS.class);
    }

    /**
     * <p>
     * The recorder generates sample bits for clients
     * </p>
     */
    private void configureRecorder() {
        // Configure recorder
        bind(Recorder.class)
                .to(BasicRecorder.class);
    }

    /**
     * <p>
     * The strategies describe implementations of abstract methods
     * </p>
     */
    private void configureStrategies() {
        // Configure the strategies
        bind(ItemSerializer.class)
                .to(JSONSerializer.class);

        bind(TransferExecutor.class)
                .to(PostTransferExecutor.class);
    }

    /**
     * <p>
     * The connector commutes generated samples in a thread safe fashion
     * </p>
     */
    private void configureConnectors() {
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
    }

    /**
     * <p>The components implement sample receiving and generation</p>
     */
    private void configureComponents() {
        // Configure the components
        configureNotifierLoopBack();
        configureSensors();
    }


    /**
     * <p>
     * The notifier loop-back uses the sample source activation policies to notify a user of recorded
     * samples
     * </p>
     */
    private void configureNotifierLoopBack() {
        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.notifierTitle"))
                .to("MORA Collector");

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.notifierText"))
                .to("Reading sensor values");

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.notifierColor"))
                .to(Color.RED);

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.notifierIcon"))
                .to(R.drawable.ic_launcher);

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.notifierOnMs"))
                .to(900);

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.notifierOffMs"))
                .to(900);
    }

    /**
     * <p>
     * The sensors access the hardware sensors of the device
     * </p>
     */
    private void configureSensors() {
        long motionSensorCorrection = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1
                ? (long) (System.currentTimeMillis() - (System.nanoTime() / 1E6))
                : 0;

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.motionSensorCorrection"))
                .to(motionSensorCorrection);

        bindConstant()
                .annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.components.harWindowLength"))
                .to(1000);
    }

}
