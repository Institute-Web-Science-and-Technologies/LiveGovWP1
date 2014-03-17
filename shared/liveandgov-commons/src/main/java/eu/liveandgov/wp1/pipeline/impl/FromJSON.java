package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;
import org.json.JSONObject;

/**
 * <p>Serializes a JSON object into a string</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class FromJSON extends Pipeline<JSONObject, String> {

    @Override
    public void push(JSONObject jsonObject) {
        produce(jsonObject.toString());
    }
}
