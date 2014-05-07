package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.pipeline.Pipeline;
import org.json.JSONObject;

/**
 * <p>Parses a JSON object from a string</p>
 * Created by Lukas Härtel on 17.03.14.
 */
public class ToJSON extends Pipeline<String, JSONObject> {
    @Override
    public void push(String s) {
        produce(new JSONObject(s));
    }
}
