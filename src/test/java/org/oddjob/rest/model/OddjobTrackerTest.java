package org.oddjob.rest.model;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.Oddjob;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.FlagState;

public class OddjobTrackerTest {
	
	@Test
	public void testSimleIconEvents() {
		FlagState flagJob = new FlagState();
		
		OddjobTracker tracker = new OddjobTracker();
		
		int nodeId = tracker.track(flagJob);
		
		assertEquals(0, nodeId);
		
		// First info
		
		NodeInfos nodeInfos = tracker.infoFor(-1, 0);
		
		assertEquals(1, nodeInfos.getEventSeq());
		
		NodeInfo nodeInfo = nodeInfos.getNodeInfo()[0];
		
		assertEquals(0, nodeInfo.getNodeId());
		assertEquals(FlagState.class.getSimpleName(), nodeInfo.getName());
		assertEquals("ready", nodeInfo.getIcon());
		assertEquals(null, nodeInfo.getChildren());
		
		flagJob.run();
		
		// Second info
		
		nodeInfos = tracker.infoFor(-1, 0);
		
		assertEquals(3, nodeInfos.getEventSeq());
		
		nodeInfo = nodeInfos.getNodeInfo()[0];
		
		assertEquals(0, nodeInfo.getNodeId());
		assertEquals(FlagState.class.getSimpleName(), nodeInfo.getName());
		assertEquals("complete", nodeInfo.getIcon());
		assertEquals(null, nodeInfo.getChildren());
	
		// Third Info
		
		nodeInfos = tracker.infoFor(3, 0);
		
		assertEquals(3, nodeInfos.getEventSeq());
		assertEquals(0, nodeInfos.getNodeInfo().length);
				
		flagJob.hardReset();
		
		// Fourth Info
		
		nodeInfos = tracker.infoFor(3, 0);
		
		assertEquals(4, nodeInfos.getEventSeq());
		nodeInfo = nodeInfos.getNodeInfo()[0];
		
		assertEquals(0, nodeInfo.getNodeId());
		assertEquals(null, nodeInfo.getName());
		assertEquals("ready", nodeInfo.getIcon());
		assertEquals(null, nodeInfo.getChildren());
	}

	@Test
	public void testFullLifecycle() {
		
		String xml = 
				"<oddjob>"
				+ "<job>"
				+ "<sequential>"
				+ "<jobs>"
				+ "	<echo>First Job</echo>"
				+ " <echo>Second Job</echo>"
				+ "</jobs>"
				+ "</sequential>"
				+ "</job>"
				+ "</oddjob>";
		
		Oddjob oddjob = new Oddjob();
		oddjob.setName("My Oddjob");
		oddjob.setConfiguration(new XMLConfiguration("XML", xml));
		
		oddjob.load();
		
		OddjobTracker test = new OddjobTracker();
		
		int oddjobId = test.track(oddjob);
		Assert.assertEquals(0, oddjobId);
		
		NodeInfos oddjobNodeInfos = test.infoFor(-1L, oddjobId);

		Assert.assertEquals(1, oddjobNodeInfos.getNodeInfo().length);
		
		Assert.assertTrue(oddjobNodeInfos.getEventSeq() > 5);
		
		NodeInfo oddjobNodeInfo = oddjobNodeInfos.getNodeInfo()[0];
		
		Assert.assertEquals("My Oddjob", oddjobNodeInfo.getName());
		Assert.assertEquals("ready", oddjobNodeInfo.getIcon());
		Assert.assertEquals(1, oddjobNodeInfo.getChildren().length);
		Assert.assertEquals(1, oddjobNodeInfo.getChildren()[0]);
		
		int sequentialJobNodeId = oddjobNodeInfo.getChildren()[0];
		
		NodeInfos sequentialNodeInfos = test.infoFor(-1L, 
				sequentialJobNodeId );

		Assert.assertEquals(1, sequentialNodeInfos.getNodeInfo().length);
		
		long sequentialJobEventSeq = sequentialNodeInfos.getEventSeq();
		Assert.assertTrue(sequentialJobEventSeq > 5);
		
		NodeInfo sequentialNodeInfo = sequentialNodeInfos.getNodeInfo()[0];
		
		Assert.assertEquals("SequentialJob", sequentialNodeInfo.getName());
		Assert.assertEquals("ready", sequentialNodeInfo.getIcon());
		Assert.assertEquals(2, sequentialNodeInfo.getChildren().length);
		Assert.assertEquals(2, sequentialNodeInfo.getChildren()[0]);
		Assert.assertEquals(3, sequentialNodeInfo.getChildren()[1]);
		
		// Poll Sequential Job
		
		sequentialNodeInfos = test.infoFor(
				sequentialJobEventSeq , 
				sequentialJobNodeId);

		Assert.assertEquals(0, sequentialNodeInfos.getNodeInfo().length);
		
		Assert.assertEquals(sequentialJobEventSeq, 
				sequentialNodeInfos.getEventSeq());
		
		// Run 
		
		oddjob.run();
		
		sequentialNodeInfos = test.infoFor(
				sequentialJobEventSeq , 
				sequentialJobNodeId);

		Assert.assertEquals(1, sequentialNodeInfos.getNodeInfo().length);
		
		Assert.assertTrue(sequentialJobEventSeq <  
				sequentialNodeInfos.getEventSeq());
		
		sequentialNodeInfo = sequentialNodeInfos.getNodeInfo()[0];
		
		Assert.assertEquals(null, sequentialNodeInfo.getName());
		Assert.assertEquals("complete", sequentialNodeInfo.getIcon());
		Assert.assertEquals(null, sequentialNodeInfo.getChildren());
	}
}
