package eu.liveandgov.wp1.sensor_miner;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_settings)
public class ActivitySettings extends BaseMoraActivity {
    @InjectView(R.id.user)
    EditText user;

    @InjectView(R.id.secretLength)
    EditText secretLength;

    @InjectView(R.id.upload)
    EditText upload;

    @InjectView(R.id.uploadCompressed)
    CheckBox uploadCompressed;

    @InjectView(R.id.streaming)
    EditText streaming;

    @InjectView(R.id.gps)
    CheckBox gps;


    @InjectView(R.id.velocity)
    CheckBox velocity;

    @InjectView(R.id.acceleration)
    CheckBox acceleration;

    @InjectView(R.id.linearAcceleration)
    CheckBox linearAcceleration;

    @InjectView(R.id.gravity)
    CheckBox gravity;

    @InjectView(R.id.magnetometer)
    CheckBox magnetometer;

    @InjectView(R.id.rotation)
    CheckBox rotation;

    @InjectView(R.id.wifi)
    CheckBox wifi;

    @InjectView(R.id.bluetooth)
    CheckBox bluetooth;

    @InjectView(R.id.gsm)
    CheckBox gsm;

    @InjectView(R.id.googleActivity)
    CheckBox googleActivity;

    @InjectView(R.id.har)
    CheckBox har;

    @Inject
    @Named("eu.liveandgov.wp1.sensor_collector.config.configDefault")
    MoraConfig configDefault;

    @Override
    protected void updateStatus() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api.initConnected(new Runnable() {
            @Override
            public void run() {
                MoraConfig config = api.getConfig();
                setTo(config);
            }
        });
    }

    private void setTo(MoraConfig config) {
        user.setText(config.user);
        secretLength.setText(Integer.toString(config.secretLength));
        upload.setText(config.upload);
        uploadCompressed.setChecked(config.uploadCompressed);
        streaming.setText(config.streaming);
        gps.setChecked(config.gps != null);
        velocity.setChecked(config.velocity);
        acceleration.setChecked(config.acceleration != null);
        linearAcceleration.setChecked(config.linearAcceleration != null);
        gravity.setChecked(config.gravity != null);
        magnetometer.setChecked(config.magnetometer != null);
        rotation.setChecked(config.rotation != null);
        wifi.setChecked(config.wifi != null);
        bluetooth.setChecked(config.bluetooth != null);
        gsm.setChecked(config.gsm != null);
        googleActivity.setChecked(config.googleActivity != null);
        har.setChecked(config.har);
    }

    public void saveSettings(View view) {
        MoraConfig config = new MoraConfig(
                user.getText().toString(),
                Integer.valueOf(secretLength.getText().toString()),
                upload.getText().toString(),
                uploadCompressed.isChecked(),
                streaming.getText().toString(),
                gps.isChecked() ? 5000 : null,
                velocity.isChecked(),
                acceleration.isChecked() ? 10 : null,
                linearAcceleration.isChecked() ? 25 : null,
                gravity.isChecked() ? 25 : null,
                magnetometer.isChecked() ? 10 : null,
                rotation.isChecked() ? 25 : null,
                wifi.isChecked() ? 5000 : null,
                bluetooth.isChecked() ? 5000 : null,
                gsm.isChecked() ? 5000 : null,
                googleActivity.isChecked() ? 5000 : null,
                har.isChecked()
        );

        api.setConfig(config);
    }

    public void resetSettings(View view) {
        setTo(configDefault);
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