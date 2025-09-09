package com.lsm.junit.thread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * approves that enum is singleton
 */
public enum DummySlowEnumCache {
		INST;
	private  final static	 Logger log = LoggerFactory.getLogger(DummySlowEnumCache.class);
	private final Map<String, Object> cache ;

	DummySlowEnumCache	(){
		cache = init();
	}
	private  Map<String, Object> init() {
			try {
			log.info("DummySlowEnumCache Start Init ");
			DummyThread.build(16000, 6000).run();
		}catch(Throwable e) {
			e.printStackTrace();
		
		} finally {
			log.info("DummySlowEnumCache Done Init ");
		}
		return new ConcurrentHashMap<String, Object>();
	}

	public Object get(String key) {
		return cache.get(key);
	}

	public void put(String key, Object value) {
		cache.put(key, value);
	}

	public void putAll(Map<String, Object> m) {
		cache.putAll(m);
	}

	public void clear() {
		cache.clear();
	}
	
	public Map<String, Object> copy() {
		return new ConcurrentHashMap<String, Object>(cache);
	}
}
