package eu.liveandgov.wp1.sensor_collector;

import android.util.Log;

import com.google.common.base.Charsets;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
        bind(DateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssSSS"));
        bind(Charset.class).toInstance(Charsets.UTF_8);

        // Configure OS
        bind(OS.class).to(BasicOS.class);

        // Configure FS and its parameters
        bind(String.class).annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.fs.root")).toInstance("mora/fs");
        bind(String.class).annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.fs.metaextension")).toInstance(".meta");
        bind(String.class).annotatedWith(Names.named("eu.liveandgov.wp1.sensor_collector.fs.dataextension")).toInstance(".data");
        bind(FS.class).to(FolderFS.class);

        Log.d("INI", "Configured module");
    }
}
