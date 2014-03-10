package eu.liveandgov.wp1.sensor_miner;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ActivitySettings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get target textview for streaming address settings
        final TextView streamingAddress = (TextView) findViewById(R.id.streaming_address);

        // Open settings
        SharedPreferences settings = getSharedPreferences(getString(R.string.spn), 0);

        // Get configured value
        final String streamingAddressValue = settings.getString(getString(R.string.prf_streaming_address), null);

        // Assign into view
        streamingAddress.setText(streamingAddressValue == null ? "" : streamingAddressValue);
    }

    @Override
    protected void onPause() {

        // Open settings for editing
        SharedPreferences settings = getSharedPreferences(getString(R.string.spn), 0);
        SharedPreferences.Editor edit = settings.edit();

        // Get source textview for streaming address settings
        final TextView streamingAddress = (TextView) findViewById(R.id.streaming_address);

        // Calculate new configuration value
        final String streamingAddressValue = streamingAddress.getText().length() == 0 ? null : streamingAddress.getText().toString();

        // Store in settings and commit
        edit.putString(getString(R.string.prf_streaming_address), streamingAddressValue);
        edit.commit();

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}