package eu.liveandgov.wp1.pps;


import eu.liveandgov.wp1.data.impl.GPS;
import eu.liveandgov.wp1.data.impl.Proximity;
import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.pps.api.CalculationResult;
import eu.liveandgov.wp1.pps.api.ProximityService;

/**
 * <p>Pipeline that takes GPS coordinates and tries to match them against objects to see if they are in proximity of the given coordinate</p>
 * Created by Lukas Härtel on 18.01.14.
 */
public class PPSPipeline extends Pipeline<GPS, Proximity> {
    /**
     * The key to yield if in proximity
     */
    private final String key;

    /**
     * The service that determines the proximity
     */
    private final ProximityService proximityService;

    /**
     * Creates a new instance with the given values
     *
     * @param key              The key to yield if in proximity
     * @param proximityService The service that determines the proximityx
     */
    public PPSPipeline(String key, ProximityService proximityService) {
        this.key = key;
        this.proximityService = proximityService;
    }

    @Override
    public void push(final GPS gps) {
        final CalculationResult prox = proximityService.calculate(gps.lat, gps.lon);

        switch (prox.type) {
            case IN_PROXIMITY:
                produce(new Proximity(gps.getTimestamp(), gps.getDevice(), key, true, prox.identifier));
                break;

            case ERROR:
            case NO_DECISION:
            case NOT_IN_PROXIMITY:
                produce(new Proximity(gps.getTimestamp(), gps.getDevice(), key, false, prox.identifier));
                break;
        }
    }
}
