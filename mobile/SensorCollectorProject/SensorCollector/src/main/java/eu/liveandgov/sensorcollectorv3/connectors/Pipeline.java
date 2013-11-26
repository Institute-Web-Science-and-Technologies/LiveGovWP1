package eu.liveandgov.sensorcollectorv3.connectors;

import eu.liveandgov.wp1.feature_pipeline.connectors.Consumer;
import eu.liveandgov.wp1.feature_pipeline.connectors.Producer;

/**
 * Abstract class providing Producer and Consumer methods
 *
 * Created by hartmann on 10/25/13.
 */
public abstract class Pipeline<S,T> extends Producer<T> implements Consumer<S> { }
