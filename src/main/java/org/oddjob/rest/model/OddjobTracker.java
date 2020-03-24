package org.oddjob.rest.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.describe.Describer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.Iconic;
import org.oddjob.Stateful;
import org.oddjob.Structural;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.logging.LogLevel;
import org.oddjob.describe.UniversalDescriber;
import org.oddjob.images.IconEvent;
import org.oddjob.images.IconListener;
import org.oddjob.logging.ConsoleArchiver;
import org.oddjob.logging.LogArchiver;
import org.oddjob.logging.LogEvent;
import org.oddjob.logging.LogListener;
import org.oddjob.state.State;
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

	private static final Logger logger = LoggerFactory.getLogger(OddjobTracker.class);
	
	private final BeanDirectory beanDirectory;

	private final Describer describer;

	private final AtomicLong sequenceNumber = new AtomicLong();
	
	private final AtomicInteger nodeId = new AtomicInteger();
	
	private final ConcurrentMap<Integer, NodeTracker> nodes = new ConcurrentHashMap<>();

	private final ConcurrentMap<Object, Integer> componentsToNodIds = new ConcurrentHashMap<>();
	
	private final IconRegistry iconRegistry = new IconRegistry();

	public OddjobTracker(BeanDirectory beanDirectory, Describer describer) {

		this.beanDirectory = Objects.requireNonNull(beanDirectory);
		this.describer = Objects.requireNonNull(describer);
	}
	
	public ComponentSummary[] nodeIdFor(String... componentPaths) {
		
		List<ComponentSummary> results = new ArrayList<>();
		
		for (String path : componentPaths) {
			Object maybeComponent = beanDirectory.lookup(path);
			
			if (maybeComponent == null) {
				continue;
			}
			
			Integer nodeId = componentsToNodIds.get(maybeComponent);
			
			if (nodeId == null) {
				continue;
			}
			
			State state = null;
			if (maybeComponent instanceof Stateful) {
				state = ((Stateful) maybeComponent).lastStateEvent().getState();
			}
			
			results.add(new ComponentSummary(nodeId.intValue(), 
					maybeComponent.toString(), path, state.toString()));
		}

		return results.toArray(new ComponentSummary[results.size()]);
	}
	
	public int track(Object node) {
		
		return track(new NodeTracker(node, nodeId.getAndIncrement(), 
				sequenceNumber.get()));
	}
	
	/**
	 * Track changes in a node node.
	 * 
	 * @param tracker The job node.
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
					NodeTracker nodeTracker = nodes.remove(childId);
					componentsToNodIds.remove(nodeTracker.getNode());
				}
				
				@Override
				public void childAdded(StructuralEvent event) {
					track(tracker.addChild(event.getIndex(), event.getChild(), 
							nodeId.getAndIncrement(), sequenceNumber.incrementAndGet()));
				}
			});
		}
		
		nodes.put(tracker.getNodeId(), tracker);
		componentsToNodIds.put(tracker.getNode(), tracker.getNodeId());
		
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
	
	public StateDTO stateFor(int nodeId) {
		
		Object node = nodeFor(nodeId);

		if (node == null) {
			return null;
		}
				
		if (!(node instanceof Stateful)) {
			return null;
		}

		return new StateDTO(nodeId,
				((Stateful) node).lastStateEvent());
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
		
		logArchiver.addLogListener(ll, node, LogLevel.DEBUG, logSeq, 1000);
		logArchiver.removeLogListener(ll, node);
		
		LogLine[] lines = new LogLine[ll.list.size()];
		int i = 0;
		for (LogEvent logEvent : ll.list) {
			lines[i++] = new LogLine(logEvent.getNumber(), 
					logEvent.getLevel().toString(), logEvent.getMessage());
		}
		
		return new LogLines(tracker.getNodeId(), lines);
	}
	
	public LogLines consoleLinesFor(int nodeId, long logSeq) {
		
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
		ConsoleArchiver consoleArchiver = tracker.getConsoleArchiver();
		
		consoleArchiver.addConsoleListener(ll, node, logSeq, 1000);
		consoleArchiver.removeConsoleListener(ll, node);
		
		LogLine[] lines = new LogLine[ll.list.size()];
		int i = 0;
		for (LogEvent logEvent : ll.list) {
			lines[i++] = new LogLine(logEvent.getNumber(), 
					logEvent.getLevel().toString(), logEvent.getMessage());
		}
		
		return new LogLines(tracker.getNodeId(), lines);
	}	
	
	public PropertiesDTO propertiesFor(int nodeId) {
		Object node = nodeFor(nodeId);
		
		if (node == null) {
			return null;
		}
		
		Map<String, String> properties = describer.describe(node);
		
		return new PropertiesDTO(nodeId, properties);
	}
}
