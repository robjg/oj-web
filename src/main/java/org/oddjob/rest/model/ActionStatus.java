package org.oddjob.rest.model;

public class ActionStatus {

	public enum Code {
		
		OK,
		FAILURE
		;
	}
	
	private final Code code;
	
	private final String message;
	
	private ActionStatus(Code code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public static ActionStatus ok() {
		return new ActionStatus(Code.OK, null);
	}
	
	public static ActionStatus failure(String message) {
		return new ActionStatus(Code.FAILURE, message);
	}
	
	public Code getCode() {
		return code;
	}	
	
	public String getMessage() {
		return message;
	}
}
