package com.dunnkers.dunkbot.load;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dunnkers.net.Net;

/**
 * 
 * @author Dunnkers
 */
public class DunkBot {

	private final static Logger logger = LogManager.getLogger(DunkBot.class
			.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Net.setRequestProperties(Configuration.REQUEST_PROPERTIES);
		
		logger.info("Welcome to {} v{}!", Configuration.APPLICATION_TITLE,
				Configuration.getApplicationVersion());

		final Application application = new Application();
		application.init();
	}
}
