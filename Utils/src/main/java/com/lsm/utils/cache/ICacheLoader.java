package com.lsm.utils.cache;

import java.util.Map;

import com.lsm.common.Lazy;

@Lazy
public interface ICacheLoader<T> {
	public Map<String, T> load();

	/**
	 * On refresh should be reloaded
	 * 
	 * @return
	 */
	public default T load(String key) {
		throw new UnsupportedOperationException("Looad by key not supported");
	};

}
