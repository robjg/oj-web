package org.oddjob.rest.model;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

public class JSONTest {

	@Test
	public void testNodeInfoJSON() {
		
		NodeInfo ni0 = new NodeInfo(0, "Some Jobs", "ready", 
				new int[] { 12, 13 });
		
		Gson gson = new Gson();
		String json = gson.toJson(ni0);  
		
		String expected = "{\"nodeId\":0,\"name\":\"Some Jobs\",\"icon\":\"ready\",\"children\":[12,13]}";
		
		Assert.assertEquals(expected, json);
	}

	@Test
	public void testNodeInfoWithNullChildrenNullNameJSON() {
		
		NodeInfo ni0 = new NodeInfo(0, null, "executing", 
				null);
		
		Gson gson = new Gson();
		String json = gson.toJson(ni0);  
		
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
		
		String expected = "{\"eventSeq\":457,\"nodeInfo\":[" +
				"{\"nodeId\":0,\"name\":\"Some Jobs\",\"icon\":\"ready\",\"children\":[12,13]}" +
				"]}";
		Assert.assertEquals(expected, json);
	}
	
}
