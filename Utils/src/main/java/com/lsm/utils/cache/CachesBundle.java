package com.lsm.utils.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public enum CachesBundle {
	INST, //refresh 
	STATIC; // no refresh
	private Map<String, Map<?, ?>> caches = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public <K,V> Map<K, V> get(String cache){
		return  (Map<K, V>)get(cache,c-> new SoftCacheMap<>());
	}

	@SuppressWarnings("unchecked")
	public <K,V> Map<K, V> get(String cache,Function< String,  Map<?, ?>> mappingFunction){
		return  (Map<K, V>) caches.computeIfAbsent(cache , c -> mappingFunction.apply(c)  );
	}
	
	@SuppressWarnings("unchecked")
	public <K,V> Map<K, V> get(String cache,Supplier<  Map<?, ?>> mappingFunction){
		return  (Map<K, V>) caches.computeIfAbsent(cache , c -> mappingFunction.get() );
	}
	
	public void put(String type,Map<?, ?> cache){
  		caches.put( type,cache);
	}
	
	public void clear() {
		caches.clear();
	}
}
