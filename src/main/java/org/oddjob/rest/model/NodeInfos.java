package org.oddjob.rest.model;

public class NodeInfos {

	private final long eventSeq;
	
	private final NodeInfo[] nodeInfo;
	
	public NodeInfos(long eventSeq, NodeInfo[] nodeInfo) {
		this.eventSeq = eventSeq;
		this.nodeInfo = nodeInfo;
	}
	
	public long getEventSeq() {
		return eventSeq;
	}
	
	public NodeInfo[] getNodeInfo() {
		return nodeInfo;
	}
}
