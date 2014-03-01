package eu.liveandgov.wp1;

import eu.liveandgov.wp1.pipeline.*;

/**
 * Created by cehlen on 24/02/14.
 */
public class Main {

    public static void main(String args[]) {
        DatabaseProducer dp = new DatabaseProducer("liveandgov", "liveandgov", "liveandgov");

        WindowPipeline wp = new WindowPipeline(20000, 1000);
        dp.setConsumer(wp);

        QualityPipeline qp = new QualityPipeline(40);
        wp.setConsumer(qp);

        InterpolationPipeline ip = new InterpolationPipeline(50);
        qp.setConsumer(ip);

        FeaturePipeline fp = new FeaturePipeline();
        ip.setConsumer(fp);

        ActivityPipeline ap = new ActivityPipeline(1);
        fp.setConsumer(ap);

        TestConsumer tc = new TestConsumer();
        ap.setConsumer(tc);

        dp.start();
        System.out.println("Done");
    }
}
