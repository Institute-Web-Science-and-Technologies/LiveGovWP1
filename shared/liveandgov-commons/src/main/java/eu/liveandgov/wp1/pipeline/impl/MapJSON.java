package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;
import org.json.JSONObject;

import java.util.Map;

/**
 * <p>Makes a JSON object from an arbitrary map</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class MapJSON extends Pipeline<Map<?, ?>, JSONObject> {
    @Override
    public void push(Map<?, ?> map) {
        produce(new JSONObject(map));
    }
}
