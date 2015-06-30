package org.oddjob.rest.model;

import java.util.ArrayList;

import org.oddjob.arooa.design.screem.FileSelectionOptions;
import org.oddjob.input.InputMedium;
import org.oddjob.input.InputRequest;

public class WebForm implements InputMedium, WebDialog {

	public static WebForm createFrom(InputRequest[] requests) {

		WebForm form = new WebForm();
		
		for (InputRequest request : requests) {
			request.render(form);
		}
		
		return form;
	}
	
	private final WebDialog.Type dialogType = WebDialog.Type.FORM;
	
	private final ArrayList<WebField> fields = new ArrayList<WebField>();
	
	@Override
	public Type getDialogType() {
		return dialogType;
	}
	
	@Override
	public void prompt(String prompt, String defaultValue) {
		WebField webField = new WebField();
		webField.setType("text");
		webField.setLabel(prompt);
		webField.setValue(defaultValue);
		fields.add(webField);
	}
	
	@Override
	public void message(String message) {
		WebField webField = new WebField();
		webField.setType("message");
		webField.setLabel(message);
		fields.add(webField);
	}
	
	@Override
	public void confirm(String message, Boolean defaultValue) {
		WebField webField = new WebField();
		webField.setType("checkbox");
		webField.setLabel(message);
		webField.setValue(defaultValue != null && 
				defaultValue.booleanValue() 
				? "true" : "false");
		fields.add(webField);
	}
	
	@Override
	public void password(String prompt) {
		WebField webField = new WebField();
		webField.setType("password");
		webField.setLabel(prompt);
		fields.add(webField);
	}
	
	@Override
	public void file(String message, String defaultValue,
			FileSelectionOptions options) {
		WebField webField = new WebField();
		webField.setType("text");
		webField.setLabel(message);
		webField.setValue(defaultValue);
		fields.add(webField);
	}

}
