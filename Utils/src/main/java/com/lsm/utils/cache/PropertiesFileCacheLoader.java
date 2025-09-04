package com.lsm.utils.cache;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.lsm.common.AppException;
import com.lsm.utils.StreamUtils;

public class PropertiesFileCacheLoader implements ICacheLoader<String> {

	private final String filePath;

	public PropertiesFileCacheLoader(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public Map<String, String> load() {

		try {
			new Properties().load(StreamUtils.getResourceAsStream(filePath));
		} catch (IOException e) {
			
			throw new AppException("Failed to load properties file {} on {}", filePath, e);
		}
		return null;
	
	}
}
