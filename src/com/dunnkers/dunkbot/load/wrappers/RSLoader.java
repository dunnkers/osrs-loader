package com.dunnkers.dunkbot.load.wrappers;

import java.applet.Applet;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dunnkers.dunkbot.load.worlds.World;
import com.dunnkers.net.Net;
import com.dunnkers.util.ClassLoading;

/**
 * 
 * @author Dunnkers
 */
public class RSLoader {

	private final static Logger logger = LogManager.getLogger(RSLoader.class
			.getName());
	private final RSAppletStub appletStub = new RSAppletStub();
	private Applet applet = null;
	private final World world;
	
	public RSLoader(final World world) {
		this.world = world;
	}

	/**
	 * Loads the RuneScape applet into the local <code>applet</code> object.
	 * @return <tt>True</tt> if we succesfully loaded the RuneScape applet.
	 */
	public boolean load() {
		if (!appletStub.init(getWorld())) {
			logger.error("Failed to init applet stub");
			return false;
		}
		final RSGamePack gamePack = new RSGamePack(world.getBaseURLSpec() + "/"
				+ appletStub.getAttribute("archive"));
		final JarFile jarFile = gamePack.getGamePack();
		if (jarFile == null) {
			logger.error("Failed to get gamepack jar file!");
			return false;
		}

		/*
		 * INJECTION inject code into JarFile create new instance of main class
		 * cast as Applet
		 */

		logger.info("Loading applet...");
		try {
			applet = (Applet) ClassLoading.getClassInstance(ClassLoading.loadClasses(jarFile,
					Net.URL(gamePack.getURLSpec()), appletStub.getAttribute("code")));
		} catch (Exception e) {
			logger.debug("Exception", e);
			logger.error("Failed to cast the loaded class instance to a Applet");
		}
		if (applet == null) {
			logger.error("Failed to load applet!");
			return false;
		}
		applet.setStub(appletStub);
		
		applet.setPreferredSize(appletStub.getDimension());
		applet.init();
		applet.start();
		logger.info("Loaded applet!");
		return true;
	}

	public Applet getApplet() {
		return applet;
	}

	public World getWorld() {
		return world;
	}
}
