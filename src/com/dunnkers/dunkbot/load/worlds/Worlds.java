package com.dunnkers.dunkbot.load.worlds;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dunnkers.dunkbot.load.Configuration;
import com.dunnkers.net.Net;

/**
 * Functions related to RuneScape worlds.
 * TODO add function getnearestvalidworld
 * @author Dunnkers
 */
public class Worlds {

	private final static Logger logger = LogManager.getLogger(Worlds.class.getName());
	private final static ArrayList<World> worlds = new ArrayList<World>();

	public static ArrayList<World> getWorlds() {
		return worlds;
	}
	
	public static boolean load() {
		Document document = getDocument();
		if (document == null) {
			return false;
		}
		final Elements javaScriptElements = document
				.select("script[type=text/javascript][language=javascript]");
		if (javaScriptElements == null || !(javaScriptElements.size() > 0)) {
			logger.error("Failed to select the javascript");
			return false;
		}
		for (final Element javaScriptElement : javaScriptElements) {
			final String html = javaScriptElement.html();
			if (!html.contains("Players")) {
				continue;
			}
			String[] lines = null;
			try {
				lines = html.split(";"/*\n*/);
			} catch (Exception e) {
				logger.error("Failed split the javascript into lines");
				return false;
			}
			for (final String line : lines) {
				try {
					final Pattern pattern = Pattern
							.compile(Configuration.WORLD_NAME_REGEX);
					if (!pattern.matcher(line).find()) {
						continue;
					}
					final Matcher seperateCommasMatcher = Configuration.REGEX_BETWEEN_PARENTHESES
							.matcher(line);
					String list = null;
					while (seperateCommasMatcher.find()) {
						list = seperateCommasMatcher.group(1);
					}
					final String[] listSplit = list.split(",");
					for (int i = 0; i < listSplit.length; i ++) {
						listSplit[i] = listSplit[i].replaceAll("\"", "");
					}
					final World world = new World(Integer.parseInt(listSplit[0]),
							Boolean.parseBoolean(listSplit[1]),
							Integer.parseInt(listSplit[2]),
							listSplit[3],
							Integer.parseInt(listSplit[4]),
							listSplit[5],
							listSplit[6],
							listSplit[7],
							listSplit[8]);
					logger.debug("Found a world: [" + world.toString() + "]");
					worlds.add(world);
				} catch (Exception e) {
					logger.error("Failed to parse the javascript into a World object");
					return false;
				}
			}
		}
		if (worlds.isEmpty()) {
			logger.error("Failed to get worlds, no worlds found");
			return false;
		}
		logger.trace("Loaded "+worlds.size()+" world"+(worlds.size() > 1 ? "s" : ""));
		return true;
	}

	private static Document getDocument() {
		final String html = Net.getContent(Configuration.WORLDS_SPEC);
		if (html == null) {
			logger.error("Could not retrieve worlds page");
			return null;
		}
		return Jsoup.parse(html);
	}
	
	/**
	 * Randomly selects a valid world.
	 * @param worlds The worlds loaded.
	 * @return A valid world.
	 */
	public static World getValidWorld() {
		if (!(worlds != null && !worlds.isEmpty())) {
			logger.error("#getValidWorld; No worlds defined in argument");
			return null;
		}
		// TODO add randomness
		for (final World world : worlds) {
			if (!world.isValid()) {
				continue;
			}
			if (world.isMemberOnly() && !Configuration.MEMBER) {
				continue;
			}
			return world;
		}
		return null;
	}
}
