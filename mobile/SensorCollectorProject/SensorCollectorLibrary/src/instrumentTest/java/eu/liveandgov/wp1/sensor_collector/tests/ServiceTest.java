package eu.liveandgov.wp1.sensor_collector.tests;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;

import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import eu.liveandgov.wp1.sensor_collector.ServiceSensorControl;
import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.sensor_collector.persistence.FilePersistor;
import eu.liveandgov.wp1.sensor_collector.persistence.ZipFilePersistor;

import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.*;
import static eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI.*;

/**
 * Created by lukashaertel on 12.01.14.
 */
public class ServiceTest extends ServiceTestCase<ServiceSensorControl> {

    public ServiceTest() {
        super(ServiceSensorControl.class);
    }

    private ServiceSensorControl initialize() {
        // Start
        startService(new Intent(getSystemContext(), ServiceSensorControl.class));

        // Return
        return getService();
    }

    /**
     * Sends an intent and awaits the first intent satisfying the filter
     */
    public Intent pingPong(final Intent outwardIntent, final IntentFilter inwardIntent, Long timeoutMsOrNull) throws TimeoutException {
        // Exchange-point
        final Exchanger<Intent> exchanger = new Exchanger<Intent>();

        // Receiver for listening and exchanging
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // Commute the received intent
                try {
                    exchanger.exchange(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // Receive a "pong"
        getContext().registerReceiver(receiver, inwardIntent);

        // Send the "ping"
        startService(outwardIntent);

        // If no timeout set, wait indefinitely
        try {
            if (timeoutMsOrNull == null)
                return exchanger.exchange(null);
            else
                return exchanger.exchange(null, timeoutMsOrNull, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(e);
        }
        finally {
            // Unregister the receiver, clean stuff up
            getContext().unregisterReceiver(receiver);
        }
    }

    /**
     * Basic test of the intent API
     */
    public void testIntentAPI() {
        final ServiceSensorControl service = initialize();

        Assert.assertFalse(service.isRecording);

        // Send recording enable intent
        final Intent iaRecordingEnable = new Intent(getContext(), ServiceSensorControl.class);
        iaRecordingEnable.setAction(ACTION_RECORDING_ENABLE);
        startService(iaRecordingEnable);

        Assert.assertTrue(service.isRecording);

        // Start streaming
        final Intent iaStartStreaming = new Intent(getContext(), ServiceSensorControl.class);
        iaStartStreaming.setAction(START_STREAMING);
        startService(iaStartStreaming);

        Assert.assertTrue(service.isStreaming);

        // Stop streaming
        final Intent iaStopStreaming = new Intent(getContext(), ServiceSensorControl.class);
        iaStopStreaming.setAction(STOP_STREAMING);
        startService(iaStopStreaming);

        Assert.assertFalse(service.isStreaming);

        // Send recording disable intent
        final Intent iaRecordingDisable = new Intent(getContext(), ServiceSensorControl.class);
        iaRecordingDisable.setAction(RECORDING_DISABLE);
        startService(iaRecordingDisable);

        Assert.assertFalse(service.isRecording);
    }

    /**
     * Basic test of the intent API
     */
    public void testIntentAPIWithStatus() throws TimeoutException {
        final ServiceSensorControl service = initialize();

        // Send recording enable intent
        final Intent iaRecordingEnable = new Intent(getContext(), ServiceSensorControl.class);
        iaRecordingEnable.setAction(ACTION_RECORDING_ENABLE);
        startService(iaRecordingEnable);

        // Get status intent
        final Intent iaGetStatus = new Intent(getContext(), ServiceSensorControl.class);
        iaGetStatus.setAction(ACTION_GET_STATUS);

        // Send and await result
        final Intent firstResultIntent = pingPong(iaGetStatus, new IntentFilter(RETURN_STATUS), 1000L);

        // Assert correct status
        Assert.assertTrue(firstResultIntent.getBooleanExtra(FIELD_SAMPLING, false));

        // Send recording disable intent
        final Intent iaRecordingDisable = new Intent(getContext(), ServiceSensorControl.class);
        iaRecordingDisable.setAction(RECORDING_DISABLE);
        startService(iaRecordingDisable);

        // Send and await result
        final Intent secondResultIntent = pingPong(iaGetStatus, new IntentFilter(RETURN_STATUS), 1000L);

        // Assert correct status
        Assert.assertFalse(secondResultIntent.getBooleanExtra(FIELD_SAMPLING, true));
    }

    /**
     * Test-case for issue #43
     */
    public void testDeleteWhileRecording() {
        final ServiceSensorControl service = initialize();

        // Send recording enable intent
        final Intent iaRecordingEnable = new Intent(getContext(), ServiceSensorControl.class);
        iaRecordingEnable.setAction(ACTION_RECORDING_ENABLE);
        startService(iaRecordingEnable);

        Assert.assertTrue(service.isRecording);

        final Intent iaDeleteSamples = new Intent(getContext(), ServiceSensorControl.class);
        iaDeleteSamples.setAction(ExtendedIntentAPI.ACTION_DELETE_SAMPLES);
        startService(iaDeleteSamples);

        // Send recording disable intent
        final Intent iaRecordingDisable = new Intent(getContext(), ServiceSensorControl.class);
        iaRecordingDisable.setAction(RECORDING_DISABLE);
        startService(iaRecordingDisable);

        Assert.assertFalse(service.isRecording);
    }

    /**
     * Test if a regular file persistor reports empty if empty
     */
    public void testRFPEmpty() throws IOException {
        final ServiceSensorControl service = initialize();

        // Get Temporary file
        final File file = File.createTempFile("rfp", ".ssf", getContext().getCacheDir());

        // Create FP with this file and close it, so it has no samples
        final FilePersistor filePersistor = new FilePersistor(file);
        filePersistor.close();

        // Assert that no samples are reported
        assertFalse(filePersistor.hasSamples());
    }

    /**
     * Test if a regular file persistor reports non-empty if non-empty
     */
    public void testRFPNonEmpty() throws IOException {
        final ServiceSensorControl service = initialize();

        // Get Temporary file
        final File file = File.createTempFile("rfp", ".ssf", getContext().getCacheDir());

        // Create FP with this file and close it, so it has no samples
        final FilePersistor filePersistor = new FilePersistor(file);
        filePersistor.push("SAMPLE");
        filePersistor.close();

        // Assert that sample is reported
        assertTrue(filePersistor.hasSamples());
    }


    /**
     * Test if a zipped file persistor reports empty if empty
     */
    public void testZFPEmpty() throws IOException {
        final ServiceSensorControl service = initialize();

        // Get Temporary file
        final File file = File.createTempFile("zfp", ".ssf", getContext().getCacheDir());

        // Create ZFP with this file and close it, so it has no samples
        final ZipFilePersistor zipFilePersistor = new ZipFilePersistor(file);
        zipFilePersistor.close();

        // Assert that no samples are reported
        assertFalse(zipFilePersistor.hasSamples());
    }

    /**
     * Test if a zipped file persistor reports non-empty if non-empty
     */
    public void testZFPNonEmpty() throws IOException {
        final ServiceSensorControl service = initialize();

        // Get Temporary file
        final File file = File.createTempFile("zfp", ".ssf", getContext().getCacheDir());

        // Create ZFP with this file and close it, so it has no samples
        final ZipFilePersistor zipFilePersistor = new ZipFilePersistor(file);
        zipFilePersistor.push("SAMPLE");
        zipFilePersistor.close();

        // Assert that sample is reported
        assertTrue(zipFilePersistor.hasSamples());
    }
}
