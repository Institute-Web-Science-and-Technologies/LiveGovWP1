package eu.liveandgov.wp1;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.data.Triple;
import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.impl.Acceleration;
import eu.liveandgov.wp1.pipeline.*;

/**
 * Created by cehlen on 07/03/14.
 */
public class HARPipeline extends Pipeline<Acceleration, Triple<Long, Long, String>> {

    private WindowPipeline wp;
    private QualityPipeline qp;
    private InterpolationPipeline ip;
    private FeaturePipeline fp;
    private ActivityPipeline ap;

    private HARPipeline () {
        int WINDOW_LENGTH_MS = 5 * 1000;
        int WINDOW_OVERLAP   = WINDOW_LENGTH_MS - 100;

        int SAMPLING_FREQUENCY_HZ = 50;
        int MIN_RECORDING_FREQUENCY = 40;

        double SAMPLE_LENGHT_MS = 1000D / SAMPLING_FREQUENCY_HZ;
        int NO_SAMPLES_PER_WINDOW = (int) (WINDOW_LENGTH_MS / SAMPLE_LENGHT_MS);

        wp = new WindowPipeline(WINDOW_LENGTH_MS, WINDOW_OVERLAP);

        qp = new QualityPipeline(MIN_RECORDING_FREQUENCY);
        wp.setConsumer(qp);

        ip = new InterpolationPipeline(NO_SAMPLES_PER_WINDOW);
        qp.setConsumer(ip);

        fp = new FeaturePipeline();
        ip.setConsumer(fp);

        ap = new ActivityPipeline(0);
        fp.setConsumer(ap);
    }

    @Override
    public void setConsumer(Consumer<Long, Long, String> consumer) {
        this.ap.setConsumer(consumer);
    }

    @Override
    public void push(Acceleration acceleration) {
        Tuple<Long, Acceleration> t = new Tuple<Long, Acceleration>(0L, acceleration);
        wp.push(t);
    }
}
