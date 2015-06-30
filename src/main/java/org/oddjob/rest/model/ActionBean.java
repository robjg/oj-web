package org.oddjob.rest.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ActionBean {

	public enum Type {
		SIMPLE,
		FORM,
		;
	}
	
	private static final Map<Class<?>, ActionBean.Type> typeMapping;	
	
	static {
		typeMapping = new HashMap<>();
		typeMapping.put(Void.class, ActionBean.Type.SIMPLE);
		typeMapping.put(Properties.class, ActionBean.Type.FORM);
	}
	
	public static ActionBean createFrom(WebAction<?> webAction) {
	
		ActionBean actionBean = new ActionBean();
		actionBean.setName(webAction.getName());
		actionBean.setDisplayName(webAction.getDisplayName());
		
		ActionBean.Type actionType = typeMapping.get(
				webAction.getParamsType());
		
		if (actionType == null) {
			throw new IllegalArgumentException(
					"No mapping for param type " + webAction.getParamsType());
		}
		
		actionBean.setActionType(actionType);
		
		return actionBean;
	}
	
	public static ActionBean[] createManyFrom(WebAction<?>[] webActions) {
		ActionBean[] beans = new ActionBean[webActions.length];
		for (int i = 0; i < beans.length; ++i) {
			beans[i] = createFrom(webActions[i]);
		}
		return beans;
	}
	
	private Type actionType;
	
	private String name;
	
	private String displayName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Type getActionType() {
		return actionType;
	}

	public void setActionType(Type actionType) {
		this.actionType = actionType;
	}	
}
