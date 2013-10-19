package eu.liveandgov.sensorcollectorv3.har;

import eu.liveandgov.sensorcollectorv3.connector.Consumer;
import eu.liveandgov.sensorcollectorv3.connector.Producer;
import eu.liveandgov.sensorcollectorv3.sensors.Window;

/**
 * Created by cehlen on 10/19/13.
 */
public class FeatureProducer extends Producer<FeatureVector> implements Consumer<Window> {

    @Override
    public void push(Window m) {



    }
}
