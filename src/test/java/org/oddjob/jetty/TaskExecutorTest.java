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
import org.oddjob.rest.model.ActionStatus;
import org.oddjob.rest.model.ComponentSummary;
import org.oddjob.state.JobState;
import org.oddjob.state.ParentState;
import org.oddjob.tools.StateSteps;

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
		
		oddjob.load();
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		Stateful echo = lookup.lookup("echo", Stateful.class);
		
		StateSteps echoState = new StateSteps(echo);
		echoState.startCheck(JobState.READY, 
				JobState.EXECUTING, JobState.COMPLETE);
		
		oddjob.run();
		
		assertEquals(ParentState.STARTED, 
				oddjob.lastStateEvent().getState());
		
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
		
		String executeUrl = "http://localhost:" + port + 
				"/api/actionForm/" + nodeId + "/execute";

//		httpClient.setUrl(executeUrl);
//		httpClient.setHttpMethod(HttpMethod.POST);
//		
//		properties = new Properties();
//		properties.setProperty("favourite.fruit", "Apples");
//		properties.setProperty("some.secret", "password123");
//		
//		httpClient.setProperties(properties);
//		
//		httpClient.setContentType("application/x-www-form-urlencoded");
//		
//		httpClient.call();
//				
//		content = httpClient.getContent();
//				
//		logger.info(content);
//		
//		assertEquals(200, httpClient.getStatus());
//				
//		ActionStatus actionStatus = gson.fromJson(content, ActionStatus.class);
//		
//		assertEquals(ActionStatus.Code.OK, actionStatus.getCode());
		
		HttpClient c = new HttpClient();
		c.start();
		ContentResponse r = c.POST(executeUrl)
				.param("favourite.fruit", "Apples")
				.param("some.secret", "foo")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.send();
		System.out.println(r.getContentAsString());
		c.stop();

		
		echoState.checkWait();
		
		assertEquals("Favourite Fruit: Apples, A Secret: foo",
				lookup.lookup("echo.text", String.class));
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
}
