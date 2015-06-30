package org.oddjob.rest.model;

public interface WebDialog {
	
	enum Type {
		FORM
	}

	Type getDialogType();
}
