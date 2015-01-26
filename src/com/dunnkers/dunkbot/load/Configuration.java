package com.dunnkers.dunkbot.load;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * 
 * @author Dunnkers
 */
public class Configuration {

	/*
	 * logging protocol
	 * 
	 * exception inst.debug(msg, e) inst.error(explain, variables)
	 * 
	 * application specific inst.info(progression_msg) //ex.
	 * logger.info("Loaded worlds!");
	 * 
	 * unexpected nullpointer or error inst.error(msg)
	 * 
	 * optional failure inst.warn(msg)
	 * 
	 * debugging mesage, for user to optionally see inst.debug(msg)
	 * 
	 * trace, only for developer to see inst.trace(msg)
	 */

	private Configuration() {
	}

	public static final double APPLICATION_VERSION_MAJOR = 0;
	public static final double APPLICATION_VERSION_MINOR = 0.01;
	public static final String APPLICATION_TITLE = "OSRS Loader";
	public static final String APPLICATION_ALIAS = "Let's load!";

	public static final String WORLD_URL_PREFIX = "http://";
	public static final String WORLD_URL = ".runescape.com";
	public static final String WORLD_U = "/j1";
	public static final String WORLDS_SPEC = "http://oldschool.runescape.com/slu";
	public static final String WORLD_NAME_REGEX = "Old School";

	public static final Pattern REGEX_BETWEEN_APOSTROPHE = Pattern
			.compile("\'(.*?)\'");
	public static final Pattern REGEX_BETWEEN_PARENTHESES = Pattern
			.compile("\\((.*?)\\)");
	public static final Pattern JAVASCRIPT_OUTPUT_LINE = Pattern
			.compile("^(?=.*?document)(?=.*?write).*$");

	public static boolean MEMBER = true;

	public static final HashMap<String, String> REQUEST_PROPERTIES;

	static {
		REQUEST_PROPERTIES = new HashMap<String, String>();
		REQUEST_PROPERTIES
				.put("Accept",
						"text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		REQUEST_PROPERTIES.put("Accept-Charset",
				"ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		//REQUEST_PROPERTIES.put("Accept-Encoding", "gzip,deflate");
		// TODO Why is gzip encoding not working? new java version caused it.
		REQUEST_PROPERTIES.put("Accept-encoding", "deflate");
		REQUEST_PROPERTIES.put("Accept-Language", "en-gb,en;q=0.5");
		REQUEST_PROPERTIES.put("Connection", "keep-alive");
		REQUEST_PROPERTIES.put("Host", "oldschool.runescape.com");
		REQUEST_PROPERTIES.put("Keep-Alive", "300");
		REQUEST_PROPERTIES
				.put("User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.0.6) Gecko/20060728 Firefox/1.5.0.6");
	}
	// TODO USE asList() HERE!
	/*public static final HashMap<String, String> REQUEST_PROPERTIES1 = 
			(HashMap<String, String>) Arrays.asList();*/

	public static double getApplicationVersion() {
		return Configuration.APPLICATION_VERSION_MAJOR
				+ Configuration.APPLICATION_VERSION_MINOR;
	}
}
