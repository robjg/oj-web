package org.oddjob.rest.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.oddjob.Iconic;
import org.oddjob.Structural;
import org.oddjob.images.IconEvent;
import org.oddjob.images.IconListener;
import org.oddjob.structural.StructuralEvent;
import org.oddjob.structural.StructuralListener;

public class OddjobTracker {

	private final AtomicLong sequenceNumber = new AtomicLong();
	
	private final AtomicInteger nodeId = new AtomicInteger();
	
	private final Map<Integer, Tracker> nodes = new ConcurrentHashMap<>();
	
	private final IconRegistry iconRegistry = new IconRegistry();
	
	public int track(Object node) {
		
		int nodeId = this.nodeId.getAndIncrement();
		
		final Tracker tracker = new Tracker(node, sequenceNumber.get());
		
		nodes.put(nodeId, tracker);
		
		if (node instanceof Iconic) {
			((Iconic) node).addIconListener(new IconListener() {
				
				@Override
				public void iconEvent(IconEvent e) {
					String iconId = e.getIconId();
					iconRegistry.register(iconId, e.getSource());
					tracker.updateIcon(iconId, sequenceNumber.incrementAndGet());
				}
			});
		}
		
		if (node instanceof Structural) {
			((Structural) node).addStructuralListener(new StructuralListener() {
				
				@Override
				public void childRemoved(StructuralEvent event) {
					int childId = tracker.removeChild(event.getIndex(), sequenceNumber.incrementAndGet());
					nodes.remove(childId);
				}
				
				@Override
				public void childAdded(StructuralEvent event) {
					int childNode = track(event.getChild());
					tracker.addChild(event.getIndex(), childNode, sequenceNumber.incrementAndGet());
				}
			});
		}
		return nodeId;
	}
	
	public NodeInfos infoFor(long fromSequence, int... nodeIds) {
		
		
		long lastSequence = sequenceNumber.get();
		
		List<NodeInfo> nodeInfoList = new ArrayList<>();
		for (int i = 0; i < nodeIds.length; ++i) {
			int nodeId = nodeIds[i];
			
			Tracker tracker = nodes.get(nodeId);
			
			if (tracker == null) {
				throw new IllegalArgumentException("Unknown Id" + nodeId);
			}
			
			NodeInfo nodeInfo = tracker.infoFor(nodeId, fromSequence);
			if (nodeInfo != null) {
				nodeInfoList.add(nodeInfo);
			}
		}
		NodeInfo[] info = nodeInfoList.toArray(
				new NodeInfo[nodeInfoList.size()]);
		
		return new NodeInfos(lastSequence, info);
	}
	
	public byte[] iconImageFor(String iconId) {
		
		return iconRegistry.retrieve(iconId);
	}

	static class Tracker {
		
		private final Object node;
		
		private long nameSequence;
				
		private long iconSequence;
		
		private String icon;
		
		private long childrenSequence;
		
		private final List<Integer> children = new ArrayList<>();
		
		public Tracker(Object node, long sequence) {
			this.node = node;
			this.nameSequence = sequence;
		}
		
		synchronized void updateIcon(String icon, long sequence) {
			this.icon = icon;
			this.iconSequence = sequence;
		}
		
		
		synchronized int removeChild(int index, long sequence) {
			this.childrenSequence = sequence;
			return children.remove(index);
		}
		
		synchronized void addChild(int index, int nodeId, long sequence) {
			this.childrenSequence = sequence;
			this.children.add(index, nodeId);
		}
		
		synchronized NodeInfo infoFor(int nodeId, long fromSequence) {
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
			if (fromSequence < childrenSequence) {
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
	}
	
}
