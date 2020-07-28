package org.oddjob.rest.model;

import org.oddjob.Iconic;
import org.oddjob.Stateful;
import org.oddjob.Structural;
import org.oddjob.arooa.logging.LogLevel;
import org.oddjob.arooa.registry.BeanDirectory;
import org.oddjob.describe.Describer;
import org.oddjob.jmx.RemoteIdMappings;
import org.oddjob.logging.ConsoleArchiver;
import org.oddjob.logging.LogArchiver;
import org.oddjob.logging.LogEvent;
import org.oddjob.logging.LogListener;
import org.oddjob.state.State;
import org.oddjob.structural.StructuralEvent;
import org.oddjob.structural.StructuralListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An implementation of {@link OddjobTracker} that uses a local node ids.
 *
 * @author rob
 *
 */
public class OddjobTrackerLocal implements OddjobTracker {

	private static final Logger logger = LoggerFactory.getLogger(OddjobTrackerLocal.class);

	private final BeanDirectory beanDirectory;

	private final Describer describer;

	private final AtomicLong sequenceNumber = new AtomicLong();

	private final ConcurrentMap<Integer, NodeTracker> nodes = new ConcurrentHashMap<>();

	private final NodeIds nodeIds;

	private final IconRegistry iconRegistry = new IconRegistry();

	public OddjobTrackerLocal(BeanDirectory beanDirectory, Describer describer) {
		this(null, beanDirectory, describer);
	}

	public OddjobTrackerLocal(RemoteIdMappings remoteIdMappings, BeanDirectory beanDirectory, Describer describer) {

		this.beanDirectory = Objects.requireNonNull(beanDirectory);
		this.describer = Objects.requireNonNull(describer);
		this.nodeIds = Optional.ofNullable(remoteIdMappings)
				.<NodeIds>map(NodeIdsFromMappings::new)
				.orElseGet(NodeIdsContained::new);
	}
	
	public ComponentSummary[] nodeIdFor(String... componentPaths) {

		List<ComponentSummary> results = new ArrayList<>();
		
		for (String path : componentPaths) {
			Object maybeComponent = beanDirectory.lookup(path);
			
			if (maybeComponent == null) {
				continue;
			}
			
			Integer nodeId = nodeIds.getIdFor(maybeComponent);
			
			if (nodeId == null) {
				continue;
			}
			
			State state = null;
			if (maybeComponent instanceof Stateful) {
				state = ((Stateful) maybeComponent).lastStateEvent().getState();
			}
			
			results.add(new ComponentSummary(nodeId,
					maybeComponent.toString(),
					path,
					Optional.ofNullable(state)
							.map(State::toString)
							.orElse(null)));
		}

		return results.toArray(new ComponentSummary[0]);
	}
	
	public int track(Object node) {

		int newNodeId = nodeIds.newNodeIdFor(node);

		return track(new NodeTracker(node, newNodeId,
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
			((Iconic) node).addIconListener(e -> {
				String iconId = e.getIconId();
				iconRegistry.register(iconId, e.getSource());
				tracker.updateIcon(iconId, sequenceNumber.incrementAndGet());
			});
		}
		
		if (node instanceof Structural) {
			((Structural) node).addStructuralListener(new StructuralListener() {
				
				@Override
				public void childRemoved(StructuralEvent event) {
					int childId = tracker.removeChild(event.getIndex(), sequenceNumber.incrementAndGet());
					NodeTracker nodeTracker = nodes.remove(childId);
					nodeIds.remove(nodeTracker.getNode());
				}
				
				@Override
				public void childAdded(StructuralEvent event) {
					Object child = event.getChild();
					int newNodeId = nodeIds.newNodeIdFor(child);
					track(tracker.addChild(event.getIndex(), child,
							newNodeId, sequenceNumber.incrementAndGet()));
				}
			});
		}

		nodes.put(tracker.getNodeId(), tracker);

		return tracker.getNodeId();
	}
	
	public NodeInfos infoFor(long fromSequence, int... nodeIds) {
		
		
		long lastSequence = sequenceNumber.get();
		
		List<NodeInfo> nodeInfoList = new ArrayList<>();
		for (int nodeId : nodeIds) {
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
				new NodeInfo[0]);
		
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
			final List<LogEvent> list =
					new ArrayList<>();
			
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
			final List<LogEvent> list =
					new ArrayList<>();
			
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

	interface NodeIds {


		Integer getIdFor(Object maybeComponent);

		int newNodeIdFor(Object node);

		void remove(Object node);
	}

	static class NodeIdsContained implements NodeIds {

		private final AtomicInteger nodeId = new AtomicInteger();

		private final ConcurrentMap<Object, Integer> componentsToNodIds = new ConcurrentHashMap<>();

		@Override
		public Integer getIdFor(Object maybeComponent) {
			return componentsToNodIds.get(maybeComponent);
		}

		@Override
		public int newNodeIdFor(Object node) {
			int newNodeId = nodeId.getAndIncrement();
			componentsToNodIds.put(node, newNodeId);
			return newNodeId;
		}

		@Override
		public void remove(Object node) {
			componentsToNodIds.remove(node);
		}
	}

	static class NodeIdsFromMappings implements NodeIds {

		private final RemoteIdMappings idMappings;

		NodeIdsFromMappings(RemoteIdMappings idMappings) {
			this.idMappings = idMappings;
		}

		@Override
		public Integer getIdFor(Object maybeComponent) {
			long id = idMappings.idFor(maybeComponent);
			if (id < 0) {
				return null;
			}
			else {
				return (int) id;
			}
		}

		@Override
		public int newNodeIdFor(Object node) {
			long id = idMappings.idFor(node);
			if (id < 0) {
				throw new IllegalStateException("No id for " + node);
			}
			return (int) id;
		}

		@Override
		public void remove(Object node) {
		}
	}
}
