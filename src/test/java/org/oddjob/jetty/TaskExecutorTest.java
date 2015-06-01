package org.oddjob.jetty;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.Stateful;
import org.oddjob.rest.model.ComponentSummary;
import org.oddjob.state.ParentState;

import com.google.gson.Gson;

public class TaskExecutorTest {

	private static final Logger logger = Logger.getLogger(TaskExecutorTest.class);
	
	@Test
	public void testOddjobWebServiceActions() throws Exception {

		File file = new File(getClass().getResource(
				"TaskExecutor.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setName("My Oddjob");
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.STARTED, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		Stateful sequential = lookup.lookup("jobs", Stateful.class);
		
		assertEquals(ParentState.STARTED, 
				sequential.lastStateEvent().getState());
		
		int port = lookup.lookup("server.port", int.class);
		
		JettyHttpClient httpClient = new JettyHttpClient();
		
		httpClient.setUrl("http://localhost:" + port + 
				"/api/summariesFor");
		httpClient.setHttpMethod(HttpMethod.GET);
		
		Properties properties = new Properties();
		properties.setProperty("paths", "echo-task");
		
		httpClient.setProperties(properties);
		httpClient.call();
		
		String content = httpClient.getContent();
		
		Gson gson = new Gson();
		
		ComponentSummary[] summaries = gson.fromJson(content, 
				ComponentSummary[].class);
		
		int nodeId = summaries[0].getNodeId();
		
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
				"/api/actionForm/" + nodeId + "/execute")
				.param("favourite.fruit", "Apples")
				.param("some.secret", "foo")
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
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
}
