package eu.liveandgov.wp1.human_activity_recognition.producers;

import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Producer;
import eu.liveandgov.wp1.human_activity_recognition.containers.CountWindow;
import eu.liveandgov.wp1.human_activity_recognition.containers.FeatureVector;
import eu.liveandgov.wp1.human_activity_recognition.containers.FeatureVector_FFT;
import eu.liveandgov.wp1.human_activity_recognition.containers.TaggedWindow;

/**
 * Created by cehlen on 10/19/13.
 */
public class FeatureProducer extends Producer<FeatureVector_FFT> implements Consumer<CountWindow> {

    public void push(CountWindow m) {
        FeatureVector_FFT vec = new FeatureVector_FFT(m);
        consumer.push(vec);
    }

    public void clear() {
        consumer.clear();
    }
}