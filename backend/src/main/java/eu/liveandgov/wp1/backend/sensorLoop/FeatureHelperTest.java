package eu.liveandgov.wp1.backend.sensorLoop;

import static org.junit.Assert.*;

import org.junit.Test;

public class FeatureHelperTest {

	@Test
	public void test() {
		float[] input = { 1,2,3,4,5 };
		
		assertEquals( 3, FeatureHelper.mean(input), 0.005 );
		
		System.out.println(FeatureHelper.var(input));
		
	}

}