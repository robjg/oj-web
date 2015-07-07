package org.oddjob.rest.model;

public class WebField {

	public enum Type {
		TEXT,
		MESSAGE,
		CHECKBOX,
		PASSWORD
	}
	
	private Type fieldType;
	
	private String label;
	
	private String name;
	
	private String value;

	public Type getFieldType() {
		return fieldType;
	}

	public void setFieldType(Type type) {
		this.fieldType = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
