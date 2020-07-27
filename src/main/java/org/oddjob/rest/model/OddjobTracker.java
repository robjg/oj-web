package org.oddjob.rest.model;

/**
 * Track changes in an Oddjob tree and provide {@link NodeInfo} records
 * based on a last sequence number.
 * <p>
 * The first event will be for sequence number 0;
 * 
 * @author rob
 *
 */
public interface OddjobTracker {

	ComponentSummary[] nodeIdFor(String... componentPaths);
	
	int track(Object node);

	NodeInfos infoFor(long fromSequence, int... nodeIds);

	byte[] iconImageFor(String iconId);
	
	Object nodeFor(int nodeId);
	
	StateDTO stateFor(int nodeId);
	
	LogLines logLinesFor(int nodeId, long logSeq);
	
	LogLines consoleLinesFor(int nodeId, long logSeq);
	
	PropertiesDTO propertiesFor(int nodeId);
}
