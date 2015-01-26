package com.dunnkers.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Dunnkers
 */
public class ClassLoading {

	private final static Logger logger = LogManager.getLogger(ClassLoading.class.getName());

	/**
	 * Loads all classes in the <code>jarFile</code> referenced by the <code>url</code>,
	 * and closes the file.
	 * @param jarFile The JarFile.
	 * @param url The URL.
	 * @param className The name of the class to return.
	 * @return The class that matches the name of the given <code>className</code>
	 */
	public static Class<?> loadClasses(final JarFile jarFile, final URL url,
			final String className) {
		if (jarFile == null || url == null || className == null
				|| className.isEmpty()) {
			logger.error("Some arguments are invalid whilst trying to load classes");
			return null;
		}
		Enumeration<JarEntry> entries = jarFile.entries();
		if (entries == null) {
			logger.error("Failed to get JarFile entries whilst trying to load classes");
			return null;
		}
		final URLClassLoader loader = new URLClassLoader(new URL[] { url });
		logger.trace("Created a URLClassLoader for URL:  {}", url);
		Class<?> parameterClass = null;
		int loadedClasses = 0;
		while (entries.hasMoreElements()) {
			final JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			if (!entryName.endsWith(".class")) {
				continue;
			}
			final String name = entryName.replaceAll("/", ".").replaceAll(".class",
					"");

			Class<?> loadedClass = null;
			try {
				loadedClass = loader.loadClass(name);
			} catch (ClassNotFoundException e) {
				logger.debug("Exception", e);
				logger.warn(
						"Class '{}' not found whilst trying to load class in JarFile from URL: {}",
						name, url);
			}
			if (loadedClass == null) {
				continue;
			}
			loadedClasses ++;
			if (entryName.equals(className)) {
				logger.trace(
						"Found the class [{}]: name ({}) equals requested; ({}), loading it",
						entryName, name, className);
				parameterClass = loadedClass;
			}
		}
		try {
			loader.close();
		} catch (IOException e) {
			logger.debug("Exception", e);
			logger.warn("Failed to close URL class loader from url: {}",
					url);
		}
		try {
			jarFile.close();
		} catch (IOException e) {
			logger.debug("Exception", e);
			logger.warn("Failed to close JarFile whilst trying to load classes from URL: ", url);
		}
		logger.trace("Loaded {} classes from jar file! Returning class with name {}", loadedClasses, className);
		return parameterClass;
	}

	public static Object getClassInstance(final Class<?> loadedClass) {
		if (loadedClass == null) {
			logger.error("Loaded class invalid while trying to make an instance");
			return null;
		}
		try {
			return loadedClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.debug("Exception", e);
			logger.warn("Unable to create new instance of class '{}'",
					loadedClass.getSimpleName());
			return null;
		}
	}
}
