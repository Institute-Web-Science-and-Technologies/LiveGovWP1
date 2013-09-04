package eu.liveandgov.wp1.backend.SensorValueObjects;

public class AccSampleWindow extends SampleWindow<AccSensorValue> {

	public AccSampleWindow(int capacity) {
		super(capacity);
	}
	
	public float[] getX() {
		float x[] = new float[capacity];
		int i = 0;
		for (AccSensorValue asv : vQ) {
			x[i++] = asv.x;
		}
		return x;
	}
	
	public float[] getY() {
		float y[] = new float[capacity];
		int i = 0;
		for (AccSensorValue asv : vQ) {
			y[i++] = asv.y;
		}
		return y;
	}
	
	public float[] getZ() {
		float z[] = new float[capacity];
		int i = 0;
		for (AccSensorValue asv : vQ) {
			z[i++] = asv.z;
		}
		return z;
	}
	
	public float[] getSquare() {
		float square[] = new float[capacity];
		int i = 0;
		for (AccSensorValue asv : vQ) {
			square[i++] = (asv.x * asv.x) + (asv.y * asv.y) + (asv.z * asv.z);
		}
		return square;
	}
	
}
