package eu.liveandgov.wp1.backend.Stream;

import java.util.Iterator;
import java.util.NoSuchElementException;

import eu.liveandgov.wp1.backend.SensorValueObjects.RawSensorValue;
import eu.liveandgov.wp1.backend.format.SampleWindow;
import eu.liveandgov.wp1.backend.util.LimitedQueue;

/**
 * 
 * @author cehlen
 *
 */
public class WindowStream implements Iterable<SampleWindow> {
	private SensorEventStream sensorStream;
	private int size;
	private int step;
	private LimitedQueue<RawSensorValue> values;
	
	/**
	 * 
	 * @param ses Sensor event stream to generate windows from
	 * @param size Number of samples
	 * @param step Number of samples we move the window
	 */
	public WindowStream(SensorEventStream ses, int size, int step) {
		this.sensorStream = ses;
		this.size = size;
		this.step = step;
		values = new LimitedQueue<RawSensorValue>(size);
	}

	@Override
	public Iterator<SampleWindow> iterator() {
		return new Iterator<SampleWindow> () {
			private SampleWindow next = prepareNext();
			
			private SampleWindow prepareNext() {
				try {
					SampleWindow window = new SampleWindow();
					// if we start the queue we need to fill it
					if(values.size() < size) {
						for(int i = 0; i < size; i++) {
							values.add((RawSensorValue)sensorStream.next());
						}
					} else {
						for(int i = 0; i < step; i++) {
							values.add((RawSensorValue)sensorStream.next());
						}
					}
					window.startTime = values.getFirst().timestamp;
					window.values = values.toArray(new RawSensorValue[size]);
					return window;
				} catch(NoSuchElementException e) {
					return null;
				}
			}
			
			@Override
			public boolean hasNext() {
				return !(next == null);
			}

			@Override
			public SampleWindow next() {
				SampleWindow cur = next;
				next = prepareNext();
				if(cur == null) { throw new NoSuchElementException(); }
				return cur;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
}
