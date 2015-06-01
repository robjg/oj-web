package org.oddjob.jetty;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.rest.model.NodeInfos;
import org.oddjob.state.ParentState;

import com.google.gson.Gson;

public class JettyHttpServerExamplesTest extends TestCase {

	private static final Logger logger = 
			Logger.getLogger(JettyHttpServerExamplesTest.class);
	
	/**
	 * Test loading the whole Oddjob web application.
	 * <p>
	 * 
	 * @throws Exception
	 */
	public void testOddjobWebExample() throws Exception {

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
		
		Assert.assertEquals(404, httpClient.getStatus());
		
		String content = httpClient.getContent();
		
		logger.debug(content);
		
		Assert.assertTrue("Expected 'Not Found' in :" + content, 
				content.contains("Not Found"));
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
	
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
