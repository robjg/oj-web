package org.oddjob.jetty;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.Stateful;
import org.oddjob.state.ParentState;
import org.oddjob.tools.StateSteps;

public class OddjobWebServiceTest {

	private static final Logger logger = Logger.getLogger(OddjobWebServiceTest.class);
	
	@Test
	public void testOddjobWebServiceActions() throws Exception {

		File file = new File(getClass().getResource(
				"OddjobWebService.xml").getFile());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setName("My Oddjob");
		oddjob.setFile(file);
		
		oddjob.run();
		
		assertEquals(ParentState.STARTED, 
				oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		Stateful sequential = lookup.lookup("jobs", Stateful.class);
		
		assertEquals(ParentState.COMPLETE, 
				sequential.lastStateEvent().getState());
		
		int port = lookup.lookup("server.port", int.class);
		
		JettyHttpClient httpClient = new JettyHttpClient();
		httpClient.setUrl("http://localhost:" + port + 
				"/api/actionsFor/0");
		httpClient.call();
		
		String content = httpClient.getContent();
				
		logger.info(content);
		
		assertEquals(200, httpClient.getStatus());
				
		assertEquals("["
				+ "{\"actionType\":\"SIMPLE\",\"name\":\"start\",\"displayName\":\"Start\"},"
				+ "{\"actionType\":\"SIMPLE\",\"name\":\"stop\",\"displayName\":\"Stop\"},"
				+ "{\"actionType\":\"SIMPLE\",\"name\":\"soft-reset\",\"displayName\":\"Soft Reset\"},"
				+ "{\"actionType\":\"SIMPLE\",\"name\":\"hard-reset\",\"displayName\":\"Hard Reset\"},"
				+ "{\"actionType\":\"SIMPLE\",\"name\":\"force\",\"displayName\":\"Force\"}"
				+ "]",
				content);
		
		StateSteps steps = new StateSteps(sequential);
		steps.startCheck(ParentState.COMPLETE, ParentState.READY);
		
		httpClient.setUrl("http://localhost:" + port + 
				"/api/action/0/hard-reset");
		httpClient.call();
		
		assertEquals(200, httpClient.getStatus());
		
		content = httpClient.getContent();
		
		logger.info(content);
		
		assertEquals("{\"status\":\"OK\"}", content);
		
		steps.checkWait();
				
		steps.startCheck(ParentState.READY, ParentState.EXECUTING,
				ParentState.COMPLETE);
		
		httpClient.setUrl("http://localhost:" + port + 
				"/api/action/0/start");
		httpClient.call();
		
		assertEquals(200, httpClient.getStatus());
		
		content = httpClient.getContent();
		
		logger.info(content);
		
		assertEquals("{\"status\":\"OK\"}", content);
		
		steps.checkWait();
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
}
