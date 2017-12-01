package org.oddjob.jetty;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.Fields;
import org.junit.Assert;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.state.ParentState;

import com.google.gson.Gson;

public class EchoRequestHandlerTest extends Assert {

	private static final Logger logger = 
			LoggerFactory.getLogger(EchoRequestHandlerTest.class);
	
	@Test
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
		httpClient.setMethod(JettyHttpClient.RequestMethod.POST);
		
		Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("favourite.fruit", "Apples");
		parameters.put("some.secret", "password123");
		
		httpClient.setParameters(parameters);
		
		httpClient.call();
		
		String content = httpClient.getContent();
				
		logger.info(content);
		
		assertEquals(200, httpClient.getStatus());
						
		Gson gson = new Gson();
		
		EchoRequestBean request = gson.fromJson(content, 
				EchoRequestBean.class);
		
		assertEquals("application/x-www-form-urlencoded", 
				request.getContentType());
		
		assertEquals("favourite.fruit\u003dApples\u0026some.secret\u003dpassword123",
				request.getContent());
		
		oddjob.stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
	}
	
	public void xtestClientPost() throws Exception {
		
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
	
	public void xtestFormContent() throws Exception {
		
		HttpClient c = new HttpClient();
		c.start();
		
		Fields fields = new Fields();
		fields.put("favourite.fruit", "Apples");
		fields.put("some.secret", "foo");
		
		ContentResponse r = c.FORM("http://localhost:8090" + 
					"/api/actionForm/1234/execute", fields);
				
		System.out.println("[" + r.getStatus() + "]");
		System.out.println(r.getReason());
		System.out.println(r.getContentAsString());
		
		c.stop();
	}
}
