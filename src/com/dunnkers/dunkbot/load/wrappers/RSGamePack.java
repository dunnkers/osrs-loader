package com.dunnkers.dunkbot.load.wrappers;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dunnkers.net.Net;

/**
 * 
 * @author Dunnkers
 */
public class RSGamePack {

	private final static Logger logger = LogManager.getLogger(RSGamePack.class.getName());
	private JarFile gamePack;
	private final String urlSpec;
	
	public RSGamePack(final String urlSpec) {
		this.urlSpec = urlSpec;
	}

	public String getURLSpec() {
		return urlSpec;
	}
	
	public JarFile getGamePack() {
		if (gamePack != null) {
			return gamePack;
		}
		logger.info("Grabbing gamepack...");
		logger.trace("Gamepack URL: {}", getURLSpec());
		URL jarURL = Net.URL("jar:" + getURLSpec() + "!/");
		if (jarURL == null) {
			logger.error("Failed to create gamepack jar URL");
			return null;
		}
		final URLConnection urlConnection = Net.getURLConnection(jarURL);
		if (urlConnection == null) {
			logger.error("Failed to create gamepack URL connection");
			return null;
		}
		final JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
		final JarFile jarFile;
		try {
			jarFile = jarURLConnection.getJarFile(); // TODO counter
		} catch (IOException e) {
			logger.debug("Exception", e);
			logger.warn("Failed to get jar file from jar URL connection, jar URL: {}", jarURL);
			return null;
		}
		logger.info("Grabbed gamepack!");
		gamePack = jarFile;
		return jarFile;
	}
}
