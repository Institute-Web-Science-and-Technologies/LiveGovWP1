package eu.liveandgov.wp1.sensor_collector.components;

import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import eu.liveandgov.wp1.sensor_collector.config.Configurator;
import eu.liveandgov.wp1.sensor_collector.logging.LogPrincipal;
import eu.liveandgov.wp1.sensor_collector.util.MoraConstants;

/**
 * <p>
 * Provides the location source by Google Play Service lookup and if that fails with a subsequent
 * basic Android fallback provider.
 * </p>
 * Created by lukashaertel on 05.12.2014.
 */
@Singleton
public class LocationSourceProvider implements Provider<LocationSource> {
    private static final Logger log = LogPrincipal.get();

    @Inject
    Context context;

    @Inject
    LocationManager locationManager;

    /**
     * <p>The configuration handler</p>
     */
    @Inject
    Configurator configurator;

    /**
     * <p>Central credentials store</p>
     */
    @Inject
    Credentials credentials;

    /**
     * <p>Central item buffer</p>
     */
    @Inject
    ItemBuffer itemBuffer;

    /**
     * Acquires the location source
     *
     * @return Returns the appropriate location source
     */
    @Override
    public LocationSource get() {
        // Check if play services are available
        int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        // If so, return the play services provider
        if (code == ConnectionResult.SUCCESS)
            return new PlayServicesLocationSource(configurator, credentials, itemBuffer, context);

        // Else, log failure
        log.info("Google location services is not available, status: "
                + MoraConstants.toConnectionResultString(code) + ", falling back to android");

        // Return Android provider
        return new AndroidLocationSource(configurator, credentials, itemBuffer, locationManager, context);
    }
}
