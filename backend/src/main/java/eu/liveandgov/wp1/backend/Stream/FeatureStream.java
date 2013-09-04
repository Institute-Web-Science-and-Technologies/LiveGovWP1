package eu.liveandgov.wp1.backend.Stream;

import java.util.Iterator;

import eu.liveandgov.wp1.backend.SensorValueObjects.AccFeatureValue;
import eu.liveandgov.wp1.backend.SensorValueObjects.SensorValue;
import eu.liveandgov.wp1.backend.format.SampleWindow;

public class FeatureStream implements Iterable<AccFeatureValue>{
	private WindowStream WS;
	public FeatureStream(WindowStream WS) {
		this.WS = WS;
	}
	
	@Override
	public Iterator<AccFeatureValue> iterator() {
		return new Iterator<AccFeatureValue>() {
			
			private Iterator<SampleWindow> WSI = WS.iterator();

			@Override
			public boolean hasNext() {
				return WSI.hasNext();
			}

			@Override
			public AccFeatureValue next() {
				return AccFeatureValue.fromWindow(WSI.next());
			}

			@Override
			public void remove() {
				WSI.remove();
			}
		};
	}	
	
}
