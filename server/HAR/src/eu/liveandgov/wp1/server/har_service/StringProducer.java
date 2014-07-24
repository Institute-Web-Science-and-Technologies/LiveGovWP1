package eu.liveandgov.wp1.server.har_service;

import eu.liveandgov.wp1.data.Triple;
import eu.liveandgov.wp1.pipeline.Consumer;

public class StringProducer implements Consumer<Triple<Long, Long, String>> {
	private String activity;

    public String getActivity() {
        return this.activity;
    }

    @Override
    public void push(Triple<Long, Long, String> longLongStringTriple) {
        this.activity = longLongStringTriple.right;
    }
}
