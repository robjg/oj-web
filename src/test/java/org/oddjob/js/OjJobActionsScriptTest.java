package org.oddjob.js;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.oddjob.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.List;

@Ignore("Old JQuery htmlunit test - needs updating or replacing.")
public class OjJobActionsScriptTest extends Assert {

	private static Logger logger = LoggerFactory.getLogger(OjJobActionsScriptTest.class);
			
	@Test
	public void testAll() throws Exception {
		
		logger.info("----- testing OjJobActionsTest.html ----" );
		
		OurDirs ourDirs = new OurDirs();
		File file = ourDirs.relative("src/test/webapp/OjJobActionsTest.html");
		
		URL url = file.toURI().toURL();
		
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	 
	    final HtmlPage page = webClient.getPage(url);
	    
	    DomElement ojActionsDiv = page.getElementById("ojJobActions");
	    
	    // Test 1
	    
	    HtmlElement button1 = (HtmlElement) page.getElementById("runTestOne");
	    
	    button1.click();
	    
	    List<?> actionButtons = ojActionsDiv.getByXPath("button");
	    
	    assertEquals(4, actionButtons.size());
	    
	    // Test 2
	    
	    HtmlElement button2 = (HtmlElement) page.getElementById("runTestTwo");
	    
	    button2.click();
	    
	    String result2 = ((HtmlElement) page.getElementById(
	    		"result2").getFirstByXPath(
	    				"div[@class='actionExecuted']")).asText();
	    
	    assertEquals("actionExecuted: start/3", 
	    		result2);
	    
	    // Cleanup
	    
	    webClient.close();
	}
}
