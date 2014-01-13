package eu.liveandgov.wp1.sensor_collector.tests;

import android.content.Intent;
import android.test.ServiceTestCase;

import junit.framework.Assert;

import eu.liveandgov.wp1.sensor_collector.ServiceSensorControl;

import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.*;
import static eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI.*;

/**
 * Created by lukashaertel on 12.01.14.
 */
public class ServiceSensorControllerTest extends ServiceTestCase<ServiceSensorControl> {
    private Intent intent;

    private ServiceSensorControl service;

    public ServiceSensorControllerTest()
    {
        super(ServiceSensorControl.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        intent = new Intent(getSystemContext(), ServiceSensorControl.class);
    }

    public void testIntentAPI()
    {
        // Start and get service
        startService(intent);
        service = getService();

        Assert.assertFalse(service.isRecording);

        // Send recording enable intent
        final Intent iaRecordingEnable= new Intent(getSystemContext(), ServiceSensorControl.class);
        iaRecordingEnable.setAction(ACTION_RECORDING_ENABLE);
        startService(iaRecordingEnable);

        Assert.assertTrue(service.isRecording);

        // Start streaming
        final Intent iaStartStreaming = new Intent(getSystemContext(), ServiceSensorControl.class);
        iaStartStreaming.setAction(START_STREAMING);
        startService(iaStartStreaming);

        Assert.assertTrue(service.isStreaming);

        // Stop streaming
        final Intent iaStopStreaming = new Intent(getSystemContext(), ServiceSensorControl.class);
        iaStopStreaming.setAction(STOP_STREAMING);
        startService(iaStopStreaming);

        Assert.assertFalse(service.isStreaming);

        // Send recording disable intent
        final Intent iaRecordingDisable= new Intent(getSystemContext(), ServiceSensorControl.class);
        iaRecordingDisable.setAction(RECORDING_DISABLE);
        startService(iaRecordingDisable);

        Assert.assertFalse(service.isRecording);
    }
}
