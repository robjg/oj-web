package org.oddjob.jetty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.Stateful;
import org.oddjob.rest.model.ActionBean;
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
		
		// Get id for echo-task. 
		
		httpClient.setUrl("http://localhost:" + port + 
				"/api/summariesFor");
		
		Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("paths", "echo-task");
		
		httpClient.setParameters(parameters);
		httpClient.call();
		
		assertEquals(200, httpClient.getStatus());
				
		String content = httpClient.getContent();
		
		Gson gson = new Gson();
		
		ComponentSummary[] summaries = gson.fromJson(content, 
				ComponentSummary[].class);
		
		int nodeId = summaries[0].getNodeId();
	
		// Actions for
		
		httpClient.setUrl("http://localhost:" + port + 
			"/api/actionsFor/" + nodeId);
		httpClient.setParameters(null);
		httpClient.call();
		
		assertEquals(200, httpClient.getStatus());
		
		content = httpClient.getContent();
		
		ActionBean[] actions = gson.fromJson(content, 
				ActionBean[].class);
		
		ActionBean execute = null;
		for (ActionBean action : actions) {
			if ("execute".equals(action.getName())) {
				execute = action;
			}
		}
		
		assertNotNull("Execute action", execute);

		assertEquals(ActionBean.Type.FORM, execute.getActionType());
		
		// Execute action
		
		String executeUrl = "http://localhost:" + port + 
				"/api/formAction/" + nodeId + "/execute";

		httpClient.setUrl(executeUrl);
		httpClient.setMethod(JettyHttpClient.RequestMethod.POST);
		
		parameters = new LinkedHashMap<>();
		parameters.put("favourite.fruit", "Apples");
		parameters.put("some.secret", "password123");
		
		httpClient.setParameters(parameters);
		
		httpClient.call();
				
		content = httpClient.getContent();
				
		logger.info(content);
		
		assertEquals(200, httpClient.getStatus());
				
		ActionStatus actionStatus = gson.fromJson(content, ActionStatus.class);
		
		assertEquals(ActionStatus.Code.OK, actionStatus.getStatus());
		
		echoState.checkWait();
		
		assertEquals("Favourite Fruit: Apples, A Secret: password123",
				lookup.lookup("echo.text", String.class));
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
}
