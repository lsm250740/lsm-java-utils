package com.lsm.utils.concurrent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class ReentranLockPerObjectTest {
    
    private ReentranLockPerObject lockManager;
    private TestLockEntry testEntry1;
    private TestLockEntry testEntry2;
    
    private static class TestLockEntry {
        private final String id;
        
        public TestLockEntry(String id) {
            this.id = id;
        }
        
        @Override
        public String toString() {
            return "TestLockEntry[" + id + "]";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestLockEntry that = (TestLockEntry) obj;
            return id.equals(that.id);
        }
        
        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
    
    @BeforeEach
    void setUp() {
        lockManager = new ReentranLockPerObject();
        testEntry1 = new TestLockEntry("entry1");
        testEntry2 = new TestLockEntry("entry2");
    }
    
    @AfterEach
    void tearDown() {
    	assertDoesNotThrow(() ->  lockManager.close(), "Failed to close lock manager");
    }
    
    @Test
    void testBasicLocking() {
        ReentrantLock lock1 = lockManager.of(testEntry1);
        ReentrantLock lock2 = lockManager.of(testEntry2);
        
        assertNotSame(lock1, lock2);
        
        lock1.lock();
        assertTrue(lock1.isLocked());
        assertFalse(lock2.isLocked());
        lock1.unlock();
        assertFalse(lock1.isLocked());
        
        System.out.println("testBasicLocking done successfully");
    }
    
    @Test
    void testSameObjectReturnsSameLock() {
        ReentrantLock lock1 = lockManager.of(testEntry1);
        ReentrantLock lock2 = lockManager.of(testEntry1);
        
        assertSame(lock1, lock2);
        
        System.out.println("testSameObjectReturnsSameLock done successfully");
    }
    
    @Test
    void testTryLock() {
        ReentrantLock lock = lockManager.of(testEntry1);
        
        assertTrue(lock.tryLock());
        lock.unlock();
        
        System.out.println("testTryLock done successfully");
    }
    
    @Test
    void testTryLockWithTimeout() throws InterruptedException {
        ReentrantLock lock = lockManager.of(testEntry1);
        
        assertTrue(lock.tryLock(100, TimeUnit.MILLISECONDS));
        lock.unlock();
        
        System.out.println("testTryLockWithTimeout done successfully");
    }
    
    @Test
    void testMaxLockedObjectsLimit() {
        ReentranLockPerObject limitedManager = new ReentranLockPerObject(2, -1, -1);
        
        limitedManager.of(new TestLockEntry("1"));
        limitedManager.of(new TestLockEntry("2"));
        
        assertThrows(IllegalStateException.class, () -> {
            limitedManager.of(new TestLockEntry("3"));
        });
        
        assertDoesNotThrow(() ->     limitedManager.close(), "Close should not throw exception");
        
        System.out.println("testMaxLockedObjectsLimit done successfully");
    }
    
    @Test
    void testLockExpiration() throws InterruptedException {
        ReentranLockPerObject expiringManager = new ReentranLockPerObject(1000, 100, 50);
        
        ReentrantLock lock = expiringManager.of(testEntry1);
        lock.lock();
        lock.unlock();
        
        Thread.sleep(150);
        
        // After expiration, should be able to create new objects beyond limit
        ReentranLockPerObject limitedExpiringManager = new ReentranLockPerObject(1, 50, 25);
        ReentrantLock lock1 = limitedExpiringManager.of(new TestLockEntry("1"));
        lock1.lock();
        lock1.unlock();
        
        Thread.sleep(76);
        
        // Should not throw exception as expired lock was cleaned up
        assertDoesNotThrow(() -> {
            limitedExpiringManager.of(new TestLockEntry("2"));
        });
        
        assertDoesNotThrow(() ->  expiringManager.close(), "Close should not throw exception");
        assertDoesNotThrow(() -> limitedExpiringManager.close(), "Close should not throw exception");
        
        System.out.println("testLockExpiration done successfully");
    }
    
	@Test
	void testReentrantBehavior() {
		try (ReentranLockPerObject lockManager = new ReentranLockPerObject();) {
			ReentrantLock lock = lockManager.of(testEntry1);
			lock.lock();
			lock.lock(); // Reentrant
			assertTrue(lock.isLocked());
			assertEquals(2, lock.getHoldCount());

			lock.unlock();
			assertTrue(lock.isLocked());
			assertEquals(1, lock.getHoldCount());

			lock.unlock();
			assertFalse(lock.isLocked());
			assertEquals(0, lock.getHoldCount());
		} catch (Exception e) {
			fail("testReentrantBehavior on unexpected exception:" + e.getMessage(), e);
		}
		
		System.out.println("testReentrantBehavior done successfully");
	}
}