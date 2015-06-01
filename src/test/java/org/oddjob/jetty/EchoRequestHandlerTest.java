package org.oddjob.jetty;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Assert;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.Stateful;
import org.oddjob.rest.model.ComponentSummary;
import org.oddjob.rest.model.NodeInfos;
import org.oddjob.state.ParentState;

import com.google.gson.Gson;

public class EchoRequestHandlerTest extends TestCase {

	private static final Logger logger = 
			Logger.getLogger(EchoRequestHandlerTest.class);
	
	public void testEchoJettyClientPostFormData() throws Exception {
		
		File file = new File(getClass().getResource(
				"EchoRequestHandler.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setName("My Oddjob");
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.STARTED, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		int port = lookup.lookup("server.port", int.class);
		
		JettyHttpClient httpClient = new JettyHttpClient();
				
//		httpClient.setUrl("http://localhost:" + port + 
//				"/api/formAction/" + nodeId);
//		httpClient.setHttpMethod(HttpMethod.POST);
//		
//		properties = new Properties();
//		properties.setProperty("favourite.fruit", "Apples");
//		properties.setProperty("some.secret", "password123");
//		
//		httpClient.setProperties(properties);
//		
//		httpClient.call();
		
		HttpClient c = new HttpClient();
		c.start();
		ContentResponse r = c.POST("http://localhost:" + port + 
				"/anything/")
				.param("favourite.fruit", "Apples")
				.send();
		c.stop();
		
		System.out.println(r.getContentAsString());
		System.out.println(r.getReason());
		assertEquals(200, r.getStatus());
//		content = httpClient.getContent();
//				
//		logger.info(content);
//		
//		assertEquals(200, httpClient.getStatus());
//				
//		assertEquals("", content);
//						
		
		String content = httpClient.getContent();
		
		Gson gson = new Gson();
		
		EchoRequestBean request = gson.fromJson(content, 
				EchoRequestBean.class);
		
//		Thread.sleep(60 * 60 * 1000L);
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
	
}
