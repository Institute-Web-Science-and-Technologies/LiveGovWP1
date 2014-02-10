package eu.liveandgov.wp1.pps;


import eu.liveandgov.wp1.data.DataCommons;
import eu.liveandgov.wp1.data.Tuple;
import eu.liveandgov.wp1.data.implementation.GPS;
import eu.liveandgov.wp1.data.implementation.Proximity;
import eu.liveandgov.wp1.pipeline.Pipeline;
import eu.liveandgov.wp1.pps.api.ProximityService;

/**
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
        final Tuple<Boolean, String> prox = proximityService.calculate(gps.data.lat, gps.data.lon);

        if (prox != null) {
            produce(new Proximity(
                    DataCommons.TYPE_PROXIMITY,
                    gps.header,
                    new Proximity.ProximityStatus(
                            key,
                            prox.left
                                    ? Proximity.ProximityType.IN_PROXIMITY
                                    : Proximity.ProximityType.NOT_IN_PROXIMITY,
                            prox.right)
            ));
        } else {
            produce(new Proximity(
                    DataCommons.TYPE_PROXIMITY,
                    gps.header,
                    new Proximity.ProximityStatus(
                            key,
                            Proximity.ProximityType.NO_DECISION,
                            "")
            ));
        }
    }
}
