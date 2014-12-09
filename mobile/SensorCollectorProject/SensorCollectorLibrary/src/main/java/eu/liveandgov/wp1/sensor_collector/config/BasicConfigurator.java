package eu.liveandgov.wp1.sensor_collector.config;

import android.content.Context;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.util.MoraIO;

/**
 * Created by lukashaertel on 17.11.2014.
 */
@Singleton
public class BasicConfigurator implements Configurator {
    /**
     * <p>Android context, needed for root file resolution</p>
     */
    @Inject
    Context context;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.config.configFile")
    String configFile;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.config.configDefault")
    MoraConfig configDefault;

    private Set<ConfigListener> listeners = Sets.newLinkedHashSet();

    private MoraConfig currentConfig = null;

    /**
     * <p>Obtains the file under which all MORA file system entries are listed</p>
     *
     * @return Returns a new file
     */
    private File getConfigFile() {
        return new File(context.getFilesDir(), configFile);
    }


    @Override
    public void initListener(ConfigListener listener, boolean initialize) {
        if (listeners.add(listener) && initialize)
            listener.updated(null, getConfig());
    }

    @Override
    public MoraConfig getConfig() {
        return currentConfig == null ? configDefault : currentConfig;
    }

    @Override
    public void setConfig(MoraConfig config) {
        if (!Objects.equal(getConfig(), config)) {
            MoraConfig was = getConfig();
            currentConfig = config;

            for (ConfigListener listener : listeners)
                listener.updated(was, getConfig());
        }
    }

    @Override
    public void resetConfig() {
        setConfig(configDefault);
    }

    @Override
    public void loadConfig() throws IOException {
        setConfig(MoraIO.getContent(getConfigFile(), configDefault));
    }

    @Override
    public void storeConfig() throws IOException {
        MoraIO.setContent(getConfig(), getConfigFile());
    }
}
