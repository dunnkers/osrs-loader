package com.dunnkers.dunkbot.load.wrappers;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dunnkers.dunkbot.load.Configuration;
import com.dunnkers.dunkbot.load.worlds.World;
import com.dunnkers.net.Net;
import com.dunnkers.util.Parsing;
import com.dunnkers.util.Verification;

/**
 * 
 * @author Dunnkers
 */
public class RSAppletStub implements AppletStub/*, AppletContext*/ {

	private final static Logger logger = LogManager
			.getLogger(RSAppletStub.class.getName());
	private final HashMap<String, String> parameters = new HashMap<String, String>();
	private final HashMap<String, String> attributes = new HashMap<String, String>();
	private URL documentBase;
	private URL codeBase;
	private Dimension dimension;

	public boolean init(final World world) {
		logger.info("Grabbing applet- attributes and parameters...");

		if (!this.setParamsAndAttribs(world.getURLSpec())) {
			logger.error("Could not set parameters and attributes of stub");
			return false;
		}
		final URL baseURL = world.getBaseURL();
		if (baseURL == null) {
			logger.error("Could not get base URL");
			return false;
		}
		
		// TODO seperate documentBase and codeBase
		// http://docs.oracle.com/javase/tutorial/deployment/applet/data.html
		this.setCodeBase(baseURL);
		this.setDocumentBase(baseURL);

		logger.info("Grabbed applet- attributes and parameters!");
		return true;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public URL getDocumentBase() {
		return documentBase;
	}

	@Override
	public URL getCodeBase() {
		return codeBase;
	}

	@Override
	public String getParameter(final String name) {
		return parameters.get(name);
	}

	@Override
	public AppletContext getAppletContext() {
		return null/*this*/;
	}

	@Override
	public void appletResize(int width, int height) {

	}

	public String getAttribute(final String name) {
		return attributes.get(name);
	}

	public void putParameter(final String key, final String value) {
		parameters.put(key, value);
	}

	public void putAttribute(final String key, final String value) {
		attributes.put(key, value);
	}

	public void setDocumentBase(URL documentBase) {
		this.documentBase = documentBase;
	}

	public void setCodeBase(URL codeBase) {
		this.codeBase = codeBase;
	}
	
	public Dimension getDimension() {
		if (dimension == null) {
			dimension = new Dimension(Parsing.parseNumber(this.attributes, "width"), Parsing.parseNumber(this.attributes, "height"));
		}
		return dimension;
	}

	/**
	 * Grabs the applet parameters and attributes from the specified URL by
	 * searching the HTML for a <i>applet</i> element and a <i>param</i>
	 * element.
	 * 
	 * @param urlSpec
	 *            URL specification.
	 * @return whether we succeeded to set parameters and attributes or not.
	 */
	public boolean setParamsAndAttribs(final String urlSpec) {
		String html = Net.getContent(urlSpec);
		if (html == null) {
			logger.error("Unable to continue, failed to read game page");
			return false;
		}
		final ArrayList<String> lines = getParametersAndAttributesHTML(html);
		if (lines == null) {
			logger.error("Failed to set applet stub params");
			return false;
		}
		for (final String line : lines) {
			final Document document = Jsoup.parse(line);
			final Elements paramElements = document.select("param");
			if (!paramElements.isEmpty()) {
				for (final Element paramElement : paramElements) {
					this.putParameter(paramElement.attr("name"),
							paramElement.attr("value"));
					logger.debug("[parameter]{} = {}",
							paramElement.attr("name"),
							paramElement.attr("value"));
				}
			}
			final Elements appletElements = document.select("applet");
			if (!appletElements.isEmpty()) {
				for (final Element appletElement : appletElements) {
					for (final Attribute attribute : appletElement.attributes()) {
						if (attribute.getValue().trim().isEmpty()) {
							continue;
						}
						this.putAttribute(attribute.getKey(),
								attribute.getValue());
						logger.debug("[attribute]{} = {}", attribute.getKey(),
								attribute.getValue());
					}
				}
			}
		}
		logger.debug("Total parameters parsed: {}", this.parameters.size());
		logger.debug("Total attributes parsed: {}", this.attributes.size());
		return this.parameters.size() > 0 && this.attributes.size() > 0;
	}

	private ArrayList<String> getParametersAndAttributesHTML(final String html) {
		final ArrayList<String> lines = new ArrayList<String>();
		final Document document = Jsoup.parse(html);
		final Element javaScriptElement = document.getElementById("deployJava");
		if (javaScriptElement == null) {
			logger.error("Failed to get the html of the parameters and attributes");
			return null;
		}
		final String javaScript = javaScriptElement.html();
		String[] javaScriptLines = null;
		try {
			javaScriptLines = javaScript.split(";");
		} catch (Exception e) {
			logger.debug("Exception", e);
			logger.error("Could not split javascript in lines");
			return null;
		}
		if (javaScriptLines == null) {
			logger.error("No javascript was split");
			return null;
		}
		String javaScriptLineStack = "";
		for (final String javaScriptLine : javaScriptLines) {
			String line = parseHTML(javaScriptLine);
			if (line == null) {
				continue;
			}
			if (!Verification.isHTML(line)) {
				javaScriptLineStack += line;
				if (Verification.isHTML(javaScriptLineStack)) {
					lines.add(javaScriptLineStack);
					javaScriptLineStack = "";
				}
			} else {
				lines.add(line);
			}
		}
		return lines.isEmpty() ? null : lines;
	}

	private String parseHTML(final String javaScriptLine) {
		if (!Configuration.JAVASCRIPT_OUTPUT_LINE.matcher(javaScriptLine).matches()) {
			return null;
		}
		final Matcher matcher = Configuration.REGEX_BETWEEN_APOSTROPHE
				.matcher(javaScriptLine);
		if (!matcher.find()) {
			return null;
		}
		String code = null;
		try {
			code = matcher.group(1);
		} catch (Exception e) {
			logger.debug("Failed to parse a javascript line and to get the html from document.write");
			return null;
		}
		if (code == null) {
			return null;
		}
		return code;
	}
}
