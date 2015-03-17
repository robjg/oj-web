package org.oddjob.js;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class OjJobActionsScriptTest {

	@Test
	public void testAll() throws Exception {
		
		File file = new File("src/test/webapp/OjJobActionsTest.html");
		
		URL url = file.toURI().toURL();
		
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	 
	    final HtmlPage page = webClient.getPage(url);
	    
	    DomElement ojActionsDiv = page.getElementById("ojJobActions");
	    
	    // Test 1
	    
	    HtmlElement button1 = (HtmlElement) page.getElementById("runTestOne");
	    
	    button1.click();
	    
	    List<?> actionButtons = ojActionsDiv.getByXPath("button");
	    
	    assertEquals(3, actionButtons.size());
	    
	    // Test 2
	    
	    HtmlElement button2 = (HtmlElement) page.getElementById("runTestTwo");
	    
	    button2.click();
	    
	    String result2 = ((HtmlElement) page.getElementById(
	    		"result2").getFirstByXPath(
	    				"div[@class='actionExecuted']")).asText();
	    
	    assertEquals("actionExecuted: run/3", 
	    		result2);
	    
	    // Cleanup
	    
	    webClient.closeAllWindows();
	}
}