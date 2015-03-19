package org.oddjob.rest.model;

public class LogLines {

	private final int nodeId;
	
	private final LogLine[] logLines;
	
	public LogLines(int nodeId, LogLine[] logLines) {
		this.nodeId = nodeId;
		this.logLines = logLines;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public LogLine[] getLogLines() {
		return logLines;
	}
}
