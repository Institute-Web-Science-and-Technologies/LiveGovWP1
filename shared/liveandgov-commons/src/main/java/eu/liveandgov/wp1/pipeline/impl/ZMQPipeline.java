package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;

/**
 * Created by Lukas Härtel on 10.02.14.
 */
public abstract class ZMQPipeline extends Pipeline<String, String> {
    public static int HWM = 1000;
}
