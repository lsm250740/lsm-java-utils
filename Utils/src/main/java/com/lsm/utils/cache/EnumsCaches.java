package com.lsm.utils.cache;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.lsm.utils.ReflectionUtils;

public enum EnumsCaches{
	Status,
	StateType,
	ResourceType	;

	private final Map<Class<?>, Set<?>> caches = new ConcurrentHashMap<>();
	@SuppressWarnings("unchecked")
	public<T> Set<T> values(Class<?> type){
		return (Set<T>) caches.computeIfAbsent(type, c -> build(type));
	}
	
	
	@SuppressWarnings("unchecked")
	private <I> Set<I> build(Class<I> type){
		Set<I> set = (Set<I>) ReflectionUtils.getAllFields(type)
				.stream()
				.filter(f -> Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
				.map(f -> {
					f.setAccessible(true);
					try{
						return f.get(type);
					}catch(IllegalArgumentException | IllegalAccessException e){
						// ignore
						return null;
					}
				})
				.filter(f -> f != null)
				.collect(Collectors.toSet());
		;
		//buildRealEnums(set);
		return set;
	}

	
	/*private synchronized  void buildRealEnums(Set<?> set){
		set.stream().collect(Collectors.groupingBy(c -> c.getClass()))
				.forEach((k, e) -> {
					Set b = Optional.ofNullable(caches.get(k))
							.map(v -> new HashSet<>(v)).orElse(new HashSet<>());
					b.addAll(e);
					caches.put(k, b);
				});

	}*/

	public void clear(){
		caches.clear();
	}

	@SuppressWarnings("unchecked")
	public<T> Set<T> values(Class<T> type, Supplier<Set<T>> supplier){
		return (Set<T>) caches.computeIfAbsent(type, c -> supplier.get());
	}
}
