package com.lsm.junit.thread;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class DummySlowCacheTest {

    @Test
    void testGetPut() {
        DummySlowCache.put("key1", "value1");
        assertEquals("value1", DummySlowCache.get("key1"));
    }

    @Test
    void testConcurrentInit() throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                	System.out.println("thread" + threadId + " submitted");
                    DummySlowCache.put("thread" + threadId, "value" + threadId);
                    assertNotNull(DummySlowCache.get("thread" + threadId));
                } finally {
                	System.out.println("thread" + threadId + " done");
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(15, TimeUnit.SECONDS));
        executor.shutdown();
    }
}