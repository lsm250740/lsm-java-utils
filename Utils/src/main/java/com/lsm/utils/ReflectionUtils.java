package com.lsm.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.lsm.utils.cache.CacheMap;
import com.lsm.utils.cache.SoftCacheMap;

public class ReflectionUtils {
	private static Logger log = LogManager.getLogger(ReflectionUtils.class);
	public static final String DEFAULT_PACKAGES_PREFIX = "com.lsm";
	private static final CacheMap<Class<?>, Object> subTypesCache = new SoftCacheMap<>();
	private static final CacheMap<Class<?>, CacheMap<String, Field>> fieldsCache = new SoftCacheMap<>();
	private static final CacheMap<Class<?>, Set<Field>> beanCache = new SoftCacheMap<>();

	public static final Class<?> classFor(Type type) {
		try {
			return Class.forName(type.getTypeName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found " + type.getTypeName(), e);
		}
	}

	public static final Class<?> classFor(String type) {
		try {
			return Class.forName(type);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found " + type, e);
		}
	}

	/**
	 * returns reall class type even for Anonymous classes
	 * 
	 * @param type
	 * @return
	 */
	public static final Class<?> classFor(Object type) {
		Class<?> cls = type.getClass();
		return cls.isAnonymousClass() ? cls.getInterfaces().length == 0 ? cls.getSuperclass() : cls.getInterfaces()[0]
				: cls;
	}

	public static final <T> T newInstance(Class<T> entity) {
		try {
			log.debug("newInstance({})", entity);
			return entity.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("Failed to create new instance " + entity.getSimpleName());
		}
	}

	public static final <T> T newInstance(Class<T> entity, Class<?>[] parameterTypes, Object... initargs) {
		try {
			return entity.getDeclaredConstructor(parameterTypes).newInstance(initargs);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Failed to create new instance " + entity.getSimpleName(),
					Optional.ofNullable(e.getCause()).orElse(e));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Failed to create new instance " + entity.getSimpleName(), e);
		}
	}

	public static final <T> Optional<T> buildInstance(Class<T> entity, Class<?>[] parameterTypes, Object... initargs) {
		try {
			return Optional.ofNullable(entity.getDeclaredConstructor(parameterTypes).newInstance(initargs));
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Failed to create new instance " + entity.getSimpleName(),
					Optional.ofNullable(e.getCause()).orElse(e));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException
				| SecurityException e) {
			return Optional.empty();
		}
	}

	/**
	 * Build Proxy instance of specified interface Builds bean get methods only
	 * 
	 * @param <T>            interfaceType
	 * @param instanceValues Map of method name and its value
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T> T buildProxy(Class<? extends T> interfaceType, Map<String, Object> instanceValues) {
		return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class<?>[] { interfaceType },
				(proxy, method, args1) -> instanceValues.get(method.getName()));
	}

	/**
	 * Build Proxy instance of specified interface Builds bean methods only
	 * 
	 * @param <T> interfaceType param instanceValues Map of method name and its
	 *            value for sample "getName", "Failed","getCode", -1
	 * @return
	 */

	public static <T> T buildProxy(Class<? extends T> interfaceType, Object... beanValues) {
		return buildProxy(interfaceType, CollectionsUtil.mapOf(beanValues));
	}

	@SuppressWarnings("unchecked")
	public static <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
		Set<Class<? extends T>> set = (Set<Class<? extends T>>) subTypesCache.computeIfAbsent(type,
				t -> getSubTypesOf(DEFAULT_PACKAGES_PREFIX, type));
		return set;
	}

	@SuppressWarnings("rawtypes")
	/**
	 * Usefull only on SubTypes with non Generic types only
	 * 
	 * @param <T>
	 * @param instance
	 * @return
	 */
	public static <T> List<Class> getGenericTypesOf(T instance) {
		Type[] types = ((ParameterizedType) instance.getClass().getGenericSuperclass()).getActualTypeArguments();

		// Type[] types =
		// ((ParameterizedType[])instance.getClass().getTypeParameters()).getActualTypeArguments();
		if (types != null) {
			return List.of(types).stream().filter(t -> t instanceof Class).map(t -> (Class) t)
					.collect(Collectors.toList());
		}
		return null;

	}

	public static <T> Class<? extends T> getFirstSubTypeOf(Class<T> type) {
		Set<Class<? extends T>> classes = getSubTypesOf(type);
		if (classes.size() == 0) {
			throw new RuntimeException("Problem to get subtype of " + type);
		}
		return classes.iterator().next();
	}

	public static <T> Set<Class<? extends T>> getSubTypesOf(String pck, Class<T> type) {
		log.debug("getSubTypesOf({}, {})", pck, type);
		ConfigurationBuilder config = ConfigurationBuilder.build(pck);
		config.setInputsFilter(s -> s.endsWith(".class"));
		return new Reflections(config).getSubTypesOf(type);

	}

	public static Set<Class<?>> getSuperTypes(Class<?> type) {
		return org.reflections.ReflectionUtils.getSuperTypes(type);
	}

	@SuppressWarnings("unchecked")
	public static Set<Class<?>> getAllSuperTypes(Class<?> type) {
		return org.reflections.ReflectionUtils.getAllSuperTypes(type);
	}

	@SuppressWarnings("unchecked")
	public static Set<Field> getAllFields(Class<?> type) {
		return org.reflections.ReflectionUtils.getAllFields(type);
	}

	@SuppressWarnings("unchecked")
	public static Set<Field> getBeanFields(Class<?> type) {
		return beanCache.computeIfAbsent(type, key -> {
			final Set<String> methods = org.reflections.ReflectionUtils.getAllMethods(type).stream()
					.filter(m -> m.getName().matches("(get|is).*") && Modifier.isPublic(m.getModifiers())
							&& m.getParameterCount() == 0)
					.map(f -> StringUtils.uncapitalize(f.getName().replaceFirst("get|is", "")))
					.collect(Collectors.toSet());

			return ReflectionUtils.getAllFields(type).stream().filter(f -> methods.contains(f.getName()))
					.collect(Collectors.toSet());
		});
	}

	public static Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> type) {
		Reflections reflections = new Reflections(DEFAULT_PACKAGES_PREFIX);
		return reflections.getTypesAnnotatedWith(type);
	}

	public static Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> type) {
		ConfigurationBuilder builder = new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage(DEFAULT_PACKAGES_PREFIX))
				.setScanners(new MethodAnnotationsScanner());
		builder.setInputsFilter(s -> s.endsWith(".class"));
		Reflections reflections = new Reflections(builder);
		return reflections.getMethodsAnnotatedWith(type);
	}

	public static Set<Field> getFieldsAnnotatedWith(Class<? extends Annotation> type) {
		ConfigurationBuilder builder = new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage(DEFAULT_PACKAGES_PREFIX))
				.setScanners(new FieldAnnotationsScanner());
		Reflections reflections = new Reflections(builder);
		return reflections.getFieldsAnnotatedWith(type);
	}

	public static Set<Field> getFieldsAnnotatedWith(Class<?> entityClass, Class<? extends Annotation> type) {
		return getAllFields(entityClass).stream().filter(field -> field.getAnnotation(type) != null)
				.collect(Collectors.toSet());
	}

	public static Field getField(Class<?> entity, String fieldName) {
		return fieldsCache.computeIfAbsent(entity, m -> new SoftCacheMap<String, Field>()).computeIfAbsent(fieldName,
				fld -> {
					Field field = getAllFields(entity).stream().filter(f -> f.getName().equals(fieldName)).findAny()
							.orElseThrow(() -> new IllegalArgumentException(
									"Field " + fieldName + " not exists in  " + entity.getCanonicalName()));
					field.setAccessible(true);
					return field;
				});
	}

	/**
	 * Returns the value of a (nested) field on a bean.
	 * 
	 * @param bean      the object
	 * @param fieldName the field name, with "." separating nested properties
	 * @return the value
	 */
	public static <T> Object getFieldValue(T entity, String fieldName) {
		return getFieldValue(entity, fieldName, false);
	}

	/**
	 * Returns the value of a (nested) field on a bean.
	 * 
	 * @param bean      the object
	 * @param fieldName the field name, with "." separating nested properties
	 * @param fullPath  creates new instance on upper nested properties
	 * @return the value
	 */
	@SuppressWarnings("unchecked")
	public static <T> Object getFieldValue(T entity, String fieldName, boolean fullPath) {
		if (entity instanceof Map) {
			return getFieldValueFromMap((Map<String, Object>) entity, fieldName.trim(), fullPath);
		}

		final String[] nestedField = StringUtils.split(fieldName.trim(), ".", 2);
		return Optional.ofNullable(getFieldValuefromEntity(entity, nestedField[0].trim(), fullPath))
				.map(v -> nestedField.length == 2 ? getFieldValue(nestedField[2], nestedField[2], fullPath) : v)
				.orElse(null);

	}

	private static <T> Object getFieldValuefromEntity(T entity, String fieldName, boolean fullPath) {
		try {
			Field field = ReflectionUtils.getField(entity.getClass(), fieldName.trim());
			return Optional.ofNullable(field.get(entity)).orElseGet(() -> {
				if (fullPath) {
					return ReflectionUtils.newInstance(field.getType());
				}
				return null;
			});
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException("illegal field " + fieldName + " on type " + entity.getClass(), e);
		}
	}

	private static <T> Object getFieldValueFromMap(Map<String, Object> entity, String fieldName, boolean fullPath) {
		if (entity.containsKey(fieldName) || !fieldName.contains(".")) {
			return ((Map<String, Object>) entity).get(fieldName);
		}
		String[] nestedField = StringUtils.split(fieldName, ",", 2);
		if (StringUtils.isBlank(nestedField[2])) {
			throw new IllegalAccessError("Illegal field name " + fieldName);
		}
		return Optional.ofNullable(entity.get(nestedField[0]))
				.map(v -> getFieldValue(v, nestedField[1].trim(), fullPath)).orElse(null);
	}

	public static void setFieldValue(Object entity, String fieldName, Object value) {
		try {
			if (fieldName.contains(".")) {
				entity = getFieldValue(entity, StringUtils.substringBeforeLast(fieldName, "."), true);
				fieldName = StringUtils.substringAfterLast(fieldName, ".");
			}
			ReflectionUtils.getField(entity.getClass(), fieldName).set(entity, value);
		} catch (IllegalAccessException e) {
			throw new IllegalAccessError(
					"Field " + fieldName + " not exists in  " + entity.getClass().getCanonicalName());
		}
	}

	/**
	 * ClassLoader Scanner
	 * 
	 * @param classLoader
	 * @param pkg
	 * @return
	 */
	public static Set<Class<? extends Object>> classesOfPackage(String pkg) {
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().filterInputsBy(new FilterBuilder().includePackage(pkg))
						.setUrls(ClasspathHelper.forPackage(pkg)).setScanners(new SubTypesScanner(false)));

		Set<Class<?>> subTypesOf = reflections.getSubTypesOf(Object.class);
		return subTypesOf;
	}

	/**
	 * 
	 * @param pkg  packages for scanning Entity classes
	 * @param Type annotation
	 * @return Set of classes annotated with specified Annotation
	 */
	public static Set<Class<? extends Object>> classesOfAnnotation(String pkg, Class<? extends Annotation> annotation) {
		return classesOfPackage(pkg).stream().filter(e -> e.getAnnotation(annotation) != null)
				.collect(Collectors.toSet());

	}

	public static <T> Method method(Class<?> entity, String methodName, Class<?>... args) {
		try {
			Method method = entity.getDeclaredMethod(methodName, args);
			method.setAccessible(true);
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			throw new UnsupportedOperationException("Unsupported method " + methodName + " on " + entity.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	public final static <T> T getValue(Field fld) {
		try {
			return (T) fld.get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			if (Modifier.isStatic(fld.getModifiers())) {
				throw new IllegalAccessError("Field " + fld.getName() + " failes access value");
			}
			throw new IllegalAccessError("Field " + fld.getName() + " is not static");
		}
	}

	@SuppressWarnings("unchecked")
	public final static <T> T getValue(Field fld, Object instance) {
		try {
			return (T) fld.get(instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalAccessError("Field " + fld.getName() + " is not static");
		}
	}
}
