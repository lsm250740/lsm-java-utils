package com.lsm.junit.thread;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class DummySlowEnumCacheTest {

    @Test
    void testGetPut() {
        DummySlowEnumCache.INST.put("key1", "value1");
        assertEquals("value1", DummySlowEnumCache.INST.get("key1"));
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
                    DummySlowEnumCache.INST.put("thread" + threadId, "value" + threadId);
                    assertNotNull(DummySlowEnumCache.INST.get("thread" + threadId));
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