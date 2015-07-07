package org.oddjob.rest.model;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.design.screem.FileSelectionOptions;
import org.oddjob.input.InputMedium;
import org.oddjob.input.InputRequest;

public class WebForm implements InputMedium, WebDialog {

	public static WebForm createFrom(InputRequest[] requests) {

		WebForm form = new WebForm();
		
		for (InputRequest request : requests) {
			request.render(form);
		}
		
		List<WebField> fields = form.getFields();
		for (int i = 0; i < requests.length; ++i) {
			fields.get(i).setName(requests[i].getProperty());
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
		webField.setFieldType(WebField.Type.TEXT);
		webField.setLabel(prompt);
		webField.setValue(defaultValue);
		fields.add(webField);
	}
	
	@Override
	public void message(String message) {
		WebField webField = new WebField();
		webField.setFieldType(WebField.Type.MESSAGE);
		webField.setLabel(message);
		fields.add(webField);
	}
	
	@Override
	public void confirm(String message, Boolean defaultValue) {
		WebField webField = new WebField();
		webField.setFieldType(WebField.Type.CHECKBOX);
		webField.setLabel(message);
		webField.setValue(defaultValue != null && 
				defaultValue.booleanValue() 
				? "true" : "false");
		fields.add(webField);
	}
	
	@Override
	public void password(String prompt) {
		WebField webField = new WebField();
		webField.setFieldType(WebField.Type.PASSWORD);
		webField.setLabel(prompt);
		fields.add(webField);
	}
	
	@Override
	public void file(String message, String defaultValue,
			FileSelectionOptions options) {
		WebField webField = new WebField();
		webField.setFieldType(WebField.Type.TEXT);
		webField.setLabel(message);
		webField.setValue(defaultValue);
		fields.add(webField);
	}

	public ArrayList<WebField> getFields() {
		return fields;
	}
}
