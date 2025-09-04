package com.lsm.utils;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SanitizeUtil {
	private static Pattern htmlPattern = Pattern.compile("<(?i)script>|<(?i)/script>") ;
	private static Pattern sqlQuotes = Pattern.compile("(\"|\')") ;

	/**
	 * sanitizes all types of quotes :',"
	 * @param in - input string 
	 * @return - output string
	 */
	public static String sanitizeQuotes( String in) {
		if (!isBlank( in )) {
			Matcher m = sqlQuotes.matcher(in);
			m.replaceAll("");
		}
		return in;
	}

	/**
	 * sanitizes all types of quotes :',"
	 * @param in - input string 
	 * @return - output string
	 */
	public static String sanitizeHtml( String in) {
		if (!isBlank( in )) {
		   Matcher m = htmlPattern.matcher(in);
 			   return  m.replaceAll("");
		}
		return in;
	}
	
	/**
	 * sanitizes all types of quotes :',"
	 * @param in - input string 
	 * @return - output string
	 */
	public static String escapeHtml( String in) {
		in = sanitizeHtml(in);
		if (!isBlank( in )) {	
			in = escapeHtml4(in);
		}
		return in;
	}
	
}
