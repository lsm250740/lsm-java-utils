package com.lsm.utils;

import static com.lsm.utils.SanitizeUtil.sanitizeHtml;
import static com.lsm.utils.cache.ObjectMappersUtils.baseMapper;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import com.lsm.encryptor.CryptoException;
//import com.migdal.ent.common.utils.json.JsonMappersCache;
import com.lsm.encryptor.aes.AESEncryptorUtil;


/**
 * @author omryk
 *
 */
public class PropsUtil{
	private static final Logger logger = LoggerFactory.getLogger(PropsUtil.class);
	private final static ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
	public final static String LIST_DELIMITER = "[\\s]*(,|;)[\\s]*";
	public final static String STRING_NAME_DELIMITER = "[\\s]*\\.[\\s]*";
	public final static String NAME_CONVERTER_PATTERN = "(?<!^)([a-z])([A-Z])";
	public final static String REGEX_NUMBER = "[^\\d.]"; // commonly string parameter has "," delimiter
	private final static String DEFAULT_PROPERTIES_SOURCE = "./general.properties";
	private final static Date startupTime = new Date();
	private static Properties defaultProperties = new Properties();
	private static Map<String,Object> loadedProperties =  Collections.synchronizedMap(new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
	private final static String hostName = setHostName();
	private static Environment environment;

	public enum Environment{
		prod, pre, test, dev;

		private static final Environment toEnum(String environment){
			return environment == null ? Environment.prod
					: Arrays.stream(Environment.values())
							.filter(type -> type.name().equalsIgnoreCase(environment))
							.findAny().orElse(Environment.prod);
		}

	}

	static{
		loadedProperties = loadDefaultProperties();
	}

	public static Map<String,Object> loadDefaultProperties(){
		logger.debug("loadDefaultProperties - START");
		final Map<String,Object> refresh =  Collections.synchronizedMap(new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
		try{
		
			defaultProperties.load(StreamUtils.getResourceAsStream(DEFAULT_PROPERTIES_SOURCE));
			logger.debug("loadDefaultProperties loaded");
			environment = Environment.toEnum( defaultProperties.getProperty("environment"));
			logger.debug("environment is set");
			defaultProperties.entrySet().forEach( e ->refresh.put((String) e.getKey(), e.getValue()));
			logger.debug("properties inited");
		}catch(IOException e){
			logger.error("Error Loading General Properties", e);
		}catch(NullPointerException e){
			logger.error("Failed to find general.properties resource",e);
		}
		logger.debug("loadDefaultProperties - FINISHED");
		return refresh;
	}

	public static void refresh(final Map<String, Object> all){
		cache.clear();
		final Map<String,Object> refresh =  loadDefaultProperties();
			Map<String, Object> filter = all.entrySet().stream()
				.filter(k -> !"environment".equalsIgnoreCase(k.getKey()) && !k.getKey().matches("^(?i)system\\..*$"))
				
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		refresh.putAll(filter);
		loadedProperties = refresh;
	}

	public static String getProperty(String key){
		try{
			String value = Optional.ofNullable(loadedProperties.get(key)).map(Object::toString).orElse(null);
		    //Optional.ofNullable(loadedProperties.get(key))
		    //  .map( v -> v instanceof String? (String)v: JsonMappersCache.wsObjectMapper.writeToString(v))
  	        //    .orElse(null);
			
			return sanitizeHtml(value);
		}catch(Exception e){
			return null;
		}
	}
   
	public static void putProperty(String key, Object value){
	try {	
		loadedProperties.put(key, value);
	  }catch (Exception e){
		 logger.error("Failed read property {} value {}",key,value );
	  }
	}

	/**
	 * Returns a property value when it is defined and property value is not empty.
	 * Otherwise returns defaultValue
	 * 
	 * @param key          - property name
	 * @param defaultValue - the property name
	 * @return value of the property
	 */
	public static String getProperty(String key, String defaultValue){
		try{
			return getPropertyAssertable(key);
		}catch(IllegalAccessError e){
			return defaultValue;
		}
	}

	/**
	 * Assert that a property is defined and property value is not empty.
	 * 
	 * @param key - String the property name
	 * @return value of the property
	 */
	public static String getPropertyAssertable(String key){
		try{
			String prp = getProperty(key);
			Assert.notEmpty(prp, key);
			return sanitizeHtml(prp);
		}catch(IllegalArgumentException e){
			throw new IllegalAccessError(key);
		}catch(Exception e){
			throw new IllegalAccessError("Faile to read property " + key + ":" + e.toString());
		}
	}

	public static Integer getIntProperty(String key){
		try{
			String value = getProperty(key);
			return StringUtils.isNumeric(value) ? Integer.valueOf(value) : 0;
		}catch(Exception e){
			logger.error("Property " + key + "illegal value", e);
			return 0;
		}
	}

	public static Double getDoubleProperty(String key){
		return getDoubleProperty(key, 0.0);
	}

	public static Double getDoubleProperty(String key, double defaultValue){
		try{
			String value = getProperty(key);
			return value == null ? defaultValue : Double.valueOf(value);
		}catch(Exception e){
			logger.error("Property " + key + "illegal value", e);
			return defaultValue;
		}
	}

	public static Integer getIntProperty(String key, Integer defaultValue){
		try{
			String value = getProperty(key);
			return value == null ? defaultValue : getIntProperty(key);
		}catch(Exception e){
			logger.error("Property " + key + "illegal value", e);
			return defaultValue;
		}
	}

	/*public static <T> T ofProperty(String name, Function<String, ? extends T> mappingFunction){
		if(loadedProperties.containsKey(name) ){
			return mappingFunction.apply(name);
		}
		return null;
	}
	
	public static <T> T ofProperty(String name, String defaultName, Function<String, ? extends T> mappingFunction){
		if(loadedProperties.containsKey(name) || !loadedProperties.containsKey(defaultName)){
			return mappingFunction.apply(name);
		}
		return mappingFunction.apply(defaultName);
	}*/

	public static Long getLongProperty(String key){
		try{
			String value = getProperty(key);
			return StringUtils.isNumeric(value) ? Long.valueOf(value) : 0L;
		}catch(Exception e){
			logger.error("Property " + key + "illegal value", e);
			return 0L;
		}

	}

	public static Long getLongProperty(String key, long defaultValue){
		try{
			String value = getProperty(key);
			return value == null ? defaultValue : getLongProperty(key);
		}catch(Exception e){
			logger.error("Property " + key + "illegal value", e);
			return 0L;
		}

	}

	public static boolean getBooleanProperty(String key){
		return getBooleanProperty(key, false);
	}

	public static boolean getBooleanProperty(String key, boolean defaultValue){
		try{
			return Optional.ofNullable(PropsUtil.getProperty(key)).map(str -> Boolean.parseBoolean(str))
					.orElse(defaultValue);
		}catch(NumberFormatException e){
			logger.error("Error parsing property key [" + key + "]");
		}

		return defaultValue;
	}

	public static <T> void setProperty(String key, T property){
		loadedProperties.put(key, property);

	}

	public static boolean contansProperty(String key){
		return getProperty(key) != null;
	}

	public static void printAll(){
		defaultProperties.forEach((k, v) -> logger.info(k + "=" + v));
		loadedProperties.forEach((k, v) -> logger.info(k + "=" + v));
	}
	
	public static Properties getAllProperties(){
		Properties props = new Properties(defaultProperties);
		props.putAll(loadedProperties);
		return props;
	}	

    public static Date getStartupTime(){
		return startupTime;
	}

	public static String getHostName(){
		return hostName;
	}

	private static final String setHostName(){
		try{
			return InetAddress.getLocalHost().getHostName();
		}catch(UnknownHostException e){
			logger.error("failed get hostName ", e);
		}
		return "undefined";
	}

	public static Float getFloatProperty(String key){
		String val = getProperty(key);
		if(val != null){
			try{
				return Float.parseFloat(val.toString());
			}catch(NumberFormatException e){
				logger.error("Error parsing property key [" + key + "]");
			}
		}

		return null;
	}

	public static Float getFloatProperty(String key, Float defaultValue){
		String str = PropsUtil.getProperty(key);
		if(str != null){
			try{
				return Float.parseFloat(str);
			}catch(NumberFormatException e){
				logger.error("Error parsing property key [" + key + "]");
			}
		}

		return defaultValue;
	}

	public static String getPassword(String propName){
		return (String) cache.computeIfAbsent(propName, key -> {
			try{
				logger.debug("get password [" + propName + "]");
				String prop = PropsUtil.getPropertyAssertable(propName);
				logger.debug("password [" + propName + "] = prop. Try do decrypt. ");
				return AESEncryptorUtil.aes256.decrypt(prop);
			}catch(CryptoException e){
				throw new RuntimeException("Error to decrypt propery " + propName);
			} finally {
				logger.debug("password [" + propName + "] decrypted");
			}
		});
	}

	public static Pattern getPattern(String property){
		if(PropsUtil.contansProperty(property)){
			return (Pattern) cache.computeIfAbsent(property, key -> {
				try{
					return java.util.regex.Pattern.compile(PropsUtil.getProperty(property));
				}catch(Exception e){
					logger.error(String.join(" ", "Failed to compile", property, " pattern ",
							PropsUtil.getProperty(property)),
							e);
					return null;
				}
			});
		}
		return null;
	}

	/**
	 * environment property never updatable
	 */
	public final static Environment getEnvironment(){
		return environment;
	}

	public final static boolean isProdEnvironment(){
		return environment == Environment.prod;
	}

	/**
	 * from json to array
	 * 
	 * @param key
	 * @return
	 */
	public static List<String> getListProperty(String propertyName){
		return configurationOf(propertyName, new TypeReference<List<String>>() {
		});
	}

	/**
	 * from json for array getConfigurationProperty( prpName,new
	 * TypeReference<List<String>>()
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T configurationOf(String property, TypeReference<T> config){
		return (T) cache.computeIfAbsent(property, key -> {
			try{
				String json = getProperty(property);
				if(isBlank(json)){
					return null;
				}

				return baseMapper.convert(json, config);
			}catch(Throwable e){
				logger.error("Failed to desirialize property :" + property, e);
			}

			return null;
		});
	}

	/**
	 * from json to Entity
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T configurationOf(String property, Class<T> config){
		return (T) cache.computeIfAbsent(property, key -> {
			try{
				String json = getProperty(property);
				if(isBlank(json)){
					return null;
				}
				return baseMapper.convert(json, config);
			}catch(Throwable e){
				logger.error("Failed to desirialize property :" + property, e);
			}
			return null;
		});
	}


	/**
	 * from json to Entity
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T numberOf(String property, Class<T> config){
		return (T) cache.computeIfAbsent(property, key -> {
			try{
				String number = getProperty(property);
				if(isBlank(number)){
					return null;
				}
				return ReflectionUtils.method(config, "valueOf", String.class).invoke(null,number);
			}catch(Throwable e){
				logger.error("Failed to desirialize property :" + property, e);
			}
			return null;
		});
	}

	
	/**
	 * from json to Map
	 * 
	 * @param key
	 * @return

	public static Map<String, Object> mapOf(String propertyName){
		return configurationOf(propertyName, new TypeReference<Map<String, Object>>() {
		});
	}*/

	public static boolean isPropertyContains(String propertyName, String value){
		if(!isBlank(value)){
			return Optional.ofNullable(getProperty(propertyName))
					.map(val -> Arrays.asList(val.split(LIST_DELIMITER)).indexOf(value) >= 0).orElse(false);
		}
		return false;
	}

	public static Optional<String> of(String propertyName){
		return Assert.ofEmpty(getProperty( propertyName));
	}

	
    public static String asName(Class<?> type) {
    	return asName(type.getSimpleName());
    }
    
    public static String asName(String entityName) {
    	return entityName.replaceAll(NAME_CONVERTER_PATTERN, "$1_$2");
    }
    
    @SuppressWarnings("unchecked")
	public static<T> T valueOf(String prpName){
     	return (T) loadedProperties.get(prpName);
    }
     
}
