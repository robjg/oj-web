package org.oddjob.rest.model;

public class ComponentSummary {

	private final int nodeId;
	
	private final String name;
	
	private final String path;
	
	private final String state;
	
	public ComponentSummary(int nodeId, String name, 
			String componentPath, String state) {
		this.nodeId = nodeId;
		this.name = name;
		this.path = componentPath;
		this.state = state;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getState() {
		return state;
	}
}
