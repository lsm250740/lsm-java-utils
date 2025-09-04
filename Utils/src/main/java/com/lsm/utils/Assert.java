package com.lsm.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNoneEmpty;

import java.util.Optional;
/**
 * Assertion utility class that assists in validating arguments
 * <p> Simply it is to bothered validate arguments with if-then statements, when exception should be thrown 
 * <p> By default IllegalArgumentException is thrown
 * 
 * @author olgaso
 */
public class Assert {

	/**
	 * Assert a boolean expression, throwing {@code IllegalArgumentException}
	 * if the test result is {@code false}.
	 * @param expression a boolean expression
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if expression is {@code false}
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void isTrue(boolean expression, RuntimeException e) {
		if (!expression) {
			throw e;
		}
	}
	
	/**
	 * Assert a boolean expression, throwing {@code IllegalArgumentException}
	 * if the test result is {@code false}.
	 * @param expression a boolean expression
	 * @throws IllegalArgumentException if expression is {@code false}
	 */
	public static void isTrue(boolean expression) {
		isTrue(expression, "This expression must be true");
	}

	/**
	 * Assert that an object is  {@code null} .
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is not {@code null}
	 */
	public static void notNull(Object object, Object... message) {
		if (object == null) {
			if(message==null) {
				throw new IllegalArgumentException("Null is illegal value");
			}
			throw new IllegalArgumentException(CollectionsUtil.join( " ",message));
		}
	}
	
	/**
	 * Optional that the given String has not be null and must contain at least one non-whitespace character.
	 * @param text the String to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the text does not contain valid text content
	 */
	public static Optional<String> ofEmpty(String text) {
		if (isBlank(text)) {
			return Optional.empty();
		}
		return Optional.of(text);
	}
	
	
	/**
	 * Assert that the given String has not be null and must contain at least one non-whitespace character.
	 * @param text the String to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the text does not contain valid text content
	 */
	public static void notEmpty(String text, Object... message) {
		if (isBlank(text)) {
			if(message==null) {
				throw new IllegalArgumentException("Null, empty, or blank is illegal value");
			}
			throw new IllegalArgumentException(CollectionsUtil.join( " ",message));
		}
	}

	/**
	 * Assert that the given text does not contain the given substring.
	 * @param textToSearch the text to search
	 * @param substring the substring to find within the text
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the text contains the substring
	 */
	public static void isNotContain(String textToSearch, String substring, Object... message) {
		if (isNoneEmpty(textToSearch)  && textToSearch.contains(substring)) {
			if(message==null) {
				throw new IllegalArgumentException("This String argument must not contain the substring [\" + substring + \"]\"");
			}
			throw new IllegalArgumentException(CollectionsUtil.join( " ",message));
		}
	}

	
	/**
	 * Assert that the provided object is an instance of the provided class.
	 * @param type the type to check against
	 * @param obj the object to check
	 * @param message a message which will be prepended to the message produced by
	 * the function itself
	 * @throws IllegalArgumentException if the object is not an instance of clazz
	 * @see Class#isInstance
	 */
	public static void isInstanceOf(Class<?> type, Object obj, Object... message) {
		notNull(type, "Type to check against must not be null");
		if (!type.isInstance(obj)) {
			IllegalArgumentException exception =new IllegalArgumentException("Object of class [" + (obj != null ? obj.getClass().getName() : "null") +
					"] must be an instance of " + type);
			if(message==null) {
				throw exception;
			}
			throw new IllegalArgumentException(CollectionsUtil.join( " ",message),	exception);
		}
	}

	/**
	 * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
	 * @param superType the super type to check
	 * @param subType the sub type to check
	 * @throws IllegalArgumentException if the classes are not assignable
	 */
	public static void isAssignable(Class<?> superType, Class<?> subType) {
		isAssignable(superType, subType, "");
	}

	/**
	 * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
	 * @param superType the super type to check against
	 * @param subType the sub type to check
	 * @param message a message which will be prepended to the message produced by
	 * the function itself
	 * appended to it.
	 * @throws IllegalArgumentException if the classes are not assignable
	 */
	public static void isAssignable(Class<?> type, Object obj, Object... message) {
		notNull(type, "Type to check against must not be null");
		if (!type.isInstance(obj)) {
			IllegalArgumentException exception =new IllegalArgumentException("Object of class [" + (obj != null ? obj.getClass().getName() : "null") +
					"] must be assignable from " + type);
			if(message==null) {
				throw exception;
			}
			throw new IllegalArgumentException(CollectionsUtil.join( " ",message),	exception);
		}
	}
}
