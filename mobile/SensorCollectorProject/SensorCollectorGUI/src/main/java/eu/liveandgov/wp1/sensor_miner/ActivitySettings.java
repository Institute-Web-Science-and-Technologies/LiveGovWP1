package eu.liveandgov.wp1.sensor_miner;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.concurrent.TimeUnit;

import eu.liveandgov.wp1.sensor_collector.api.MoraConfig;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_settings)
public class ActivitySettings extends BaseMoraActivity {
    @InjectView(R.id.user)
    EditText user;

    @InjectView(R.id.upload)
    EditText upload;

    @InjectView(R.id.uploadCompressed)
    CheckBox uploadCompressed;

    @InjectView(R.id.streaming)
    EditText streaming;

    @InjectView(R.id.gps)
    RadioGroup gps;


    @InjectView(R.id.velocity)
    CheckBox velocity;

    @InjectView(R.id.acceleration)
    RadioGroup acceleration;

    @InjectView(R.id.linearAcceleration)
    RadioGroup linearAcceleration;

    @InjectView(R.id.gravity)
    RadioGroup gravity;

    @InjectView(R.id.magnetometer)
    RadioGroup magnetometer;

    @InjectView(R.id.rotation)
    RadioGroup rotation;

    @InjectView(R.id.wifi)
    RadioGroup wifi;

    @InjectView(R.id.bluetooth)
    RadioGroup bluetooth;

    @InjectView(R.id.gsm)
    RadioGroup gsm;

    @InjectView(R.id.googleActivity)
    RadioGroup googleActivity;

    @InjectView(R.id.har)
    RadioGroup har;

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
        upload.setText(config.upload);
        uploadCompressed.setChecked(config.uploadCompressed);
        streaming.setText(config.streaming);
        assignInteger(gps, config.gps, TimeUnit.SECONDS);
        velocity.setChecked(config.velocity);
        assignInteger(acceleration, config.acceleration, TimeUnit.MILLISECONDS);
        assignInteger(linearAcceleration, config.linearAcceleration, TimeUnit.MILLISECONDS);
        assignInteger(gravity, config.gravity, TimeUnit.MILLISECONDS);
        assignInteger(magnetometer, config.magnetometer, TimeUnit.MILLISECONDS);
        assignInteger(rotation, config.rotation, TimeUnit.MILLISECONDS);
        assignInteger(wifi, config.wifi, TimeUnit.SECONDS);
        assignInteger(bluetooth, config.bluetooth, TimeUnit.SECONDS);
        assignInteger(gsm, config.gsm, TimeUnit.SECONDS);
        assignInteger(googleActivity, config.googleActivity, TimeUnit.SECONDS);
        assignBoolean(har, config.har);
    }

    public void saveSettings(View view) {
        MoraConfig config = new MoraConfig(
                user.getText().toString(),
                5, // TODO I DON'T WANT TO INCLUDE THIS IN SETTINGS, REMOVE
                upload.getText().toString(),
                uploadCompressed.isChecked(),
                streaming.getText().toString(),
                evaluateInteger(gps, TimeUnit.SECONDS),
                velocity.isChecked(),
                evaluateInteger(acceleration, TimeUnit.MILLISECONDS),
                evaluateInteger(linearAcceleration, TimeUnit.MILLISECONDS),
                evaluateInteger(gravity, TimeUnit.MILLISECONDS),
                evaluateInteger(magnetometer, TimeUnit.MILLISECONDS),
                evaluateInteger(rotation, TimeUnit.MILLISECONDS),
                evaluateInteger(wifi, TimeUnit.SECONDS),
                evaluateInteger(bluetooth, TimeUnit.SECONDS),
                evaluateInteger(gsm, TimeUnit.SECONDS),
                evaluateInteger(googleActivity, TimeUnit.SECONDS),
                evaluateBoolean(har)
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

    private Integer evaluateInteger(RadioGroup radioGroup, TimeUnit unit) {
        RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        String caption = radioButton.getText().toString();
        if ("Off".equals(caption))
            return null;
        else
            return (int) TimeUnit.MILLISECONDS.convert(Integer.valueOf(caption), unit);
    }

    private void assignInteger(RadioGroup radioGroup, Integer value, TimeUnit unit) {
        if (value == null) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View child = radioGroup.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) child;

                    if ("Off".equals(radioButton.getText().toString()))
                        radioButton.setChecked(true);
                    return;
                }
            }
        } else {
            RadioButton selected = null;
            int cmd = Integer.MAX_VALUE;

            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View child = radioGroup.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) child;

                    if (!"Off".equals(radioButton.getText().toString())) {
                        int p = (int) TimeUnit.MILLISECONDS.convert(Integer.valueOf(radioButton.getText().toString()), unit);
                        if (Math.abs(value - p) < cmd) {
                            selected = radioButton;
                            cmd = Math.abs(value - p);
                        }
                    }
                }
            }

            if (selected == null)
                assignInteger(radioGroup, null, unit);
            else
                selected.setChecked(true);
        }
    }

    private boolean evaluateBoolean(RadioGroup radioGroup) {
        RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        String caption = radioButton.getText().toString();
        return !"Off".equals(caption);
    }

    private void assignBoolean(RadioGroup radioGroup, boolean value) {
        if (value) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View child = radioGroup.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) child;

                    if (!"Off".equals(radioButton.getText().toString())) {
                        radioButton.setChecked(true);
                        return;
                    }
                }
            }
        } else {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View child = radioGroup.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) child;

                    if ("Off".equals(radioButton.getText().toString())) {
                        radioButton.setChecked(true);
                        return;
                    }
                }
            }
        }
    }

}