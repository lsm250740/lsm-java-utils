package com.lsm.utils.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.lsm.utils.cache.TimeBasedCache.ValuesReference;

public class CacheConfiguration {
 
  private String name;
 
  private TimeUnit timeUnit;
  
  private int initSize;
  
  private int maxSize;
  
  private ValuesReference valueStrength;
  
  private ValuesReference keyStrength;
  
  private long maxWeight;
  
 
	/**
	 * Specifies that each entry should be automatically removed from the cache once
	 * a fixed duration has elapsed after the entry's creation, the most recent
	 * replacement of its value, or its last access. Access time is reset by all
	 * cache read and write operations, but not by {@code
	 * containsKey(Object)}, nor by operations on the collection-views of
	 * {@link Cache#asMap}}.
	 */
  private long timeExpire;

	
	public <T> T getValue(String name ) {
		return null;
	}

  
  public static CacheConfiguration of(String name) {
	  return null;
  }
  
  
}
