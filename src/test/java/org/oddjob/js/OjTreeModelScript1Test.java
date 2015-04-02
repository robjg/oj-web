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

public class OjTreeModelScript1Test {

	private static Logger logger = Logger.getLogger(OjTreeModelScript1Test.class);
	
	@Test
	public void testAll() throws Exception {
		
		logger.info("-----  testing OjTreeModelTest.html ----- ");
		
		File file = new File("src/test/webapp/OjTreeModelTest.html");
		
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
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0 457", 
	    		result2Request);
	    	    
	    
	    String result2TreeCaptureLength = ((HtmlElement) page.getElementById(
	    		"result2").getFirstByXPath(
	    				"div[@class='ojTreeUI_captureLength']")).asText();
	    
	    assertEquals("ojTreeUI_captureLength: 1", 
	    		result2TreeCaptureLength );
	    
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
	    
	    String result4Request = ((HtmlElement) page.getElementById(
	    		"result4").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0 457", 
	    		result4Request);
	    	    
	    String result4TreeCaptureLength = ((HtmlElement) page.getElementById(
	    		"result4").getFirstByXPath(
	    				"div[@class='ojTreeUI_captureLength']")).asText();
	    
	    assertEquals("ojTreeUI_captureLength: 2", 
	    		result4TreeCaptureLength );
	    
	    // Test 5
	    
	    HtmlElement button5 = (HtmlElement) page.getElementById("runTestFive");
	    
	    button5.click();
	    
	    String result5Request = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0,12,13 572", 
	    		result5Request);
	    	    
	    String result5TreeCapture1 = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture1']")).asText();
	    
	    assertEquals("nodeUpdated: 12", 
	    		result5TreeCapture1);
	    
	    String result5TreeCapture2 = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture2']")).asText();
	    
	    assertEquals("nodeUpdated: 13 81,82", 
	    		result5TreeCapture2);
	    
	    String result5TreeCaptureLength = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='ojTreeUI_captureLength']")).asText();
	    
	    assertEquals("ojTreeUI_captureLength: 4", 
	    		result5TreeCaptureLength );
	    
	    // Test 6
	    
	    HtmlElement button6 = (HtmlElement) page.getElementById("runTestSix");
	    
	    button6.click();
	    
	    String result6Request1 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture1']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0,12,13 612", 
	    		result6Request1);
	    	    
	    String result6Request2 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture2']")).asText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 20,21,22 -1", 
	    		result6Request2);

	    String result6TreeCapture1 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture1']")).asText();
	    
	    assertEquals("nodeUpdated: 0 20,12,21,22", 
	    		result6TreeCapture1);
	    
	    String result6TreeCapture2 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture2']")).asText();
	    
	    assertEquals("nodeInserted: 0 0 20", 
	    		result6TreeCapture2);
	    
	    String result6TreeCapture3 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture3']")).asText();
	    
	    assertEquals("nodeInserted: 0 2 21", 
	    		result6TreeCapture3);
	    
	    String result6TreeCapture4 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture4']")).asText();
	    
	    assertEquals("nodeInserted: 0 3 22", 
	    		result6TreeCapture4);
	    
	    String result6TreeCapture5 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture5']")).asText();
	    
	    assertEquals("nodeRemoved: 13", 
	    		result6TreeCapture5);
	    
	    
	    String result6TreeCaptureLength = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_captureLength']")).asText();
	    
	    assertEquals("ojTreeUI_captureLength: 9", 
	    		result6TreeCaptureLength );
	    
	    
	    // Cleanup
	    
	    webClient.closeAllWindows();		
	}
}
