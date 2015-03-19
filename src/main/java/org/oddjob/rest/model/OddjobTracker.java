package org.oddjob.rest.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.oddjob.Iconic;
import org.oddjob.Structural;
import org.oddjob.images.IconEvent;
import org.oddjob.images.IconListener;
import org.oddjob.logging.LogArchiver;
import org.oddjob.logging.LogEvent;
import org.oddjob.logging.LogLevel;
import org.oddjob.logging.LogListener;
import org.oddjob.structural.StructuralEvent;
import org.oddjob.structural.StructuralListener;

/**
 * Track changes in an Oddjob tree and provide {@link NodeInfo} records
 * based on a last sequence number.
 * <p>
 * The first event will be for sequence number 0;
 * 
 * @author rob
 *
 */
public class OddjobTracker {

	private static final Logger logger = Logger.getLogger(OddjobTracker.class);
	
	private final AtomicLong sequenceNumber = new AtomicLong();
	
	private final AtomicInteger nodeId = new AtomicInteger();
	
	private final Map<Integer, NodeTracker> nodes = new ConcurrentHashMap<>();
	
	private final IconRegistry iconRegistry = new IconRegistry();

	public int track(Object node) {
		
		return track(new NodeTracker(node, nodeId.getAndIncrement(), 
				sequenceNumber.get()));
	}
	
	/**
	 * Track changes in a node node.
	 * 
	 * @param node The job node.
	 * @return The nodeId create to reference the node by.
	 */
	int track(final NodeTracker tracker) {
		
		Object node = tracker.getNode(); 
		
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
					track(tracker.addChild(event.getIndex(), event.getChild(), 
							nodeId.getAndIncrement(), sequenceNumber.incrementAndGet()));
				}
			});
		}
		
		nodes.put(tracker.getNodeId(), tracker);
		
		return tracker.getNodeId();
	}
	
	public NodeInfos infoFor(long fromSequence, int... nodeIds) {
		
		
		long lastSequence = sequenceNumber.get();
		
		List<NodeInfo> nodeInfoList = new ArrayList<>();
		for (int i = 0; i < nodeIds.length; ++i) {
			int nodeId = nodeIds[i];
			
			NodeTracker tracker = nodes.get(nodeId);
			
			if (tracker == null) {
				logger.debug("Node Info request for unknown Id [" + nodeId + "]");
				continue;
			}
			
			NodeInfo nodeInfo = tracker.infoFor(fromSequence);
			if (nodeInfo != null) {
				nodeInfoList.add(nodeInfo);
			}
		}
		NodeInfo[] info = nodeInfoList.toArray(
				new NodeInfo[nodeInfoList.size()]);
		
		return new NodeInfos(lastSequence, info);
	}
	
	public byte[] iconImageFor(String iconId) {
		
		byte[] image = iconRegistry.retrieve(iconId);
		if (image == null) {
			logger.debug("Icon request for unknown Id [" + iconId + "]");
		}
		return image;
	}
	
	public Object nodeFor(int nodeId) {
		
		NodeTracker tracker = nodes.get(nodeId);
		
		if (tracker == null) {
			return null;
		}
		
		return tracker.getNode();
	}
	
	public LogLines logLinesFor(int nodeId, long logSeq) {
		
		NodeTracker tracker = nodes.get(nodeId);
		
		if (tracker == null) {
			return null;
		}
		
		class LL implements LogListener {
			List<LogEvent> list = 
				new ArrayList<LogEvent>();
			
			public void logEvent(LogEvent logEvent) {
				list.add(logEvent);
			}
		}
		
		LL ll = new LL(); 
		
		Object node = tracker.getNode();
		LogArchiver logArchiver = tracker.getLogArchiver();
		
		logArchiver.addLogListener(ll, node, LogLevel.DEBUG, -1, 1000);
		logArchiver.removeLogListener(ll, node);
		
		LogLine[] lines = new LogLine[ll.list.size()];
		int i = 0;
		for (LogEvent logEvent : ll.list) {
			lines[i++] = new LogLine(logEvent.getNumber(), 
					logEvent.getLevel().toString(), logEvent.getMessage());
		}
		
		return new LogLines(tracker.getNodeId(), lines);
	}
	
}
