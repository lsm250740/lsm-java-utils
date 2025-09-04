package com.lsm.utils.cache;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.lsm.utils.adapters.TimeUnitDeserializer;

public enum ObjectMappersUtils {

	baseMapper(baseMapper());
	private final ObjectMapper mapper;
	ObjectMappersUtils(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public <T> T convert(Object fromValue, Class<T> toValueType) {
		if ( fromValue instanceof String) {
			try{
				return mapper.readValue( (String)fromValue, toValueType);
			}catch(IOException e){
				throw new RuntimeException("Failed parce " );
			}
		}
		return mapper.convertValue(fromValue, toValueType);
	}

	public <T> List<T> convertToList(Object fromValue) {
		return convert(fromValue, new TypeReference<List<T>>() {});
	}
	
	

	public <K,V> Map<K,V> convertToMap(Object fromValue) {
		return convert(fromValue, new TypeReference<Map<K,V>>() {});
	}
	
	public <T> T convert(Object fromValue, TypeReference<T> toValueTypeRef) {
		if ( fromValue instanceof String) {
			try{
				return mapper.readValue( (String)fromValue, toValueTypeRef);
			}catch(IOException e){
				throw new RuntimeException("Failed parce " );
			}
		}
		return mapper.convertValue(fromValue, toValueTypeRef);
	}

	public <T> T convert(Object fromValue, JavaType toValueType)  {
		if ( fromValue instanceof String) {
			try{
				return mapper.readValue( (String)fromValue, toValueType);
			}catch(IOException e){
				throw new RuntimeException("Failed parce " );
			}
		}
		return mapper.convertValue(fromValue, toValueType);
	}

	public static final<T> TypeReference<T> typeReferenceOf() {
		return new TypeReference<T>() {};
	}
	
	public static final ObjectMapper baseMapper() {
		ObjectMapper mapper = JsonMapper.builder()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
//				.configure(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS, true)
//				.configure(MapperFeature.USE_ANNOTATIONS, false)
				.build();
		mapper.setSerializationInclusion(Include.NON_NULL);
		SimpleModule simpleModule = new SimpleModule("Common Mapper");
		simpleModule.addDeserializer(TimeUnit.class, new TimeUnitDeserializer());
		mapper.registerModule(simpleModule);
		return mapper;		
	}

}
