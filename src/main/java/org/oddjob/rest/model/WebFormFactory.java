package org.oddjob.rest.model;

import org.oddjob.input.InputRequest;

public class WebFormFactory {

	public WebForm createFrom(InputRequest[] requests) {

		WebForm form = new WebForm();
		
		for (InputRequest request : requests) {
			request.render(form);
		}
		
		return form;
	}
	
}
