package eu.liveandgov.wp1;

import eu.liveandgov.wp1.data.FeatureVector;
import eu.liveandgov.wp1.helper.Persistor;
import eu.liveandgov.wp1.pipeline.*;

/**
 * Created by cehlen on 24/02/14.
 */
public class Main {

    public static void main(String args[]) {

        int delay = 100;

        int WINDOW_LENGTH_MS = 5 * 1000;
        int WINDOW_OVERLAP   = WINDOW_LENGTH_MS - delay;

        int SAMPLING_FREQUENCY_HZ = 50;
        int MIN_RECORDING_FREQUENCY = 40;

        double SAMPLE_LENGHT_MS = 1000D / SAMPLING_FREQUENCY_HZ;
        int NO_SAMPLES_PER_WINDOW = (int) (WINDOW_LENGTH_MS / SAMPLE_LENGHT_MS);

        WindowPipeline wp = new WindowPipeline(WINDOW_LENGTH_MS, WINDOW_OVERLAP);

        CSVReader csvReader = new CSVReader(wp);
        csvReader.setConsumer(wp);

        QualityPipeline qp = new QualityPipeline(MIN_RECORDING_FREQUENCY);
        wp.setConsumer(qp);

        InterpolationPipeline ip = new InterpolationPipeline(NO_SAMPLES_PER_WINDOW);
        qp.setConsumer(ip);

        FeaturePipeline fp = new FeaturePipeline(csvReader);
        ip.setConsumer(fp);

        Persistor pers = new Persistor("/DATA/FEATURES.csv");
        pers.setCsvHead(FeatureVector.getCsvHead());
        fp.setConsumer(pers);

        csvReader.readDir("/DATA/UKOB_ALL", true);

        pers.flush();
        System.out.println("Done");
    }
}
