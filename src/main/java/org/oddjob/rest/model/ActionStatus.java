package org.oddjob.rest.model;

public class ActionStatus {

	public enum Code {
		
		OK,
		FAILURE
		;
	}
	
	private final Code status;
	
	private final String message;
	
	private ActionStatus(Code code, String message) {
		this.status = code;
		this.message = message;
	}
	
	public static ActionStatus ok() {
		return new ActionStatus(Code.OK, null);
	}
	
	public static ActionStatus failure(String message) {
		return new ActionStatus(Code.FAILURE, message);
	}
	
	public Code getStatus() {
		return status;
	}	
	
	public String getMessage() {
		return message;
	}
}
