package org.oddjob.rest.model;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.describe.UniversalDescriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.oddjob.Structural;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.logging.LogEnabled;
import org.oddjob.structural.StructuralEvent;
import org.oddjob.structural.StructuralListener;

public class OddjobTrackerLogTest extends Assert {

	private final Logger logger1 = LoggerFactory.getLogger(Job1.class);
	private final Logger logger2 = LoggerFactory.getLogger(Job2.class);
	
	public static class Job1 implements LogEnabled, Structural {
		
		@Override
		public String loggerName() {
			return getClass().getName();
		}
		
		@Override
		public void addStructuralListener(StructuralListener listener) {
			listener.childAdded(new StructuralEvent(this, new Job2(), 0));
		}
		
		@Override
		public void removeStructuralListener(StructuralListener listener) {
			throw new RuntimeException("Unexpected!");
		}
	}
	
	public static class Job2 implements LogEnabled {
		
		@Override
		public String loggerName() {
			return getClass().getName();
		}
	}
	
	@Test
	public void testLogLines() {

		ArooaSession session = new StandardArooaSession();
		OddjobTracker test = new OddjobTracker(session.getBeanRegistry(), new UniversalDescriber(session));
		
		int rootId = test.track(new Job1());
		
		logger1.info("Job1 - Log Line 1");
		logger1.info("Job1 - Log Line 2");
		
		logger2.info("Job2 - Log Line 1");
		
		LogLines result1 = test.logLinesFor(rootId, -1);
		
		assertEquals(0, result1.getNodeId());
		
		LogLine[] job1Lines = result1.getLogLines();
		
		assertEquals(2, job1Lines.length);
		
		assertEquals(0, job1Lines[0].getLogSeq());
		assertEquals("INFO", job1Lines[0].getLevel());
		assertTrue(job1Lines[0].getMessage(), 
				job1Lines[0].getMessage().trim().endsWith("Job1 - Log Line 1"));
		
		assertEquals(1, job1Lines[1].getLogSeq());
		assertEquals("INFO", job1Lines[1].getLevel());
		assertTrue(job1Lines[1].getMessage(), 
				job1Lines[1].getMessage().trim().endsWith("Job1 - Log Line 2"));
		
		LogLines result2 = test.logLinesFor(rootId + 1, -1);
		
		assertEquals(1, result2.getNodeId());
		
		LogLine[] job2Lines = result2.getLogLines();
		
		assertEquals(1, job2Lines.length);		
		
		assertEquals(0, job2Lines[0].getLogSeq());
		assertEquals("INFO", job2Lines[0].getLevel());
		assertTrue(job2Lines[0].getMessage(), 
				job2Lines[0].getMessage().trim().endsWith("Job2 - Log Line 1"));
		
		logger2.info("Job2 - Log Line 2");
		
		result2 = test.logLinesFor(rootId + 1, 0);
		
		assertEquals(1, result2.getNodeId());
		
		job2Lines = result2.getLogLines();
		
		assertEquals(1, job2Lines.length);		
		
		assertEquals(1, job2Lines[0].getLogSeq());
		assertEquals("INFO", job2Lines[0].getLevel());
		assertTrue(job2Lines[0].getMessage(), 
				job2Lines[0].getMessage().trim().endsWith("Job2 - Log Line 2"));
		
	}
}
