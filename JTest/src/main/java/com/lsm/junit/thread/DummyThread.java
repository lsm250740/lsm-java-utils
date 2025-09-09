package com.lsm.junit.thread;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(DummyThread.class);
	private static final Random random = new Random();
	private final int min;
	private final int max;
	private static final AtomicInteger id = new AtomicInteger(0); // max;

	private DummyThread(String name, int max, int min) {
		super(name);
		this.min = min <= 0 ? 500 : min;
		this.max = max <= 0 ? 1000 : max;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		log.info("[Thread {}]: Started  at ",   Thread.currentThread().getName());
		try {
			int sleepTime = random.nextInt(max - min) + min;
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			long endTime = System.currentTimeMillis();
			log.info("Thread {} done in {} ms", Thread.currentThread().getName(), (endTime - startTime));
		}
	}

	public static DummyThread build(int max, int min) {
		return new DummyThread("id-" + id.incrementAndGet(), max, min);
	}

	public static DummyThread build(int max) {
		return build(max, 0);
	}

	public static DummyThread build( String name) {
		return build(name ,0,0) ;
	}

	public static DummyThread build(String name, int max, int min) {
		return new DummyThread(name, max, min);
	}
}
