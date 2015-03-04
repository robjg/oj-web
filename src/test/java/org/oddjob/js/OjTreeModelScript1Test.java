package org.oddjob.js;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class OjTreeModelScript1Test {

	@Test
	public void testAll() throws Exception {
		
		File file = new File("src/test/webapp/OjTreeModelTest.html");
		
		URL url = file.toURI().toURL();
		
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	 
	    final HtmlPage page = webClient.getPage(url);
	    
	    // Test 1
	    
	    HtmlElement button1 = (HtmlElement) page.getElementById("runTestOne");
	    
	    button1.click();
	    
	    String result1 = page.getElementById("result1").asText();
	    
	    Assert.assertTrue(result1, result1.contains(
	    		"makeNodeInfoRequest_argCapture: 0 -1"));
	    
	    // Test 2
	    
	    HtmlElement button2 = (HtmlElement) page.getElementById("runTestTwo");
	    
	    button2.click();
	    
	    String result2 = page.getElementById("result2").asText();
	    
	    Assert.assertTrue(result2, result2.contains(
	    		"makeNodeInfoRequest_argCapture: 0 457"));
	    
	    // Test 3
	    
	    HtmlElement button3 = (HtmlElement) page.getElementById("runTestThree");
	    
	    button3.click();
	    
	    String result3 = page.getElementById("result3").asText();
	    
	    Assert.assertTrue(result3, result3.contains(
	    		"makeNodeInfoRequest_argCapture: 12,13 -1"));
	    
	    // Test 4
	    
	    HtmlElement button4 = (HtmlElement) page.getElementById("runTestFour");
	    
	    button4.click();
	    
	    String result4 = page.getElementById("result4").asText();
	    
	    Assert.assertTrue(result4, result4.contains(
	    		"makeNodeInfoRequest_argCapture: 0 457"));
	    
	    // Test 5
	    
	    HtmlElement button5 = (HtmlElement) page.getElementById("runTestFive");
	    
	    button5.click();
	    
	    String result5 = page.getElementById("result5").asText();
	    
	    Assert.assertTrue(result5, result5.contains(
	    		"makeNodeInfoRequest_argCapture: 0,12,13 572"));
	    
	    // Test 6
	    
	    HtmlElement button6 = (HtmlElement) page.getElementById("runTestSix");
	    
	    button6.click();
	    
	    String result6 = page.getElementById("result6").asText();
	    
	    Assert.assertTrue(result6, result6.contains(
	    		"makeNodeInfoRequest_argCapture: 0,12,13 612"));
	    
	    Assert.assertTrue(result6, result6.contains(
	    		"makeNodeInfoRequest_argCapture: 20,21,22 -1"));
	    
	    // Cleanup
	    
	    webClient.closeAllWindows();		
	}
}
