package eu.liveandgov.wp1.backend;

import java.util.Iterator;

import eu.liveandgov.wp1.backend.SensorValueObjects.RawSensorValue;

/**
 * 
 * @author cehlen
 *
 */
public class WindowStream implements Iterable<SampleWindow> {
	private SensorEventStream sensorStream;
	private int size;
	private int overlap;
	
	/**
	 * 
	 * @param ses Sensor event stream to generate windows from
	 * @param size Number of samples
	 * @param overlap Number of samples which overlap with the previous and next window
	 */
	public WindowStream(SensorEventStream ses, int size, int overlap) {
		this.sensorStream = ses;
		this.size = size;
		this.overlap = overlap;
	}

	@Override
	public Iterator<SampleWindow> iterator() {
		return new Iterator<SampleWindow> () {
			private SampleWindow lastWindow; 
			
			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public SampleWindow next() {
				SampleWindow window = new SampleWindow();
				for(int i = window.values.length - overlap; i < window.values.length; i++) {
					
				}
				
				return null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
}
