package com.lsm.utils;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Format message using all types of message format in Java Favorite libs: -
 * Java String format: @see java.lang.String.format() - Java MessageFormat
 * : @see java.text.MessageFormat - Log4j standard Library: @see
 * org.slf4j.helpers.MessageFormatter
 * 
 * That utility makes easy call for defferent formats but not mix them t
 * 
 * @author lsm
 */
public class MessageFormatUtil {

	private final static Pattern namePattern = Pattern.compile("\\{([^\\s}]+)\\}");

	/**
	 * Formats a message using multiple formatting approaches: 1. Java
	 * String.format() for %s, %d placeholders 2. Tuple formatting for {}
	 * placeholders 3. MessageFormat for {0}, {1} placeholders
	 * 
	 * It is impossible those place holders in the same message, because the
	 * arguments are not removed on insert them to the message
	 * 
	 * @param message the message template to format
	 * @param args    the arguments to substitute
	 * @return formatted message
	 */
	public static String format(String message, Object... args) {
		if (CollectionsUtil.isEmpty(args))
			return message;
		message = String.format(message, args);
		message = _formatByTuple(message, args);
		return formatMessage(message, args);
	}

	/**
	 * Formats a message using Java MessageFormat with indexed placeholders {0},
	 * {1}, etc.
	 * 
	 * @param message the message template with indexed placeholders
	 * @param args    the arguments to substitute at specified indices
	 * @return formatted message using MessageFormat
	 */
	public static String formatMessage(String message, Object... args) {
		if (CollectionsUtil.isEmpty(args) || !message.contains("{"))
			return message;
		return MessageFormat.format(message, args);
	}

	/**
	 * Internal method to format messages with tuple-style {} placeholders. Replaces
	 * {} placeholders sequentially with provided arguments.
	 * 
	 * @param message the message template with {} placeholders
	 * @param args    the arguments to substitute sequentially
	 * @return message with {} placeholders replaced
	 */
	private static String _formatByTuple(String message, Object... args) {
		if (CollectionsUtil.isEmpty(args) || !message.contains("{}"))
			return message;
		for (Object arg : args) {
			message = message.replaceFirst("\\{\\}", arg != null ? arg.toString() : "");
		}
		return message;
	}

	/**
	 * Formats a message using named placeholders like {name}, {value}. Replaces
	 * named placeholders with corresponding values from the map.
	 * 
	 * @param message the message template with named placeholders
	 * @param args    map containing key-value pairs for placeholder substitution
	 * @return formatted message with named placeholders replaced
	 */
	public static String formatByName(String message, Map<?, ?> args) {
		if (CollectionsUtil.isEmpty(args) || !message.contains("{"))
			return message;
		Matcher matcher = namePattern.matcher(message);
		while (matcher.find()) {
			String key = matcher.group(1);
			message = message.replaceAll("\\{" + key + "\\}",
					Optional.ofNullable(args.get(key)).map(Object::toString).orElse(""));
		}
		return message;
	}

	/**
	 * Formats a message using both named placeholders and positional arguments.
	 * First applies named placeholder substitution, then positional formatting.
	 * Special handling for LinkedHashMap to use values as positional args.
	 * 
	 * @param message   the message template with mixed placeholders types
	 * @param namedArgs map for named placeholder substitution
	 * @param args      positional arguments for remaining placeholders
	 * @return formatted message with all placeholders replaced
	 */
	public static String formatMix(String message, Map<?, ?> namedArgs, Object... args) {
		if (CollectionsUtil.isEmpty(namedArgs) || !message.contains("{"))
			return message;
		message = formatByName(message, namedArgs);
		if (!CollectionsUtil.isEmpty(args)) {
			message = format(message, args);
		}
		return message;
	}

}
