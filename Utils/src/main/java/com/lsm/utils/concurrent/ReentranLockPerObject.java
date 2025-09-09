package com.lsm.utils.concurrent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe lock manager that provides individual ReentrantLock instances per object.
 * Manages lock lifecycle with configurable limits and automatic cleanup of expired locks.
 * Uses Semaphore to control the number of active locks.
 * 
 * Features:
 * - Maximum number of concurrent locked objects (default: 1000)
 * - Automatic expiration and cleanup of unused locks
 * - Semaphore-based resource management
 * - Background cleanup thread with configurable intervals
 */
public class ReentranLockPerObject  implements AutoCloseable{
	/** Logger for initialization and cleanup operations */
	private static final Logger logger = Logger.getLogger(ReentranLockPerObject.class.getName());
	
	/** Thread-safe map storing locks per object */
	private final ConcurrentHashMap<Object, LockEntry> locks = new ConcurrentHashMap<>();
	
	/** Maximum number of concurrent locked objects */
	private final int maxLockedObjects;
	
	/** Maximum time a lock can remain active before expiration (ms) */
	private final long maxLockTimeMs;
	
	/** Semaphore controlling the number of active locks */
	private final Semaphore semaphore;

	/** Background executor for cleanup operations */
	private final ScheduledExecutorService cleanupExecutor;
	
	/** Interval between cleanup operations (ms) */
	private long idleLockTimeMs;

	/**
	 * Enhanced ReentrantLock that tracks last access time for expiration management.
	 * Automatically updates timestamp on any lock operation.
	 */
	protected static class LockEntry extends ReentrantLock{
		private static final long serialVersionUID = 1L;
		
		/** Last access timestamp for expiration tracking */
		volatile long lockTime = -1;

		/** Updates the last access timestamp */
		private void touch() {
			lockTime = System.currentTimeMillis();
		}

		@Override
		public void lock() {
			touch();
			super.lock();
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			touch();
			super.lockInterruptibly();
		}

		@Override
		public boolean tryLock() {
			touch();
			return super.tryLock();
		}

		@Override
		public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
			touch();
			return super.tryLock(timeout, unit);
		}

		@Override
		public Condition newCondition() {
			touch();
			return super.newCondition();
		}

		/**
		 * Checks if this lock entry has expired based on last access time.
		 * @param maxTimeMs Maximum allowed idle time in milliseconds
		 * @return true if the lock has expired
		 */
		boolean isExpired(long maxTimeMs) {
			return maxTimeMs > 0 && lockTime > 0 && System.currentTimeMillis() - lockTime > maxTimeMs;
		}
	}

	/**
	 * Creates a ReentranLockPerObject with default settings:
	 * - maxLockedObjects: 1000
	 * - maxLockTimeMs: -1 (no expiration)
	 * - idleLockTimeMs: -1 (auto-calculated)
	 */
	public ReentranLockPerObject() {
		this(1000, -1, -1);
	}

	/**
	 * Creates a ReentranLockPerObject with custom configuration.
	 * 
	 * @param maxLockedObjects Maximum number of concurrent locked objects (minimum: 1)
	 * @param maxLockTimeMs Maximum lock lifetime in milliseconds (minimum: 1000, -1 for no limit)
	 * @param idleLockTimeMs Cleanup interval in milliseconds (auto-calculated if -1)
	 */
	public ReentranLockPerObject(int maxLockedObjects, long maxLockTimeMs, long idleLockTimeMs) {
		// Ensure minimum values for configuration parameters
		this.maxLockedObjects = Math.max(1, maxLockedObjects);
		this.maxLockTimeMs = Math.max(1000, maxLockTimeMs);
		this.semaphore = new Semaphore(maxLockedObjects);
		
		// Auto-calculate cleanup interval if not specified
		this.idleLockTimeMs = Math.max(maxLockTimeMs / 10, Math.max(10, idleLockTimeMs));
		
		// Create daemon cleanup thread
		this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "LockCleanup");
			t.setDaemon(true);
			return t;
		});

		// Schedule periodic cleanup
		cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredLocks, this.idleLockTimeMs, this.idleLockTimeMs,
				TimeUnit.MILLISECONDS);

		// Log initialization
		logger.info(String.format("ReentranLockPerObject initialized: maxObjects=%d, maxLockTime=%dms, idleTime=%dms", 
				this.maxLockedObjects, this.maxLockTimeMs, this.idleLockTimeMs));
	}

	/**
	 * Gets existing lock or creates new one for the given object.
	 * Thread-safe operation using ConcurrentHashMap.
	 * 
	 * @param obj Object to get/create lock for
	 * @return LockEntry for the object
	 */
	protected final LockEntry getOrCreateLockEntry(Object obj) {
		return locks.computeIfAbsent(obj, k -> createLockEntry(obj));
	}

	/**
	 * Creates a new LockEntry with semaphore-based resource control.
	 * Blocks until a permit is available or timeout occurs.
	 * 
	 * @param obj Object for which to create the lock
	 * @return New LockEntry instance
	 * @throws IllegalStateException if max limit reached or interrupted
	 */
	protected LockEntry createLockEntry(Object obj) {
		try {
			// Try to acquire semaphore permit with timeout
			if (!semaphore.tryAcquire(maxLockTimeMs, TimeUnit.SECONDS)) {
				throw new IllegalStateException("Max locked objects limit reached: " + maxLockedObjects);
			}
			return new LockEntry();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("InterruptedException on locked objects limit reached: " + maxLockedObjects,
					e);
		}
	}

	/**
	 * Removes expired and unlocked entries from the lock map.
	 * Called periodically by the cleanup executor.
	 * Releases semaphore permits for removed entries.
	 */
	protected void cleanupExpiredLocks() {
		if (locks.isEmpty()) {
			return;
		}
		
		int beforeSize = locks.size();
		
		// Remove expired and unlocked entries
		if(locks.entrySet().removeIf(entry -> !entry.getValue().isLocked() && entry.getValue().isExpired(idleLockTimeMs))) {
			// Release semaphore permit for removed entry
			semaphore.release();
		}
		
		int afterSize = locks.size();
		
		// Debug logging of cleanup results
		if (logger.isLoggable(Level.FINE)) {
			logger.fine(String.format("Cleanup: locks before=%d, after=%d, available permits=%d", 
					beforeSize, afterSize, semaphore.availablePermits()));
		}
	}

	/**
	 * Shuts down the cleanup executor and releases resources.
	 * Should be called when the lock manager is no longer needed.
	 */
	private void shutdown() {
		if (cleanupExecutor != null) {
			cleanupExecutor.shutdown();
		}
	}

	/** @return Maximum number of concurrent locked objects */
	public int getMaxLockedObjects() {
		return maxLockedObjects;
	}

	/** @return Maximum lock lifetime in milliseconds */
	public long getMaxLockTimeMs() {
		return maxLockTimeMs;
	}

	/**
	 * Gets a ReentrantLock for the specified object.
	 * Creates a new lock if one doesn't exist.
	 * 
	 * @param entry Object to get lock for
	 * @return ReentrantLock instance for the object
	 * @throws IllegalStateException if max locked objects limit is reached
	 */
	public ReentrantLock of(Object entry) {
		return getOrCreateLockEntry(entry);
	}

	@Override
	public void close() throws Exception {
			shutdown();	
	}
}
