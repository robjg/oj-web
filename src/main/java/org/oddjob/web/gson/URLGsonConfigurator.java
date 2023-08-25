package org.oddjob.web.gson;

import com.google.gson.GsonBuilder;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.net.URL;
import java.util.Objects;

/**
 * An {@link GsonConfigurator} that loads delegate {@link GsonConfigurator}s
 * from XML configuration found at the given URLs.
 * 
 * @author rob
 *
 */
public class URLGsonConfigurator implements GsonConfigurator {

	/** The URLs. */
	private final URL[] urls;

	/** The session for the parser to use. */
	private final ArooaSession session;

	/**
	 * Constructor.
	 *
	 * @param urls
	 * @param session
	 */
	public URLGsonConfigurator(URL[] urls, ArooaSession session) {
		this.urls = Objects.requireNonNull(urls);
		this.session = Objects.requireNonNull(session);
	}

	@Override
	public GsonBuilder configure(GsonBuilder gsonBuilder) {

		try {
			for (URL url: urls ) {
				
				XMLConfiguration config = new XMLConfiguration(
						url.toString(), url.openStream());
				
				StandardFragmentParser parser = 
					new StandardFragmentParser(session);
				
				parser.parse(config);
				
				GsonConfigurator configurator =
					(GsonConfigurator) parser.getRoot();

				configurator.configure(gsonBuilder);
			}
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		return gsonBuilder;
	}
	
}
