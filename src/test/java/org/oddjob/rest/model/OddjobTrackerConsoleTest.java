package org.oddjob.rest.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.oddjob.OddjobConsole;
import org.oddjob.Structural;
import org.oddjob.arooa.logging.LogLevel;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.logging.ConsoleOwner;
import org.oddjob.logging.LogArchive;
import org.oddjob.logging.LogArchiver;
import org.oddjob.logging.cache.LogArchiveImpl;
import org.oddjob.structural.StructuralEvent;
import org.oddjob.structural.StructuralListener;

public class OddjobTrackerConsoleTest extends Assert {

	private static final Logger logger = LoggerFactory.getLogger(OddjobTrackerConsoleTest.class);
	
	public static class Job1 implements Structural {
		
		Job2 job2 = new Job2();
		
		@Override
		public void addStructuralListener(StructuralListener listener) {
			listener.childAdded(new StructuralEvent(this, job2, 0));
		}
		
		@Override
		public void removeStructuralListener(StructuralListener listener) {
			throw new RuntimeException("Unexpected!");
		}
	}
	
	public static class Job2 implements ConsoleOwner {

		LogArchiveImpl consoleArchive = new LogArchiveImpl(
					"OurConsole", LogArchiver.MAX_HISTORY);
		
		@Override
		public LogArchive consoleLog() {
			return consoleArchive;
		}
	}
	
	@Test
	public void testConsoleLines() throws ClassNotFoundException {
		
		logger.info("-------------  testConsoleLines  -------------------");
		
		OddjobTracker test = new OddjobTracker(new StandardArooaSession());
		
		Job1 job1 = new Job1();
		
		int rootId = test.track(job1);

		try (OddjobConsole.Close consoleClose = OddjobConsole.initialise()) {
			
			System.out.println("Job1 - Console Line 1");
			System.out.println("Job1 - Console Line 2");
			
			job1.job2.consoleArchive.addEvent(LogLevel.INFO, 
					"Job2 - Console Line 1");
			
			LogLines result1 = test.consoleLinesFor(rootId, -1);
			
			assertEquals(0, result1.getNodeId());
			
			LogLine[] job1Lines = result1.getLogLines();
			
			int lineCount = job1Lines.length;
			
			assertEquals(lineCount - 2, job1Lines[lineCount - 2].getLogSeq());
			assertEquals("INFO", job1Lines[lineCount - 2].getLevel());
			assertTrue(job1Lines[lineCount - 2].getMessage(), 
					job1Lines[lineCount - 2].getMessage().trim().endsWith("Job1 - Console Line 1"));
			
			assertEquals(lineCount - 1, job1Lines[lineCount - 1].getLogSeq());
			assertEquals("INFO", job1Lines[lineCount - 1].getLevel());
			assertTrue(job1Lines[lineCount - 1].getMessage(), 
					job1Lines[lineCount - 1].getMessage().trim().endsWith("Job1 - Console Line 2"));
			
			LogLines result2 = test.consoleLinesFor(rootId + 1, -1);
			
			assertEquals(1, result2.getNodeId());
			
			LogLine[] job2Lines = result2.getLogLines();
			
			assertEquals(1, job2Lines.length);		
			
			assertEquals(0, job2Lines[0].getLogSeq());
			assertEquals("INFO", job2Lines[0].getLevel());
			assertTrue(job2Lines[0].getMessage(), 
					job2Lines[0].getMessage().trim().endsWith("Job2 - Console Line 1"));
			
			job1.job2.consoleArchive.addEvent(LogLevel.INFO, 
					"Job2 - Console Line 2");
			
			result2 = test.consoleLinesFor(rootId + 1, 0);
			
			assertEquals(1, result2.getNodeId());
			
			job2Lines = result2.getLogLines();
			
			assertEquals(1, job2Lines.length);		
			
			assertEquals(1, job2Lines[0].getLogSeq());
			assertEquals("INFO", job2Lines[0].getLevel());
			assertTrue(job2Lines[0].getMessage(), 
					job2Lines[0].getMessage().trim().endsWith("Job2 - Console Line 2"));
			
		}
		
	}
}
