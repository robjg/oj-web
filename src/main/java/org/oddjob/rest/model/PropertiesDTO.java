package org.oddjob.rest.model;

import java.util.Map;

public class PropertiesDTO {

	private final int nodeId;
	
	private final Map<String, String> properties;
	
	public PropertiesDTO(int nodeId,
			Map<String, String> properties) {
		this.nodeId = nodeId;
		this.properties = properties;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
}
