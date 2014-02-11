package eu.liveandgov.wp1.pps;


import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.pps.api.CalculationResult;
import eu.liveandgov.wp1.pps.api.ProximityService;

/**
 * Todo: Pipelineobject
 * Created by lukashaertel on 18.01.14.
 */
public class PPSPipeline extends Pipeline<GPS, Proximity> {
    private static final String LOG_TAG = "PPSP";

    private final String key;

    private final ProximityService proximityService;

    public PPSPipeline(String key, ProximityService proximityService) {
        this.key = key;
        this.proximityService = proximityService;
    }

    @Override
    public void push(final GPS gps) {
        final CalculationResult prox = proximityService.calculate(gps.lat, gps.lon);

        switch (prox.type) {
            case IN_PROXIMITY:
                produce(new Proximity(gps, key, true, prox.identifier));
                break;

            case ERROR:
            case NO_DECISION:
            case NOT_IN_PROXIMITY:
                produce(new Proximity(gps, key, false, prox.identifier));
                break;
        }
    }
}
