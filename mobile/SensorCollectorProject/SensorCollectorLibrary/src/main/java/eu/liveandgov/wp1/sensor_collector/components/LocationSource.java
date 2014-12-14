package eu.liveandgov.wp1.sensor_collector.components;

import com.google.inject.ProvidedBy;

import eu.liveandgov.wp1.sensor_collector.os.Reporter;
import eu.liveandgov.wp1.sensor_collector.os.SampleSource;

/**
 * <p>Intermediate interface for locations sources</p>
 * Created by lukashaertel on 05.12.2014.
 */
@ProvidedBy(LocationSourceProvider.class)
public interface LocationSource extends SampleSource, Reporter {
}
