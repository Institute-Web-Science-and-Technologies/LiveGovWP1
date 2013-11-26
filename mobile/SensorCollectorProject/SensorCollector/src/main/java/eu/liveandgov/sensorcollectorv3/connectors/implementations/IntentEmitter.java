package eu.liveandgov.sensorcollectorv3.connectors.implementations;

import android.content.Intent;
import android.util.Log;

import eu.liveandgov.sensorcollectorv3.GlobalContext;
import eu.liveandgov.wp1.human_activity_recognition.connectors.Consumer;

/**
 * Consumer that sends out received messages as broadcast intents.
 *
 * Requires GlobalContext to be set.
 *
 * Created by hartmann on 10/25/13.
 */
public class IntentEmitter implements Consumer<String> {

    private String action;
    private String field;

    public IntentEmitter(String action, String field) {
        this.action = action;
        this.field = field;
    }

    @Override
    public void push(String m) {
        Log.d("EM", "Sending out intent action:" + action + " \n " + field + ":" + m );
        Intent intent = new Intent(action);
        intent.putExtra(field, m);
        GlobalContext.context.sendBroadcast(intent);
    }
}
