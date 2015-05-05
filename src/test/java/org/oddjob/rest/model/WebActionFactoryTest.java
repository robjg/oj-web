package org.oddjob.rest.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.oddjob.jobs.structural.SequentialJob;
import org.oddjob.state.FlagState;
import org.oddjob.state.JobState;

public class WebActionFactoryTest {

	private static Logger logger = Logger.getLogger(WebActionFactoryTest.class);
	
	@Test
	public void testActionsForRunnable() {
		
		logger.info("testActionsForRunnable");
		
		WebActionFactory test = new WebActionFactory(null);
		
		Runnable runnable = mock(Runnable.class);
		
		WebAction<?>[] actions = test.actionsFor(runnable);
		
		assertEquals(1, actions.length);
		
		assertEquals("run", actions[0].getName());
	}
	
	@Test
	public void testActionsAll() {
		
		logger.info("testActionsAll");
		
		SequentialJob job = new SequentialJob();
		
		WebActionFactory test = new WebActionFactory(null);
		
		WebAction<?>[] actions = test.actionsFor(job);
		
		assertEquals(5, actions.length);
		
		assertEquals("run", actions[0].getName());
		assertEquals("stop", actions[1].getName());
		assertEquals("soft-reset", actions[2].getName());
		assertEquals("hard-reset", actions[3].getName());
		assertEquals("force", actions[4].getName());
	}
	
	@Test
	public void testPerformActions() {
		
		logger.info("testPerformActions");
		
		FlagState job = new FlagState();
		
		WebActionFactory test = new WebActionFactory(new Executor() {
			
			@Override
			public void execute(Runnable command) {
				command.run();
			}
		});
		
		assertEquals(JobState.READY, job.lastStateEvent().getState());
		
		test.performAction(job, "run", null);
		
		assertEquals(JobState.COMPLETE, job.lastStateEvent().getState());
		
		test.performAction(job, "hard-reset", null);
		
		assertEquals(JobState.READY, job.lastStateEvent().getState());
	}

}
