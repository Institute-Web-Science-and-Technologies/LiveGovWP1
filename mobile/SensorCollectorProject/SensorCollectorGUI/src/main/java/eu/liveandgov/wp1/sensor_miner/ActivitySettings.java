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
        final TextView uploadAddress = (TextView) findViewById(R.id.upload_address);
        final TextView uploadPort = (TextView) findViewById(R.id.upload_port);
        final TextView uploadServlet = (TextView) findViewById(R.id.upload_servlet);
        final TextView streamingAddress = (TextView) findViewById(R.id.streaming_address);
        final TextView streamingPort = (TextView) findViewById(R.id.streaming_port);

        // Open settings
        SharedPreferences settings = getSharedPreferences(getString(R.string.spn), 0);

        // Get configured value
        final String uploadAddressValue = settings.getString(getString(R.string.prf_upload_address), null);
        final int uploadPortValue = settings.getInt(getString(R.string.prf_upload_port), Integer.MIN_VALUE);
        final String uploadServletValue = settings.getString(getString(R.string.prf_upload_servlet), null);
        final String streamingAddressValue = settings.getString(getString(R.string.prf_streaming_address), null);
        final int streamingPortValue = settings.getInt(getString(R.string.prf_streaming_port), Integer.MIN_VALUE);

        // Assign into view
        uploadAddress.setText(uploadAddressValue == null ? "" : uploadAddressValue);
        uploadPort.setText(uploadPortValue == Integer.MIN_VALUE ? "" : Integer.toString(uploadPortValue));
        uploadServlet.setText(uploadServletValue == null ? "" : uploadServletValue);
        streamingAddress.setText(streamingAddressValue == null ? "" : streamingAddressValue);
        streamingPort.setText(streamingPortValue == Integer.MIN_VALUE ? "" : Integer.toString(streamingPortValue));
    }

    @Override
    protected void onPause() {

        // Open settings for editing
        SharedPreferences settings = getSharedPreferences(getString(R.string.spn), 0);
        SharedPreferences.Editor edit = settings.edit();

        // Get source textview for streaming address settings
        final TextView uploadAddress = (TextView) findViewById(R.id.upload_address);
        final TextView uploadPort = (TextView) findViewById(R.id.upload_port);
        final TextView uploadServlet = (TextView) findViewById(R.id.upload_servlet);
        final TextView streamingAddress = (TextView) findViewById(R.id.streaming_address);
        final TextView streamingPort = (TextView) findViewById(R.id.streaming_port);

        // Calculate new configuration value
        final String uploadAddressValue = uploadAddress.getText().length() == 0 ? null : uploadAddress.getText().toString();
        final int uploadPortValue = uploadPort.getText().length() == 0 ? Integer.MIN_VALUE : Integer.valueOf(uploadPort.getText().toString());
        final String uploadServletValue = uploadServlet.getText().length() == 0 ? null : uploadServlet.getText().toString();

        final String streamingAddressValue = streamingAddress.getText().length() == 0 ? null : streamingAddress.getText().toString();
        final int streamingPortValue = streamingPort.getText().length() == 0 ? Integer.MIN_VALUE : Integer.valueOf(streamingPort.getText().toString());

        // Store in settings and commit
        edit.putString(getString(R.string.prf_upload_address), uploadAddressValue);
        edit.putInt(getString(R.string.prf_upload_port), uploadPortValue);
        edit.putString(getString(R.string.prf_upload_servlet), uploadServletValue);
        edit.putString(getString(R.string.prf_streaming_address), streamingAddressValue);
        edit.putInt(getString(R.string.prf_streaming_port), streamingPortValue);
        edit.apply();

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