package com.lsm.utils.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class TimeBasedCache<K, V>  implements Map<K, V>{

	public enum ValuesReference{
		STRONG, SOFT, WEAK;
	}

	private LoadingCache<K, V> cache;

	public TimeBasedCache(CacheConfiguration config, ValuesReference valuesReference) {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();

		if(valuesReference != null){
			if(valuesReference.equals(ValuesReference.SOFT)){
				builder = builder.softValues();
			}else if(valuesReference.equals(ValuesReference.WEAK)){
				builder = builder.weakValues();
			}
		}

		cache = builder.build(CacheLoader.from(k -> null));
	}

	public TimeBasedCache(long size, long duration, TimeUnit unit) {
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
		if(size > 0){
			builder = builder.maximumSize(size);
		}

		if(duration > 0){
			builder = builder.expireAfterAccess(duration, unit);
		}
		cache = builder.build(CacheLoader.from(k -> null));
	}

	public TimeBasedCache(long size) {
		this(size, -1, null);
	}

	public TimeBasedCache(long duration, TimeUnit unit) {
		cache = CacheBuilder.newBuilder().expireAfterAccess(duration, unit).build(CacheLoader.from(k -> null));
	}

	public TimeBasedCache(long duration, TimeUnit unit, ValuesReference valuesReference) {
		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().expireAfterAccess(duration, unit);
		if(valuesReference.equals(ValuesReference.SOFT)){
			cacheBuilder = cacheBuilder.softValues();
		}

		if(valuesReference.equals(ValuesReference.WEAK)){
			cacheBuilder = cacheBuilder.weakValues();
		}
		cache = cacheBuilder.build(CacheLoader.from(k -> null));
	}

	@Override
	public int size() {
		return cache.asMap().size();
	}

	@Override
	public V get(Object key) {
		return cache.asMap().get(key);
	}

	@Override
	public void clear() {
		cache.asMap().clear();
	}

	@Override
	public V put(K key, V value) {
		return cache.asMap().put(key, value);
	}

	@Override
	public V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> mappingFunction) {
		return cache.asMap().computeIfAbsent(key, mappingFunction);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return cache.asMap().putIfAbsent(key, value);
	}

	@Override
	public Set<K> keySet() {
		return cache.asMap().keySet();
	}

	public boolean containsKey(Object key) {
		return cache.asMap().containsKey(key);
	}

	public V containsValue(Object key, V defaultValue) {
		return cache.asMap().getOrDefault(key, defaultValue);
	}

	@Override
	public boolean isEmpty(){
		return cache.asMap().isEmpty();
	}

	@Override
	public boolean containsValue(Object value){
		return cache.asMap().containsValue(value);
	}

	@Override
	public V remove(Object key){
		return cache.asMap().remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m){
		cache.asMap().putAll(m);
	}

	@Override
	public Collection<V> values(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<K, V>> entrySet(){
		// TODO Auto-generated method stub
		return null;
	}

}
