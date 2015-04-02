package org.oddjob.js;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class OjTreeModelScript2Test {

	private static Logger logger = Logger.getLogger(OjTreeModelScript2Test.class);
	
	@Test
	public void testAll() throws Exception {
		
		logger.info("-----  testing OjTreeModelTest2.html  -----");
		
		File file = new File("src/test/webapp/OjTreeModelTest2.html");
		
		URL url = file.toURI().toURL();
		
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	 
	    final HtmlPage page = webClient.getPage(url);
	    
	    // Test 1
	    
	    HtmlElement button1 = (HtmlElement) page.getElementById("runTestOne");
	    
	    button1.click();
	    
	    String result1Request = ((HtmlElement) page.getElementById(
	    		"result1").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0 -1", 
	    		result1Request);
	    
	    String result1TreeCapture = ((HtmlElement) page.getElementById(
	    		"result1").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture']")).asText();
	    
	    assertEquals("treeInitialised: 0 Some Jobs", 
	    		result1TreeCapture);
	    
	    // Test 2
	    
	    HtmlElement button2 = (HtmlElement) page.getElementById("runTestTwo");
	    
	    button2.click();
	    
	    String result2Request = ((HtmlElement) page.getElementById(
	    		"result2").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 12,13 -1",
	    		result2Request);
	    
	    String result2TreeCapture = ((HtmlElement) page.getElementById(
	    		"result2").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture']")).asText();
	    
	    assertEquals("nodeExpanded: 0 12 13", 
	    		result2TreeCapture);
	    
	    // Test 3
	    
	    HtmlElement button3 = (HtmlElement) page.getElementById("runTestThree");
	    
	    button3.click();
	    
	    String result3Request = ((HtmlElement) page.getElementById(
	    		"result3").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 78,79,80 -1",
	    		result3Request);
	    
	    String result3TreeCapture = ((HtmlElement) page.getElementById(
	    		"result3").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture']")).asText();
	    
	    assertEquals("nodeExpanded: 12 78 79", 
	    		result3TreeCapture);
	    
	    // Test 4
	    
	    HtmlElement button4 = (HtmlElement) page.getElementById("runTestFour");
	    
	    button4.click();
	    
	    String result4TreeCapture1 = ((HtmlElement) page.getElementById(
	    		"result4").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture1']")).asText();
	    
	    assertEquals("nodeCollapsed: 12", 
	    		result4TreeCapture1);
	    
	    String result4TreeCapture2 = ((HtmlElement) page.getElementById(
	    		"result4").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture2']")).asText();
	    
	    assertEquals("nodeCollapsed: 0", 
	    		result4TreeCapture2);
	    
	    // Test 5
	    
	    HtmlElement button5 = (HtmlElement) page.getElementById("runTestFive");
	    
	    button5.click();
	    
	    String result5Request = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0 457",
	    		result5Request);
	    
	    // Cleanup
	    
	    webClient.closeAllWindows();		
	}
}
