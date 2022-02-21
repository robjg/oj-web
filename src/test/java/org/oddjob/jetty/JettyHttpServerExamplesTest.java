package org.oddjob.jetty;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.WebExists;
import org.oddjob.rest.model.NodeInfos;
import org.oddjob.state.ParentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JettyHttpServerExamplesTest extends Assert {

	private static final Logger logger = 
			LoggerFactory.getLogger(JettyHttpServerExamplesTest.class);
	
	/**
	 * Test loading the whole Oddjob web application.
	 * <p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOddjobWebExample() throws Exception {

		Assume.assumeTrue(WebExists.check());

		File file = new File(getClass().getResource(
				"OddjobWeb.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setName("My Oddjob");
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.STARTED, 
				oddjob.lastStateEvent().getState());
		
		int port = new OddjobLookup(oddjob).lookup("server.port", 
				int.class);
		
		JettyHttpClient httpClient = new JettyHttpClient();
		httpClient.setUrl("http://localhost:" + port);
		httpClient.call();
		
		String content = httpClient.getContent();
				
		logger.info(content);
		
		Assert.assertEquals(200, httpClient.getStatus());
		
		// Todo use HtmlUnit to do a whole load more testing.
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
	
	@Test
	public void testOddjobWebServiceExample() throws Exception {

		File file = new File(getClass().getResource(
				"OddjobWebService.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setName("My Oddjob");
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.STARTED, 
				oddjob.lastStateEvent().getState());
		
		int port = new OddjobLookup(oddjob).lookup("server.port", 
				int.class);
		
		JettyHttpClient httpClient = new JettyHttpClient();
		httpClient.setUrl("http://localhost:" + port + 
				"/api/nodeInfo?nodeIds=0,1,2&eventSeq=-1");
		httpClient.call();
		
		String content = httpClient.getContent();
				
		logger.info(content);
		
		Assert.assertEquals(200, httpClient.getStatus());
		
		Gson gson = new Gson();
		NodeInfos nodeInfos = gson.fromJson(content, NodeInfos.class);		
		
		Assert.assertNotNull(nodeInfos);
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
	
	@Test
	public void testDefaultServerExample() throws Exception {

		File file = new File(getClass().getResource(
				"DefaultServerExample.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setName("My Oddjob");
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.STARTED,
				oddjob.lastStateEvent().getState());
		
		int port = new OddjobLookup(oddjob).lookup("server.port", 
				int.class);
		
		JettyHttpClient httpClient = new JettyHttpClient();
		httpClient.setUrl("http://localhost:" + port+ 
				"/foo");
		httpClient.call();
		
		Assert.assertEquals(200, httpClient.getStatus());
		
		String content = httpClient.getContent();
		
		logger.debug(content);
		
		Assert.assertTrue("Expected 'Welcome' in :" + content, 
				content.contains("Welcome"));
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
	
	@Test
	public void testResourceHandlerExample() throws Exception {

		File file = new File(getClass().getResource(
				"ResourceHandlerExample.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setName("My Oddjob");
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.STARTED, 
				oddjob.lastStateEvent().getState());
		
		int port = new OddjobLookup(oddjob).lookup("server.port", 
				int.class);
		
		JettyHttpClient httpClient = new JettyHttpClient();
		httpClient.setUrl("http://localhost:" + port+ 
				"/TestFile.txt");
		httpClient.call();
		
		Assert.assertEquals(200, httpClient.getStatus());
		Assert.assertEquals("Test", httpClient.getContent());
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
	
	@Test
	public void testResourceWelcomeFileExample() throws Exception {

		File file = new File(getClass().getResource(
				"ResourceHandlerWelcomeFile.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setName("My Oddjob");
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.STARTED, 
				oddjob.lastStateEvent().getState());
		
		int port = new OddjobLookup(oddjob).lookup("server.port", 
				int.class);
		
		JettyHttpClient httpClient = new JettyHttpClient();
		httpClient.setUrl("http://localhost:" + port );
		httpClient.call();
		
		Assert.assertEquals(200, httpClient.getStatus());
		Assert.assertEquals("Test", httpClient.getContent());
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
}
