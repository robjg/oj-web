package org.oddjob.js;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Test;
import org.oddjob.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class OjTreeModelScript3Test extends Assert {

	private static Logger logger = LoggerFactory.getLogger(OjTreeModelScript3Test.class);
	
	@Test
	public void testAll() throws Exception {
		
		logger.info("-----  testing OjTreeModelTest3.html  -----");
		
		OurDirs ourDirs = new OurDirs();
		File file = ourDirs.relative("src/test/webapp/OjTreeModelTest3.html");
		
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
	    
	    assertEquals("nodeInitialised: 0 Some Jobs", 
	    		result1TreeCapture);
	    
	    // Test 2
	    
	    HtmlElement button2 = (HtmlElement) page.getElementById("runTestTwo");
	    
	    button2.click();
	    
	    String result2Request = ((HtmlElement) page.getElementById(
	    		"result2").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0 457",
	    		result2Request);
	    
	    String result2TreeCapture = ((HtmlElement) page.getElementById(
	    		"result2").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture']")).asText();
	    
	    assertEquals("nodeUpdated: 0", 
	    		result2TreeCapture);
	    
	    // Test 3
	    
	    HtmlElement button3 = (HtmlElement) page.getElementById("runTestThree");
	    
	    button3.click();
	    
	    String result3Request = ((HtmlElement) page.getElementById(
	    		"result3").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 12,13 -1",
	    		result3Request);
	    
	    String result3TreeCapture = ((HtmlElement) page.getElementById(
	    		"result3").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture']")).asText();
	    
	    assertEquals("nodeExpanded: 0 12 13", 
	    		result3TreeCapture);
	    
	    // Test 4
	    
	    HtmlElement button4 = (HtmlElement) page.getElementById("runTestFour");
	    
	    button4.click();
	    
	    String result4TreeCapture1 = ((HtmlElement) page.getElementById(
	    		"result4").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture1']")).asText();
	    
	    assertEquals("nodeUpdated: 0", 
	    		result4TreeCapture1);
	    
	    String result4TreeCapture2 = ((HtmlElement) page.getElementById(
	    		"result4").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture2']")).asText();
	    
	    assertEquals("nodeUpdated: 12", 
	    		result4TreeCapture2);
	    
	    // Test 5
	    
	    HtmlElement button5 = (HtmlElement) page.getElementById("runTestFive");
	    
	    button5.click();
	    
	    String result5Request = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0 572",
	    		result5Request);
	    
	    String result5TreeCapture = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture']")).asText();
	    
	    assertEquals("nodeUpdated: 0", 
	    		result5TreeCapture);
	    
	    // Cleanup
	    
	    webClient.close();		
	}
}
