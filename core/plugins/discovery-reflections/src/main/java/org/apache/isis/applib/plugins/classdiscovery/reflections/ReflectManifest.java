package org.apache.isis.applib.plugins.classdiscovery.reflections;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.reflections.vfs.SystemDir;
import org.reflections.vfs.Vfs;

import com.google.common.collect.Lists;

/**
 * 
 * package private utility class
 *
 */
class ReflectManifest {

	/*
	 * If this static reference survives ApplicationScope life-cycles, thats ok.
	 * List once initialized is quasi immutable. 
	 */
	private final static List<Vfs.UrlType> urlTypes = new ArrayList<>();
	
	public static void prepareDiscovery() {
		Vfs.setDefaultURLTypes(getUrlTypes());
	}

	// -- 

	private static List<Vfs.UrlType> getUrlTypes() {
		
		if(urlTypes.isEmpty()) {
			urlTypes.add(new EmptyIfFileEndingsUrlType(".pom", ".jnilib", "QTJava.zip"));
			urlTypes.add(new JettyConsoleUrlType());
			urlTypes.addAll(Arrays.asList(Vfs.DefaultUrlTypes.values()));
		}

		return urlTypes;
	}

	// -- HELPER

	private static class EmptyIfFileEndingsUrlType implements Vfs.UrlType {

		private final List<String> fileEndings;

		private EmptyIfFileEndingsUrlType(final String... fileEndings) {
			this.fileEndings = Lists.newArrayList(fileEndings);
		}

		public boolean matches(URL url) {
			final String protocol = url.getProtocol();
			final String externalForm = url.toExternalForm();
			if (!protocol.equals("file")) {
				return false;
			}
			for (String fileEnding : fileEndings) {
				if (externalForm.endsWith(fileEnding))
					return true;
			}
			return false;
		}

		public Vfs.Dir createDir(final URL url) throws Exception {
			return emptyVfsDir(url);
		}

		private static Vfs.Dir emptyVfsDir(final URL url) {
			return new Vfs.Dir() {
				@Override
				public String getPath() {
					return url.toExternalForm();
				}

				@Override
				public Iterable<Vfs.File> getFiles() {
					return Collections.emptyList();
				}

				@Override
				public void close() {
					//
				}
			};
		}
	}

	private static class JettyConsoleUrlType implements Vfs.UrlType {
		public boolean matches(URL url) {
			final String protocol = url.getProtocol();
			final String externalForm = url.toExternalForm();
			final boolean matches = protocol.equals("file") && externalForm.contains("jetty-console") && externalForm.contains("-any-") && externalForm.endsWith("webapp/WEB-INF/classes/");
			return matches;
		}

		public Vfs.Dir createDir(final URL url) throws Exception {
			return new SystemDir(getFile(url));
		}

		/**
		 * try to get {@link java.io.File} from url
		 *
		 * <p>
		 *     Copied from {@link Vfs} (not publicly accessible)
		 * </p>
		 */
		static java.io.File getFile(URL url) {
			java.io.File file;
			String path;

			try {
				path = url.toURI().getSchemeSpecificPart();
				if ((file = new java.io.File(path)).exists()) return file;
			} catch (URISyntaxException e) {
			}

			try {
				path = URLDecoder.decode(url.getPath(), "UTF-8");
				if (path.contains(".jar!")) path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length());
				if ((file = new java.io.File(path)).exists()) return file;

			} catch (UnsupportedEncodingException e) {
			}

			try {
				path = url.toExternalForm();
				if (path.startsWith("jar:")) path = path.substring("jar:".length());
				if (path.startsWith("file:")) path = path.substring("file:".length());
				if (path.contains(".jar!")) path = path.substring(0, path.indexOf(".jar!") + ".jar".length());
				if ((file = new java.io.File(path)).exists()) return file;

				path = path.replace("%20", " ");
				if ((file = new java.io.File(path)).exists()) return file;

			} catch (Exception e) {
			}

			return null;
		}
	}
}
