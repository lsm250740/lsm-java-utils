package com.lsm.utils.cache;

import static org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength.SOFT;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.collections4.map.ReferenceMap;

public class SoftCacheMap<K, V> implements CacheMap<K, V> {
	private final Map<K, V> cache = Collections.synchronizedMap(new ReferenceMap<K, V>(SOFT, SOFT));
	private volatile int maxEntries = Integer.MAX_VALUE;
	
	@Override
	public V put(K key, V value) {
		return cache.put(key, value);
	}

	@Override
	public V get(Object key) {
		return cache.get(key);
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return cache.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public int power() {
		return maxEntries;
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return cache.putIfAbsent(key, value);
	}

	/**
     *  iteration on keySet or values set is not thread-safe
     * <pre>
     *  Map m = Collections.synchronizedMap(new HashMap());
     *      ...
     *  Set s = m.keySet();  // Needn't be in synchronized block
     *      ...
     *  synchronized (m) {  // Synchronizing on m, not s!
     *      Iterator i = s.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
	 */
	@Override
	public Set<K> keySet() {
		return cache.keySet();
	}

	@Override
	public boolean isEmpty(){
		return cache.isEmpty();
	}

	@Override
	public boolean containsKey(Object key){
		return cache.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value){
		
		return cache.containsValue(value);
	}

	@Override
	public V remove(Object key){
		
		return cache.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m){
      cache.putAll(m);		
	}

	/**
     *  iteration on keySet or values set is not thread-safe
     * <pre>
     *  Map m = Collections.synchronizedMap(new HashMap());
     *      ...
     *  Set s = m.values();  // Needn't be in synchronized block
     *      ...
     *  synchronized (m) {  // Synchronizing on m, not s!
     *      Iterator i = s.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
	 */
	@Override
	public Collection<V> values(){
	
		return cache.values();
	}

	/**
     *  iteration on keySet or values set is not thread-safe
     * <pre>
     *  Map m = Collections.synchronizedMap(new HashMap());
     *      ...
     *  Set s = m.entrySet();  // Needn't be in synchronized block
     *      ...
     *  synchronized (m) {  // Synchronizing on m, not s!
     *      Iterator i = s.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
	 */
	@Override
	public Set<Entry<K, V>> entrySet(){
		
		return cache.entrySet();
	}

}
