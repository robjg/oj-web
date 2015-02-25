package org.oddjob.js;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;

public class OjTreeUIScriptTest {

	@Test
	public void testRootNode() throws Exception {
		
		File file = new File("src/test/webapp/OjTreeUITest.html");
		
		URL url = file.toURI().toURL();
		
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);
	 
//		WebClientUtils.attachVisualDebugger(webClient);
	    
	    final HtmlPage page = webClient.getPage(url);
	    
//		webClient.waitForBackgroundJavaScript(30 * 1000);
		
	    DomElement rootElement = page.getElementById("ojNode0");
	    
	    Assert.assertNotNull(rootElement);
	    
	    HtmlUnorderedList rootUl = (HtmlUnorderedList) rootElement.getFirstByXPath("ul");
	    
	    Assert.assertNotNull(rootUl);
	    
	    Assert.assertEquals(0, rootUl.getChildElementCount());
	    
	    // Test 1
	    
	    HtmlElement button1 = (HtmlElement) page.getElementById("runTestOne");
	    
	    button1.click();
	    
	    Assert.assertEquals(3, rootUl.getChildElementCount());
	 
	    List<?> listItemToggles = rootUl.getByXPath("li/img[@class='toggle']");
	    
	    Assert.assertEquals(0,  listItemToggles.size());

	    List<?> listItemIcons = rootUl.getByXPath("li/img[@class='icon']");
	    
	    Assert.assertEquals(3, listItemIcons.size());
	    
	    HtmlImage icon1 = (HtmlImage) listItemIcons.get(0);
	    HtmlImage icon2 = (HtmlImage) listItemIcons.get(1);
	    HtmlImage icon3 = (HtmlImage) listItemIcons.get(2);

	    Assert.assertEquals("executing", icon1.getAltAttribute());
	    Assert.assertEquals("executing", icon2.getAltAttribute());
	    Assert.assertEquals("ready", icon3.getAltAttribute());
	    
	    // Test 2
	    
	    HtmlElement button2 = (HtmlElement) page.getElementById("runTestTwo");
	    
	    button2.click();
	    
	    listItemToggles = rootUl.getByXPath("li[@id='ojNode1']/img[@class='toggle']");
	    
	    Assert.assertEquals(1,  listItemToggles.size());

	    HtmlImage toggle1 = (HtmlImage) listItemToggles.get(0);
	    
	    Assert.assertEquals("expand", toggle1.getAltAttribute());
	    
	    listItemIcons = rootUl.getByXPath("li/img[@class='icon']");
	    
	    Assert.assertEquals(3, listItemIcons.size());
	    
	    icon3 = (HtmlImage) listItemIcons.get(2);

	    Assert.assertEquals("executing", icon3.getAltAttribute());

	    // Test 3
	    
	    HtmlElement button3 = (HtmlElement) page.getElementById("runTestThree");
	    
	    button3.click();
	    
	    HtmlListItem node2Li = (HtmlListItem) page.getElementById("ojNode2");

	    List<?> node2Children = node2Li.getByXPath("ul/li");
	    
	    Assert.assertEquals(3, node2Children.size());
	    
	    listItemToggles = node2Li.getByXPath("img[@class='toggle']");
	    
	    Assert.assertEquals(1,  listItemToggles.size());

	    toggle1 = (HtmlImage) listItemToggles.get(0);
	    
	    Assert.assertEquals("collapse", toggle1.getAltAttribute());
	    
	    // Test 4
	    
	    HtmlElement button4 = (HtmlElement) page.getElementById("runTestFour");
	    
	    button4.click();
	    
	    node2Li = (HtmlListItem) page.getElementById("ojNode2");

	    node2Children = node2Li.getByXPath("ul/li");
	    
	    Assert.assertEquals(0, node2Children.size());
	    
	    listItemToggles = node2Li.getByXPath("img[@class='toggle']");
	    
	    Assert.assertEquals(0,  listItemToggles.size());

	    // Test 4
	    
	    HtmlElement button5 = (HtmlElement) page.getElementById("runTestFive");
	    
	    button5.click();
	    
	    Assert.assertEquals(0, rootUl.getChildElementCount());
		 
	    listItemToggles = rootElement.getByXPath("img[@class='toggle']");
	    
	    Assert.assertEquals(1,  listItemToggles.size());

	    toggle1 = (HtmlImage) listItemToggles.get(0);
	    
	    Assert.assertEquals("expand", toggle1.getAltAttribute());
	    
	    // Cleanup
	    
	    webClient.closeAllWindows();		
	}
	
}
