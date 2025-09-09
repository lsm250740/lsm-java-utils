package com.lsm.junit.thread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * approves that static block in initialization also is thread-safe 
 */
public class DummySlowCache {
	private  final static Logger log = LoggerFactory.getLogger(DummySlowCache.class);
	private final static Map<String, Object> cache ;
	static {
		cache =  init();
	}
		
	static Map<String, Object> init() {
			try {
			log.info("DummySlowCache Start Init ");
			DummyThread.build(16000, 6000).run();
		}catch(Throwable e) {
			e.printStackTrace();
		
		} finally {
			log.info("DummySlowCache Done Init ");
		}
		return new ConcurrentHashMap<String, Object>();
	}

	public static  Object get(String key) {
		return cache.get(key);
	}

	public static void put(String key, Object value) {
		cache.put(key, value);
	}

	public static void putAll(Map<String, Object> m) {
		cache.putAll(m);
	}

	public static void clear() {
		cache.clear();
	}
	
	public static Map<String, Object> copy() {
		return new ConcurrentHashMap<String, Object>(cache);
	}
}
