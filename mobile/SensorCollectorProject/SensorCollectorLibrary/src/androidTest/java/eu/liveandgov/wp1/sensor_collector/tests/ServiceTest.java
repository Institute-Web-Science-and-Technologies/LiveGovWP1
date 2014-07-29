package eu.liveandgov.wp1.sensor_collector.tests;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;

import junit.framework.Assert;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import eu.liveandgov.wp1.data.impl.Tag;
import eu.liveandgov.wp1.sensor_collector.ServiceSensorControl;
import eu.liveandgov.wp1.sensor_collector.configuration.ExtendedIntentAPI;
import eu.liveandgov.wp1.sensor_collector.tests.utils.ItemPipeHelper;
import eu.liveandgov.wp1.sensor_collector.tests.utils.Matcher;

import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_ANNOTATE;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_GET_STATUS;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_RECORDING_ENABLE;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_SET_ID;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_START_HAR;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.ACTION_STOP_HAR;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_ANNOTATION;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_SAMPLING;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.FIELD_USER_ID;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.RECORDING_DISABLE;
import static eu.liveandgov.wp1.sensor_collector.configuration.IntentAPI.RETURN_STATUS;

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
        } finally {
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
     * Test if annotation is entered in the pipeline
     */
    public void testAnnotation() {
        // Setup pipeline with expected regular expression
        final ItemPipeHelper helper = new ItemPipeHelper();
        helper.expectFrom("TAG,[^,]+,[^,]+,\"ANONYTATION\"").exactly(1).toMatch(Matcher.PATTERN_MATCHER);

        // Initialize service, add the pipeline tester to the pipeline
        final ServiceSensorControl service = initialize();
        service.connectorThread.addConsumer(helper.itemNode);

        // Send annotaion intent
        final Intent iaAnnotate = new Intent(getContext(), ServiceSensorControl.class);
        iaAnnotate.setAction(ACTION_ANNOTATE);
        iaAnnotate.putExtra(FIELD_ANNOTATION, "ANONYTATION");
        startService(iaAnnotate);

        // Assert that the pipeline commutes the annotation, give it time! 1 Second is long (hehe)
        helper.assertStatusIn(1L, TimeUnit.SECONDS);
    }

    /**
     * Test the status update flag
     */
    public void testSetId() throws TimeoutException {
        final ServiceSensorControl service = initialize();

        // Set Id change request
        final Intent iaSetId = new Intent(getContext(), ServiceSensorControl.class);
        iaSetId.setAction(ACTION_SET_ID);
        iaSetId.putExtra(FIELD_USER_ID, "ANONYMOUSE");
        startService(iaSetId);

        // Get status intent
        final Intent iaGetStatus = new Intent(getContext(), ServiceSensorControl.class);
        iaGetStatus.setAction(ACTION_GET_STATUS);

        // Ping pong stuff to app and back to the test case
        final Intent resultIntent = pingPong(iaGetStatus, new IntentFilter(RETURN_STATUS), 1000L);

        // Assert correct user identity
        Assert.assertEquals(resultIntent.getStringExtra(FIELD_USER_ID), "ANONYMOUSE");
    }

    public void testHAR() {
        // Setup pipeline with always matcher
        final ItemPipeHelper helper = new ItemPipeHelper();

        // Initialize service, set the pipeline tester as the HAR
        final ServiceSensorControl service = initialize();
        service.harPipeline = helper.itemNode;

        // Start HAR
        final Intent iaStartHAR = new Intent(getContext(), ServiceSensorControl.class);
        iaStartHAR.setAction(ACTION_START_HAR);
        startService(iaStartHAR);

        // Setup helper, push some stuff, don't care for format, then assert stuff reaches HAR
        helper.expectFrom(null).atLeast(1).toMatch(Matcher.ALWAYS);
        service.sensorQueue.push(new Tag(0, "NO DEVICE", "NOT EMPTY"));
        helper.assertStatusIn(1L, TimeUnit.SECONDS);

        // Stop HAR
        final Intent iaStopHAR = new Intent(getContext(), ServiceSensorControl.class);
        iaStopHAR.setAction(ACTION_STOP_HAR);
        startService(iaStopHAR);

        // Update helper, push more stuff, still don't care for format, assert stuff does not reach
        // HAR
        helper.clear();
        helper.expectFrom(null).exactly(0).toMatch(Matcher.ALWAYS);
        service.sensorQueue.push(new Tag(0, "NO DEVICE", "NOT EMPTY"));
        helper.assertStatusIn(1L, TimeUnit.SECONDS);

    }

    // TODO: These tests were wrong, make new ones
    //    /**
    //     * Test if a regular file persistor reports empty if empty
    //     */
    //    public void testRFPEmpty() throws IOException {
    //        final ServiceSensorControl service = initialize();
    //
    //        // Get Temporary file
    //        final File file = File.createTempFile("rfp", ".ssf", getContext().getCacheDir());
    //
    //        // Create FP with this file and close it, so it has no samples
    //        final FilePersistor filePersistor = new FilePersistor(file);
    //        filePersistor.close();
    //
    //        // Assert that no samples are reported
    //        assertFalse(filePersistor.hasSamples());
    //    }
    //
    //    /**
    //     * Test if a regular file persistor reports non-empty if non-empty
    //     */
    //    public void testRFPNonEmpty() throws IOException {
    //        final ServiceSensorControl service = initialize();
    //
    //        // Get Temporary file
    //        final File file = File.createTempFile("rfp", ".ssf", getContext().getCacheDir());
    //
    //        // Create FP with this file and close it, so it has no samples
    //        final FilePersistor filePersistor = new FilePersistor(file);
    //        service.sensorQueue.push(new Tag(0, "NO DEVICE", "SAMPLE"));
    //        filePersistor.close();
    //
    //        // Assert that sample is reported
    //        assertTrue(filePersistor.hasSamples());
    //    }
    //
    //
    //    /**
    //     * Test if a zipped file persistor reports empty if empty
    //     */
    //    public void testZFPEmpty() throws IOException {
    //        final ServiceSensorControl service = initialize();
    //
    //        // Get Temporary file
    //        final File file = File.createTempFile("zfp", ".ssf", getContext().getCacheDir());
    //
    //        // Create ZFP with this file
    //        final ZipFilePersistor zipFilePersistor = new ZipFilePersistor(file);
    //
    //        // Add it to the services connector
    //        service.connectorThread.addConsumer(zipFilePersistor);
    //
    //        // Remove it from the services connector
    //        service.connectorThread.removeConsumer(zipFilePersistor);
    //
    //        // Close it
    //        zipFilePersistor.close();
    //
    //        // Assert that no samples are reported
    //        assertFalse(zipFilePersistor.hasSamples());
    //    }
    //
    //    /**
    //     * Test if a zipped file persistor reports non-empty if non-empty
    //     */
    //    public void testZFPNonEmpty() throws IOException {
    //        final ServiceSensorControl service = initialize();
    //
    //        // Get Temporary file
    //        final File file = File.createTempFile("zfp", ".ssf", getContext().getCacheDir());
    //
    //        // Create ZFP with this file
    //        final ZipFilePersistor zipFilePersistor = new ZipFilePersistor(file);
    //
    //        // Add it to the services connector
    //        service.connectorThread.addConsumer(zipFilePersistor);
    //
    //        // Push one item
    //        service.sensorQueue.push(new Tag(0, "NO DEVICE", "SAMPLE"));
    //
    //        // Remove it from the services connector
    //        service.connectorThread.removeConsumer(zipFilePersistor);
    //
    //        // Close it
    //        zipFilePersistor.close();
    //
    //        // Assert that sample is reported
    //        assertTrue(zipFilePersistor.hasSamples());
    //    }
}
