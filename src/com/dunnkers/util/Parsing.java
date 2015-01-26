package com.dunnkers.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Dunnkers
 */
public class Parsing {
	
	private final static Logger logger = LogManager.getLogger(Parsing.class.getName());
	private final static Pattern PATTERN_DIGIT = Pattern.compile("\\d+");
	
	/**
	 * Removes all alphabetic characters and parses an integer from the given key in given map.
	 * @param hashMap Given map.
	 * @param key Given key to the map.
	 * @return <i>-1</i> if no number was parsed, else the number.
	 */
	public static int parseNumber(final HashMap<String, String> hashMap, final String key) {
		final String valueString = hashMap.get(key);
		if (valueString == null) {
			logger.warn("Could not find key '{}' in map", key);
			return -1;
		}
		return parseNumber(valueString);
	}
	
	/**
	 * Gets the first number from string.
	 * @param string String with a number.
	 * @return The first number in the string.
	 */
	public static int parseNumber(final String string) {
		final Matcher matcher = PATTERN_DIGIT.matcher(string);
		if (!matcher.find() || matcher.groupCount() < 0) {
			return -1;
		}
		final String value = matcher.group(0);
		if (value == null) {
			return -1;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			logger.debug("Exception", e);
			logger.warn("Failed to parse number from string: {}", value);
			return -1;
		}
	}
}
