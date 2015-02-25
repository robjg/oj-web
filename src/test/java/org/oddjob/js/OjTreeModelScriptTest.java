package org.oddjob.js;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class OjTreeModelScriptTest {

	@Test
	public void testFirstNode() throws Exception {
		
		File file = new File("src/test/webapp/OjTreeModelTest.html");
		
		URL url = file.toURI().toURL();
		
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	 
	    final HtmlPage page = webClient.getPage(url);
	    
	    String result6 = page.getElementById("result6").asText();
	    
	    Assert.assertTrue(result6, result6.contains(
	    		"makeNodeInfoRequest_argCapture: 0,12,13 612"));
	    
	    Assert.assertTrue(result6, result6.contains(
	    		"makeNodeInfoRequest_argCapture: 20,21,22 -1"));
	    
	    
	}
}
