package org.oddjob.rest.model;

public class NodeInfo {

	private int nodeId;
	private String name;
	private String icon;
	private int[] children;

	public NodeInfo(int nodeId, String name, String icon, int[] children) {
		this.nodeId = nodeId;
		this.name = name;
		this.icon = icon;
		this.children = children;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public int[] getChildren() {
		return children;
	}
}
