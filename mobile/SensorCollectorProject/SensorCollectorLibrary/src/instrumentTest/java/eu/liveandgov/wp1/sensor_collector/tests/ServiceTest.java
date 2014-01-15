package eu.liveandgov.wp1.sensor_collector.tests;

import android.content.Intent;
import android.test.ServiceTestCase;

import junit.framework.Assert;

import java.io.File;
import java.io.IOException;

import eu.liveandgov.wp1.sensor_collector.GlobalContext;
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
    private Intent intent;

    private ServiceSensorControl service;

    public ServiceTest() {
        super(ServiceSensorControl.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        intent = new Intent(getSystemContext(), ServiceSensorControl.class);
    }

    /**
     * Basic test of the intent API
     */
    public void testIntentAPI() {
        // Start and get service
        startService(intent);
        GlobalContext.set(service = getService());

        Assert.assertFalse(service.isRecording);

        // Send recording enable intent
        final Intent iaRecordingEnable = new Intent(getSystemContext(), ServiceSensorControl.class);
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
        final Intent iaRecordingDisable = new Intent(getSystemContext(), ServiceSensorControl.class);
        iaRecordingDisable.setAction(RECORDING_DISABLE);
        startService(iaRecordingDisable);

        Assert.assertFalse(service.isRecording);
    }

    /**
     * Test-case for issue #43
     */
    public void testDeleteWhileRecording() {
        // Start and get service
        startService(intent);
        GlobalContext.set(service = getService());

        // Send recording enable intent
        final Intent iaRecordingEnable = new Intent(getSystemContext(), ServiceSensorControl.class);
        iaRecordingEnable.setAction(ACTION_RECORDING_ENABLE);
        startService(iaRecordingEnable);

        Assert.assertTrue(service.isRecording);

        final Intent iaDeleteSamples = new Intent(getSystemContext(), ServiceSensorControl.class);
        iaDeleteSamples.setAction(ExtendedIntentAPI.ACTION_DELETE_SAMPLES);
        startService(iaDeleteSamples);

        // Send recording disable intent
        final Intent iaRecordingDisable = new Intent(getSystemContext(), ServiceSensorControl.class);
        iaRecordingDisable.setAction(RECORDING_DISABLE);
        startService(iaRecordingDisable);

        Assert.assertFalse(service.isRecording);
    }

    /**
     * Test if a regular file persistor reports empty if empty
     */
    public void testRFPEmpty() throws IOException {
        // Start and get service
        startService(intent);
        GlobalContext.set(service = getService());

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
        // Start and get service
        startService(intent);
        GlobalContext.set(service = getService());

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
        // Start and get service
        startService(intent);
        GlobalContext.set(service = getService());

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
        // Start and get service
        startService(intent);
        GlobalContext.set(service = getService());

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
