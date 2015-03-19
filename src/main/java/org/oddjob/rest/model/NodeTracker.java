package org.oddjob.rest.model;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.OJConstants;
import org.oddjob.Structural;
import org.oddjob.logging.ConsoleArchiver;
import org.oddjob.logging.LogArchiver;
import org.oddjob.logging.cache.LocalConsoleArchiver;
import org.oddjob.logging.log4j.Log4jArchiver;

/**
 * Adds listeners to a node to track changes.
 */
class NodeTracker {
	
	private final Object node;
	
	private final int nodeId;
	
	private volatile long nameSequence;
			
	private volatile long iconSequence;
	
	private volatile String icon;
	
	private volatile long childrenSequence;
	
	private final List<Integer> children;
	
	private final LogArchiver logArchiver;
	
	private final ConsoleArchiver consoleArchiver;

	public NodeTracker(Object node, int nodeId, long sequence) {
		this(node, nodeId, sequence, null);
	}

	public NodeTracker(Object node, 
			int nodeId, long sequence, NodeTracker parent) {
		
		this.node = node;
		this.nodeId = nodeId;
		this.nameSequence = sequence;
		
		if (node instanceof Structural) {
			this.children = new ArrayList<>();
		}
		else {
			this.children = null;
		}
		
		if (parent == null) {
			this.logArchiver = new Log4jArchiver(node, 
					OJConstants.DEFAULT_LOG_FORMAT);
		}
		else if (parent.node instanceof LogArchiver) {
			this.logArchiver = (LogArchiver) parent.node;
		}
		else {
			this.logArchiver = parent.getLogArchiver();
		}
		
		if (parent == null) {
			this.consoleArchiver = new LocalConsoleArchiver();
		}
		else if (parent.node instanceof ConsoleArchiver) {
			this.consoleArchiver = (ConsoleArchiver) parent.node;
		}
		else {
			this.consoleArchiver = parent.getConsoleArchiver();
		}
	}
	
	synchronized NodeTracker addChild(int index, Object node,
			int nodeId, long sequence) {
		this.childrenSequence = sequence;
		this.children.add(index, nodeId);
		
		return new NodeTracker(node, nodeId, sequence, this);
	}
	
	synchronized int removeChild(int index, long sequence) {
		this.childrenSequence = sequence;
		return children.remove(index);
	}
	
	synchronized void updateIcon(String icon, long sequence) {
		this.icon = icon;
		this.iconSequence = sequence;
	}
	
	synchronized NodeInfo infoFor(long fromSequence) {
		
		String name = null;
		boolean hasInfo = false;
		if (fromSequence < nameSequence) {
			name = node.toString();
			hasInfo = true;
		}
		
		String icon = null;
		if (fromSequence < iconSequence) {
			icon = this.icon;
			hasInfo = true;
		}
		
		int[] children = null;
		if (this.children != null &&
				fromSequence < childrenSequence) {
			children = new int[this.children.size()];
			for (int i = 0; i < children.length; ++i) {
				children[i] = this.children.get(i);
			}
			hasInfo = true;
		}
		
		if (hasInfo) {
			return new NodeInfo(nodeId, name, icon, children);
		}
		else {
			return null;
		}
	}
		
	public Object getNode() {
		return node;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public LogArchiver getLogArchiver() {
		return logArchiver;
	}
		
	public ConsoleArchiver getConsoleArchiver() {
		return consoleArchiver;
	}	

}