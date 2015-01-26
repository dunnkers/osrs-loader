package com.dunnkers.dunkbot.load;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dunnkers.dunkbot.load.worlds.World;
import com.dunnkers.dunkbot.load.worlds.Worlds;
import com.dunnkers.dunkbot.load.wrappers.RSLoader;

/**
 * 
 * @author Dunnkers
 */
public class Application extends JFrame {
	
	private final static Logger logger = LogManager.getLogger(Application.class.getName());
	private static final long serialVersionUID = 1L;

	public Application() {
		this.setTitle(Configuration.APPLICATION_TITLE + " v"
				+ Configuration.getApplicationVersion() + " - "
				+ Configuration.APPLICATION_ALIAS);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.setSize(700, 500);
		this.setLocationRelativeTo(this.getOwner());
		this.setVisible(true);
	}

	public void init() {
		logger.info("Grabbing worlds...");
		if (!Worlds.load()) {
			logger.error("Could not grab worlds!");
			return;
		}
		final World world = Worlds.getValidWorld();
		if (world == null) {
			logger.error("Could not find a valid world!");
			return;
		}
		logger.info("Using world {} from {} with {} players!", world.getIndex(), world.getCountry(), world.getPlayers());
		logger.info("Grabbed worlds!");
		
		
		final RSLoader loader = new RSLoader(world);
		if (!loader.load()) {
			logger.error("Failed to start RS!");
			return;
		}
		final Container contentPane = this.getContentPane();
		contentPane.add(loader.getApplet(), BorderLayout.CENTER);
		this.pack();
		this.setLocationRelativeTo(this.getOwner());
	}
}
