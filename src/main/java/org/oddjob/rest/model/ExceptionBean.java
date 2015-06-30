package org.oddjob.rest.model;

public class ExceptionBean {

	public static ExceptionBean createFrom(Exception exception) {
		ExceptionBean bean = new ExceptionBean();
		bean.setMessage(exception.getMessage());
		return bean;
	}
	
	private String message;
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
