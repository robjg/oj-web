package org.oddjob.jetty;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
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
				
		httpClient.setUrl("http://localhost:" + port + 
				"/api/formAction/1234");
		httpClient.setHttpMethod(HttpMethod.POST);
		
		Properties properties = new Properties();
		properties.setProperty("favourite.fruit", "Apples");
		properties.setProperty("some.secret", "password123");
		
		httpClient.setProperties(properties);
		httpClient.setContentType("application/x-www-form-urlencoded");
		
		httpClient.call();
		
		String content = httpClient.getContent();
				
		logger.info(content);
		
		assertEquals(200, httpClient.getStatus());
						
		Gson gson = new Gson();
		
		EchoRequestBean request = gson.fromJson(content, 
				EchoRequestBean.class);
		
		assertEquals("application/x-www-form-urlencoded", 
				request.getContentType());
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		assertEquals("Apples", parameterMap.get("favourite.fruit")[0]);
		assertEquals("password123", parameterMap.get("some.secret")[0]);
		
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
	
	public void xtestClient() throws Exception {
		
		HttpClient c = new HttpClient();
		c.start();
		ContentResponse r = c.POST("http://localhost:8090" + 
					"/api/actionForm/1234/execute")
				.param("favourite.fruit", "Apples")
				.param("some.secret", "foo")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.send();
				
		System.out.println("[" + r.getStatus() + "]");
		System.out.println(r.getReason());
		System.out.println(r.getContentAsString());
		
		c.stop();
	}
}
