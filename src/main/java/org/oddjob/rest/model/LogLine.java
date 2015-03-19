package org.oddjob.rest.model;

public class LogLine {

	private final long logSeq;
	
	private final String level;
	
	private final String message;
	
	public LogLine(long sequence, String level, String message) {

		this.logSeq = sequence;
		this.level = level;
		this.message = message;
	}
	
	public long getLogSeq() {
		return logSeq;
	}
	
	public String getLevel() {
		return level;
	}
	
	public String getMessage() {
		return message;
	}
}
