package org.oddjob.rest.model;

/**
 * Track changes in an Oddjob tree.
 * 
 * @author rob
 *
 */
public interface OddjobTracker {

	/**
	 * Information for paths.
	 *
	 * @param componentPaths
	 * @return
	 */
	ComponentSummary[] nodeIdFor(String... componentPaths);

	/**
	 * Provide {@link NodeInfo} records based on a last sequence number.
	 * The first event will be for sequence number 0
	 *
	 * @param fromSequence
	 * @param nodeIds
	 * @return
	 */
	NodeInfos infoFor(long fromSequence, int... nodeIds);

	/**
	 * Get an icon.
	 *
	 * @param iconId
	 * @return
	 */
	byte[] iconImageFor(String iconId);

	/**
	 * Get the node for an Id.
	 *
	 * @param nodeId
	 * @return
	 */
	Object nodeFor(int nodeId);

	/**
	 * Get the State for an id.
	 *
	 * @param nodeId
	 * @return
	 */
	StateDTO stateFor(int nodeId);

	/**
	 * Get the log lines.
	 *
	 * @param nodeId
	 * @param logSeq
	 * @return
	 */
	LogLines logLinesFor(int nodeId, long logSeq);

	/**
	 * Get the console lines.
	 *
	 * @param nodeId
	 * @param logSeq
	 * @return
	 */
	LogLines consoleLinesFor(int nodeId, long logSeq);

	/**
	 * Get the properties.
	 *
	 * @param nodeId
	 * @return
	 */
	PropertiesDTO propertiesFor(int nodeId);
}
