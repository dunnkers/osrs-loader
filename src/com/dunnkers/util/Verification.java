package com.dunnkers.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 
 * @author Dunnkers
 */
public class Verification {

	/**
	 * Determines whether this string contains HTML tags or not.
	 * @param string The string.
	 * @return <tt>True</tt> if the string contained HTML.
	 */
	public static boolean isHTML(final String string) {
		final Document document = Jsoup.parse(string);
		return document.getAllElements().size() - 4 > 0;
	}
}
