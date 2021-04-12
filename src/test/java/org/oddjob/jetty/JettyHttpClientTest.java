package org.oddjob.jetty;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;

import java.io.File;
import java.util.Properties;

public class JettyHttpClientTest extends Assert {

	@Test
	public void testExample() throws Exception {
		
		ResourceHandlerType resourceHandler = new ResourceHandlerType();
		resourceHandler.setBase("org/oddjob/jetty");
		resourceHandler.setResourceType(ResourceHandlerType.ResourceType.CLASSPATH);
		
		JettyHttpServer server = new JettyHttpServer();
		server.setHandler(resourceHandler.toValue());
		
		server.start();
		
		File file = new File(getClass().getResource("ClientGetExample.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(file);
		
		Properties properties = new Properties();
		properties.setProperty("some.url", "http://localhost:" + server.getPort() + 
				"/TestFile.txt");

		oddjob.setProperties(properties);
		

		oddjob.run();
		
		assertTrue("Is complete.", oddjob.lastStateEvent().getState().isComplete());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		String result = lookup.lookup("request.content", String.class);
		
		assertEquals("Test", result.trim());
		
		oddjob.destroy();
		
		server.stop();
	}
}
