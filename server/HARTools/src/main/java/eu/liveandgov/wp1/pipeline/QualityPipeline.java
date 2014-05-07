package eu.liveandgov.wp1.pipeline;


import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.Window;
import eu.liveandgov.wp1.pipeline.Pipeline;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: cehlen
 * Date: 06/01/14
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class QualityPipeline extends Pipeline<Tuple<Long, Window>, Tuple<Long, Window>> {

    private double minFreq;

    public QualityPipeline(double minFreq) {
        this.minFreq = minFreq;
    }

    @Override
    public void push(Tuple<Long, Window> longWindowTuple) {
        Window w = longWindowTuple.right;
        double windowFreq = (double)w.x.length / ((w.endTime - w.startTime) / 1000.0);

        // Check if the frequency matches our desired frame
        if (windowFreq < this.minFreq) {
//            logger.info("< Frequency: " + windowFreq + " StartTime: " + tw.startTime + " EndTime: " + tw.endTime + " Samples: " + tw.x.length);
            return;
        }

        produce(longWindowTuple);
    }
}