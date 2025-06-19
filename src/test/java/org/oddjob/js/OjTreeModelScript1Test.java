package org.oddjob.js;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.oddjob.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;

@Ignore("Old JQuery htmlunit test - needs updating or replacing.")
public class OjTreeModelScript1Test extends Assert {

	private static final Logger logger = LoggerFactory.getLogger(OjTreeModelScript1Test.class);
	
	@Test
	public void testAll() throws Exception {
		
		logger.info("-----  testing OjTreeModelTest.html ----- ");
		
		OurDirs ourDirs = new OurDirs();
		File file = ourDirs.relative("src/test/webapp/OjTreeModelTest.html");
		
		assertThat(file.exists(), is(true));
		
		URL url = file.toURI().toURL();
		
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	 
	    final HtmlPage page = webClient.getPage(url);
	    
	    // Test 1
	    
	    HtmlElement button1 = (HtmlElement) page.getElementById("runTestOne");
	    
	    button1.click();
	    
	    String result1Request = ((HtmlElement) page.getElementById(
	    		"result1").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asNormalizedText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0 -1", 
	    		result1Request);
	    
	    String result1TreeCapture = ((HtmlElement) page.getElementById(
	    		"result1").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture']")).asNormalizedText();
	    
	    assertEquals("treeInitialised: 0 Some Jobs", 
	    		result1TreeCapture);
	    
	    // Test 2
	    
	    HtmlElement button2 = (HtmlElement) page.getElementById("runTestTwo");
	    
	    button2.click();
	    
	    String result2Request = ((HtmlElement) page.getElementById(
	    		"result2").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asNormalizedText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0 457", 
	    		result2Request);
	    	    
	    
	    String result2TreeCaptureLength = ((HtmlElement) page.getElementById(
	    		"result2").getFirstByXPath(
	    				"div[@class='ojTreeUI_captureLength']")).asNormalizedText();
	    
	    assertEquals("ojTreeUI_captureLength: 1", 
	    		result2TreeCaptureLength );
	    
	    // Test 3
	    
	    HtmlElement button3 = (HtmlElement) page.getElementById("runTestThree");
	    
	    button3.click();
	    
	    String result3Request = ((HtmlElement) page.getElementById(
	    		"result3").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asNormalizedText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 12,13 -1", 
	    		result3Request);
	    
	    String result3TreeCapture = ((HtmlElement) page.getElementById(
	    		"result3").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture']")).asNormalizedText();
	    
	    assertEquals("nodeExpanded: 0 12 13", 
	    		result3TreeCapture);
	    
	    // Test 4
	    
	    HtmlElement button4 = (HtmlElement) page.getElementById("runTestFour");
	    
	    button4.click();
	    
	    String result4Request = ((HtmlElement) page.getElementById(
	    		"result4").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asNormalizedText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0 457", 
	    		result4Request);
	    	    
	    String result4TreeCaptureLength = ((HtmlElement) page.getElementById(
	    		"result4").getFirstByXPath(
	    				"div[@class='ojTreeUI_captureLength']")).asNormalizedText();
	    
	    assertEquals("ojTreeUI_captureLength: 2", 
	    		result4TreeCaptureLength );
	    
	    // Test 5
	    
	    HtmlElement button5 = (HtmlElement) page.getElementById("runTestFive");
	    
	    button5.click();
	    
	    String result5Request = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture']")).asNormalizedText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0,12,13 572", 
	    		result5Request);
	    	    
	    String result5TreeCapture1 = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture1']")).asNormalizedText();
	    
	    assertEquals("nodeUpdated: 12", 
	    		result5TreeCapture1);
	    
	    String result5TreeCapture2 = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture2']")).asNormalizedText();
	    
	    assertEquals("nodeUpdated: 13 81,82", 
	    		result5TreeCapture2);
	    
	    String result5TreeCaptureLength = ((HtmlElement) page.getElementById(
	    		"result5").getFirstByXPath(
	    				"div[@class='ojTreeUI_captureLength']")).asNormalizedText();
	    
	    assertEquals("ojTreeUI_captureLength: 4", 
	    		result5TreeCaptureLength );
	    
	    // Test 6
	    
	    HtmlElement button6 = (HtmlElement) page.getElementById("runTestSix");
	    
	    button6.click();
	    
	    String result6Request1 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture1']")).asNormalizedText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 0,12,13 612", 
	    		result6Request1);
	    	    
	    String result6Request2 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='makeNodeInfoRequest_argCapture2']")).asNormalizedText();
	    
	    assertEquals("makeNodeInfoRequest_argCapture: 20,21,22 -1", 
	    		result6Request2);

	    String result6TreeCapture1 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture1']")).asNormalizedText();
	    
	    assertEquals("nodeUpdated: 0 20,12,21,22", 
	    		result6TreeCapture1);
	    
	    String result6TreeCapture2 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture2']")).asNormalizedText();
	    
	    assertEquals("nodeInserted: 0 0 20", 
	    		result6TreeCapture2);
	    
	    String result6TreeCapture3 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture3']")).asNormalizedText();
	    
	    assertEquals("nodeInserted: 0 2 21", 
	    		result6TreeCapture3);
	    
	    String result6TreeCapture4 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture4']")).asNormalizedText();
	    
	    assertEquals("nodeInserted: 0 3 22", 
	    		result6TreeCapture4);
	    
	    String result6TreeCapture5 = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_capture5']")).asNormalizedText();
	    
	    assertEquals("nodeRemoved: 13", 
	    		result6TreeCapture5);
	    
	    
	    String result6TreeCaptureLength = ((HtmlElement) page.getElementById(
	    		"result6").getFirstByXPath(
	    				"div[@class='ojTreeUI_captureLength']")).asNormalizedText();
	    
	    assertEquals("ojTreeUI_captureLength: 9", 
	    		result6TreeCaptureLength );
	    
	    
	    // Cleanup
	    
	    webClient.close();		
	}
}
