package eu.liveandgov.wp1.sensor_miner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

import eu.liveandgov.wp1.data.Callback;
import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import eu.liveandgov.wp1.sensor_collector.api.Runnables;
import eu.liveandgov.wp1.sensor_collector.api.Trip;
import eu.liveandgov.wp1.sensor_collector.os.Reporter;
import eu.liveandgov.wp1.sensor_collector.util.MoraStrings;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * <p>
 * Activity controlling the Mora service
 * </p>
 * <p>
 * Created on 12.12.2014.
 * </p>
 *
 * @author lukashaertel
 */
@ContentView(R.layout.activity_sensor_collector)
public class ActivitySensorCollector extends BaseMoraActivity {
    @InjectView(R.id.recordingToggleButton)
    ToggleButton recordingToggleButton;

    @InjectView(R.id.recordingProgressBar)
    ProgressBar recordingProgressBar;

    @InjectView(R.id.transferProgress)
    ProgressBar transferProgress;

    @InjectView(R.id.annotationText)
    EditText annotationText;

    @InjectView(R.id.logTextView)
    TextView logTextView;

    @InjectView(R.id.idText)
    EditText idText;

    @InjectView(R.id.streamButton)
    ToggleButton streamButton;

    @Override
    protected void updateStatus() {
        // Set button states
        recordingToggleButton.setChecked(api.isRecording());
        streamButton.setChecked(api.isStreaming());

        // Set recording progress bar state
        recordingProgressBar.setVisibility(api.isRecording() || api.isStreaming() ? View.VISIBLE : View.INVISIBLE);

        // Compose message
        StringBuilder b = new StringBuilder();
        for (Bundle r : api.getReports()) {
            // Append reporter
            if (r.containsKey(Reporter.SPECIAL_KEY_ORIGINATOR))
                b.append(r.get(Reporter.SPECIAL_KEY_ORIGINATOR)).append("\r\n");
            else
                b.append("Unknown reporter\r\n");

            // Append keys
            for (String k : r.keySet())
                if (!Reporter.SPECIAL_KEY_ORIGINATOR.equals(k)) {
                    b.append(k).append(": ");
                    MoraStrings.appendDeep(b, r.get(k));
                    b.append("\r\n");
                }
            b.append("\r\n");
        }

        logTextView.setText(b);
    }

    public void onRecordingToggleButtonClick(View view) {
        if (api.isRecording())
            api.stopRecording(Runnables.NO_OP_RUNNABLE);
        else
            api.startRecording(Runnables.NO_OP_RUNNABLE);

    }

    public void onStreamButtonClick(View view) {
        if (api.isStreaming())
            api.stopStreaming(Runnables.NO_OP_RUNNABLE);
        else
            api.startStreaming(Runnables.NO_OP_RUNNABLE);
    }


    public void onTransferButtonClick(View view) {
        transferProgress.setIndeterminate(true);

        api.getTrips(new Callback<List<Trip>>() {
            @Override
            public void call(List<Trip> trips) {
                try {
                    for (Trip trip : trips)
                        api.transferTrip(trip);
                } finally {
                    transferProgress.setIndeterminate(false);
                }
            }
        });
    }

    public void onSendButtonClick(View view) {
        String annotation = annotationText.getText().toString();

        api.annotate(annotation);

        Toast.makeText(this, "Adding annotation: " + annotation, Toast.LENGTH_SHORT).show();
    }

    public void onIdButtonClick(View view) {
        String id = idText.getText().toString();

        MoraConfig config = api.getConfig();
        config.user = id;
        api.setConfig(config);

        Toast.makeText(this, "User name set: " + id, Toast.LENGTH_SHORT).show();
    }

    public void onDeleteButtonClick(View view) {
        api.getTrips(new Callback<List<Trip>>() {
            @Override
            public void call(List<Trip> trips) {
                for (Trip trip : trips)
                    api.deleteTrip(trip);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_sensor_collector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, ActivitySettings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
