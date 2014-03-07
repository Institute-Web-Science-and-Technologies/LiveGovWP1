package eu.liveandgov.wp1;

import eu.liveandgov.wp1.pipeline.*;

/**
 * Created by cehlen on 24/02/14.
 */
public class Main {

    public static void main(String args[]) {
        int WINDOW_LENGTH_MS = 5 * 1000;
        int WINDOW_OVERLAP   = WINDOW_LENGTH_MS - 100;

        int SAMPLING_FREQUENCY_HZ = 50;
        int MIN_RECORDING_FREQUENCY = 40;

        double SAMPLE_LENGHT_MS = 1000D / SAMPLING_FREQUENCY_HZ;
        int NO_SAMPLES_PER_WINDOW = (int) (WINDOW_LENGTH_MS / SAMPLE_LENGHT_MS);

        DatabaseProducer dp = new DatabaseProducer("liveandgov", "liveandgov", "liveandgov");

        WindowPipeline wp = new WindowPipeline(WINDOW_LENGTH_MS, WINDOW_OVERLAP);
        dp.setConsumer(wp);

        QualityPipeline qp = new QualityPipeline(MIN_RECORDING_FREQUENCY);
        wp.setConsumer(qp);

        InterpolationPipeline ip = new InterpolationPipeline(NO_SAMPLES_PER_WINDOW);
        qp.setConsumer(ip);

        FeaturePipeline fp = new FeaturePipeline();
        ip.setConsumer(fp);

        ActivityPipeline ap = new ActivityPipeline(1);
        fp.setConsumer(ap);

        DBConsumer dbc = new DBConsumer("liveandgov", "liveandgov", "liveandgov");
        ap.setConsumer(dbc);

        dp.start();
        dbc.executeQuery();
        System.out.println("Done");
    }
}
