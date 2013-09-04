package eu.liveandgov.wp1.backend.sensorLoop;

import eu.liveandgov.wp1.backend.SensorValueObjects.AccSensorValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.SampleWindow;

public class AccWindowArrays {
	private SampleWindow<AccSensorValue> window;
	private float x[];
	private float y[];
	private float z[];
	private float square[];
	
	
	public AccWindowArrays(SampleWindow<AccSensorValue> sw) {
		window = sw;
		x = new float[sw.capacity];
		y = new float[sw.capacity];
		z = new float[sw.capacity];
		square = new float[sw.capacity];
	}
	
	public void update() {
		int i = 0;
		for (AccSensorValue asv : window.getValues()) {
			x[i] = asv.x;
			y[i] = asv.y;
			z[i] = asv.z;
			square[i] = (asv.x * asv.x) + (asv.y * asv.y) + (asv.z * asv.z);
			i++;
		}
	}
	
	public float[] getX() {
		return x;
	}
	
	public float[] getY() {
		return y;
	}
	
	public float[] getZ() {
		return z;
	}
	
	public float[] getSquare() {
		return square;
	}
}
