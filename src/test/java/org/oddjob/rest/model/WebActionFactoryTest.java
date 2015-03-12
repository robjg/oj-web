package org.oddjob.rest.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Executor;

import org.junit.Test;
import org.oddjob.jobs.structural.SequentialJob;
import org.oddjob.state.FlagState;
import org.oddjob.state.JobState;

public class WebActionFactoryTest {

	@Test
	public void testActionsForRunnable() {
		
		WebActionFactory test = new WebActionFactory(null);
		
		Runnable runnable = mock(Runnable.class);
		
		WebAction[] actions = test.actionsFor(runnable);
		
		assertEquals(1, actions.length);
		
		assertEquals("run", actions[0].getName());
	}
	
	@Test
	public void testActionsAll() {
		
		SequentialJob job = new SequentialJob();
		
		WebActionFactory test = new WebActionFactory(null);
		
		WebAction[] actions = test.actionsFor(job);
		
		assertEquals(4, actions.length);
		
		assertEquals("run", actions[0].getName());
		assertEquals("stop", actions[1].getName());
		assertEquals("soft-reset", actions[2].getName());
		assertEquals("hard-reset", actions[3].getName());
	}
	
	@Test
	public void testPerformActions() {
		
		FlagState job = new FlagState();
		
		WebActionFactory test = new WebActionFactory(new Executor() {
			
			@Override
			public void execute(Runnable command) {
				command.run();
			}
		});
		
		assertEquals(JobState.READY, job.lastStateEvent().getState());
		
		test.performAction(job, "run");
		
		assertEquals(JobState.COMPLETE, job.lastStateEvent().getState());
		
		test.performAction(job, "hard-reset");
		
		assertEquals(JobState.READY, job.lastStateEvent().getState());
	}

}
