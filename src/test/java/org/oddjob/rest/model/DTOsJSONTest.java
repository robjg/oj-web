package org.oddjob.rest.model;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.state.FlagState;
import org.oddjob.state.JobState;
import org.oddjob.state.StateEvent;

import com.google.gson.Gson;

public class DTOsJSONTest {

	private static Logger logger = Logger.getLogger(DTOsJSONTest.class);
	
	@Test
	public void testNodeInfoJSON() {
		
		NodeInfo ni0 = new NodeInfo(0, "Some Jobs", "ready", 
				new int[] { 12, 13 });
		
		Gson gson = new Gson();
		String json = gson.toJson(ni0);  
		
		logger.info(json);
		
		String expected = "{\"nodeId\":0,\"name\":\"Some Jobs\",\"icon\":\"ready\",\"children\":[12,13]}";
		
		Assert.assertEquals(expected, json);
	}

	@Test
	public void testNodeInfoWithNullChildrenNullNameJSON() {
		
		NodeInfo ni0 = new NodeInfo(0, null, "executing", 
				null);
		
		Gson gson = new Gson();
		String json = gson.toJson(ni0);  
		
		logger.info(json);
		
		String expected = "{\"nodeId\":0,\"icon\":\"executing\"}";
		
		Assert.assertEquals(expected, json);
	}

	@Test
	public void testNodeInfosOneNode() {
		
		NodeInfo ni0 = new NodeInfo(0, "Some Jobs", "ready", 
				new int[] { 12, 13 });
		
		NodeInfos nis = new NodeInfos(457, new NodeInfo[] { ni0 });
		
		Gson gson = new Gson();
		String json = gson.toJson(nis);  
		
		logger.info(json);
		
		String expected = "{\"eventSeq\":457,\"nodeInfo\":[" +
				"{\"nodeId\":0,\"name\":\"Some Jobs\",\"icon\":\"ready\",\"children\":[12,13]}" +
				"]}";
		Assert.assertEquals(expected, json);
	}
	
	@Test
	public void testStateJSON() throws ParseException {
		
		StateEvent stateEvent = new StateEvent(new FlagState(), 
				JobState.EXCEPTION, 
				DateHelper.parseDateTime("2015-04-01 08:26", "GMT"),
				new Exception("Some Exception"));

		StateDTO test = new StateDTO(456, stateEvent);
		
		Gson gson = new Gson();
		String json = gson.toJson(test);  
		
		logger.info(json);		
		
		String expected = "{\"nodeId\":456,\"state\":\"EXCEPTION\",\"time\":1427876760000,\"exception\":\"java.lang.Exception: Some Exception";
		
		Assert.assertEquals(true, json.startsWith(expected));
	}

	@Test
	public void testProperitesJSON() throws ParseException {
		
		Map<String, String> properties = new LinkedHashMap<>();
		properties.put("favourite.fruit", "apples");
		properties.put("favourite.colour", null);
		
		PropertiesDTO test = new PropertiesDTO(456, properties);
		
		
		Gson gson = new Gson();
		String json = gson.toJson(test);  
		
		logger.info(json);
		
		String expected = "{\"nodeId\":456,\"properties\":{\"favourite.fruit\":\"apples\",\"favourite.colour\":\"green\"}}";
		
		Assert.assertEquals(expected, json);
	}
	
}
