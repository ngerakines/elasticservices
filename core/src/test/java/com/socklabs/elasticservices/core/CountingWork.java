package com.socklabs.elasticservices.core;

import com.socklabs.elasticservices.core.work.AbstractWork;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nick.gerakines on 1/9/14.
 */
public class CountingWork extends AbstractWork {

	private final AtomicInteger counter;

	public CountingWork() {
		this.counter = new AtomicInteger(0);

		setPhase(StandardPhase.CREATED);
	}

	@Override
	public String getId() {
		return "test:work:counter";
	}

	@Override
	public void run() {
		setPhase(StandardPhase.STARTING);
		setPhase(StandardPhase.STARTED);

		for (int i = 0 ; i < 100; i++) {
			counter.incrementAndGet();
		}

		setPhase(StandardPhase.STOPPING);
		setPhase(StandardPhase.STOPPED);
	}

	public int getValue() {
		return counter.intValue();
	}

}
