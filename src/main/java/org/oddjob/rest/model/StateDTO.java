package org.oddjob.rest.model;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.oddjob.state.StateEvent;

public class StateDTO {

	private final int nodeId;
	
	private final String state;
	
	private final long time;
	
	private final String exception;
	
	public StateDTO(int nodeId, StateEvent stateEvent) {
		this.nodeId = nodeId;
		this.state = stateEvent.getState().toString();
		this.time = stateEvent.getTime().getTime();
		Throwable t = stateEvent.getException();
		if (t == null) {
			this.exception = null;
		}
		else {
			StringWriter writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));
			this.exception = writer.toString();;
		}
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public String getState() {
		return state;
	}
	
	public long getTime() {
		return time;
	}
	
	public String getException() {
		return exception;
	}
}
