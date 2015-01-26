package com.dunnkers.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Dunnkers
 */
public class Net {

	private final static Logger logger = LogManager.getLogger(Net.class
			.getName());
	public static HashMap<String, String> requestProperties = new HashMap<String, String>();
	private static int connectTimeout = 30000;
	private static int readTimeout = connectTimeout;

	public static void addRequestProperty(final String key, final String value) {
		requestProperties.put(key, value);
	}

	public static void setConnectTimeout(final int connectTimeout) {
		Net.connectTimeout = connectTimeout;
	}

	public static void setReadTimeout(final int readTimeout) {
		Net.readTimeout = readTimeout;
	}

	public static void setRequestProperties(
			final HashMap<String, String> requestPropertiesParam) {
		requestProperties.clear();
		requestProperties.putAll(requestPropertiesParam);
	}

	public static String getContent(final String urlSpec) {
		final ArrayList<String> connection = readConnection(urlSpec);
		if (connection == null) {
			return null;
		}
		String content = "";
		for (final String line : connection) {
			content += line;
		}
		return content.isEmpty() ? null : content;
	}

	public static ArrayList<String> readConnection(final String urlSpec) {
		try {
			return readConnection(URL(urlSpec));
		} catch (IOException e) {
			logger.debug("Exception", e);
			logger.warn(
					"Failed to close connection or failed to create URL: {}",
					urlSpec);
		}
		return null;
	}

	public static ArrayList<String> readConnection(final URL url)
			throws IOException {
		final ArrayList<String> lines = new ArrayList<String>();
		final URLConnection urlConnection = getURLConnection(url);
		if (urlConnection == null) {
			return null;
		}
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			inputStream = urlConnection.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			logger.debug("Exception", e);
			logger.warn("Could not read the connection to URL: {}", url);
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
		if (lines.isEmpty()) {
			return null;
		}
		logger.trace("Read {} lines from connection with URL: {}",
				lines.size(), url);
		return lines;
	}

	public static URLConnection getURLConnection(final URL url) {
		if (url == null) {
			logger.warn("No valid URL when getting URL connection");
			return null;
		}
		try {
			final URLConnection urlConnection = url.openConnection();
			for (final Entry<String, String> requestProperty : requestProperties
					.entrySet()) {
				urlConnection.setRequestProperty(requestProperty.getKey(),
						requestProperty.getValue());
				if (connectTimeout != -1) {
					urlConnection.setConnectTimeout(connectTimeout);
				}
				if (readTimeout != -1) {
					urlConnection.setReadTimeout(readTimeout);
				}
			}
			return urlConnection;
		} catch (IOException e) {
			logger.debug("Exception", e);
			logger.warn("Could not get URL connection to: {}", url);
			return null;
		}
	}

	public static URL URL(final String urlSpec) {
		try {
			return new URL(urlSpec);
		} catch (MalformedURLException e) {
			logger.debug("Exception", e);
			logger.warn(
					"Could not read the connection to URL specification: {}",
					urlSpec);
			return null;
		}
	}
}
