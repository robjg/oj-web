/**
 * 
 */

var ojTreeModelFactory = function(ojTreeDao) {
	"use strict";
	
	var nodeDataById = {};
	
	var lastSeq = -1;
	
	var pendingLeastSeq;
	
	var selectedNodeId;
	
	var selectionListeners = [];
	
	var changeListeners = [];
	
	function fireSelectionChanged(fromNodeId, toNodeId) {
		var event = {
			fromNodeId: fromNodeId,
			toNodeId: toNodeId
		};
		
		for (var i = 0; i < selectionListeners.length; ++i) {
			var callback = selectionListeners[i].selectionChanged;
			if (callback !== undefined) {
				callback(event);
			}
		}
	}
	
	function fireTreeInitialised(node) {
		var event = {
			rootNode: node
		};
		
		for (var i = 0; i < changeListeners.length; ++i) {
			var callback = changeListeners[i].treeInitialised;
			if (callback !== undefined) {
				callback(event);
			}
		}
	}
	
	function fireNodeInserted(parentId, index, node) {
		var event = {
			parentId: parentId,
			index: index,
			node: node
		};
		
		for (var i = 0; i < changeListeners.length; ++i) {
			var callback = changeListeners[i].nodeInserted(event);
			if (callback !== undefined) {
				callback(event);
			}
		}
	}
	
	function fireNodeRemoved(nodeId) {
		var event = {
			nodeId: nodeId
		};
		
		for (var i = 0; i < changeListeners.length; ++i) {
			var callback = changeListeners[i].nodeRemoved(event);
			if (callback !== undefined) {
				callback(event);
			}
		}
	}
	
	function fireNodeExpanded(parentId, nodeArray) {
		var event = {
			parentId: parentId,
			nodeList: nodeArray
		};
		
		for (var i = 0; i < changeListeners.length; ++i) {
			var callback = changeListeners[i].nodeExpanded;
			if (callback !== undefined) {
				callback(event);
			}
		}
	}
	
	function fireNodeCollapsed(parentId) {
		var event = {
			parentId: parentId
		};
		
		for (var i = 0; i < changeListeners.length; ++i) {
			var callback = changeListeners[i].nodeCollapsed(event);
			if (callback !== undefined) {
				callback(event);
			}
		}
	}
	
	function fireNodeUpdated(node) {
		var event = {
			node: node
		};
		
		for (var i = 0; i < changeListeners.length; ++i) {
			var callback = changeListeners[i].nodeUpdated(event);
			if (callback !== undefined) {
				callback(event);
			}
		}
	}
	
	function compareNodeList(nodes1, nodes2, callbacks) {
								
		var lastI = 0, insertPoint = 0;
		
		for (var j = 0; j < nodes2.length; ++j) {

			var found = false;
			
			for (var i = lastI; i < nodes1.length; ++i) {
			
				if (nodes2[j] == nodes1[i]) {
				
					for (; lastI < i; ++lastI) {
						callbacks.deleted(nodes1[lastI], insertPoint);
					}
					++lastI;
					++insertPoint;
					found = true;
					break;
				}
			}
			
			if (!found) {
				callbacks.inserted(nodes2[j], insertPoint++);
			}
		}			

		for (var i = lastI; i < nodes1.length; ++i) {
			callbacks.deleted(nodes1[i], insertPoint);
		}
	}

	function nodeDataFor(nodeId) {
		
		var nodeData = nodeDataById[nodeId];
		if (nodeData === undefined) {
			throw "No node data for node id [" + nodeId + "]"
		}
		return nodeData;
	}
	
	function whenNodeDataFor(nodeId, then) {
		
		var nodeData = nodeDataById[nodeId];
		
		if (nodeData !== undefined) {
			return then(nodeData);
		}
	}
	
	function updateNodeStateExpanded(nodeId, expanded) {
		
		var nodeData = nodeDataFor(nodeId);
		nodeData.expanded = expanded;
	}
	
	function createNodeState(nodeOrArray, pending) {

		if (pending === undefined) {
			pending = false;
		}
		
		if (nodeOrArray.constructor === Array) {
			for (var i = 0; i < nodeOrArray.length; ++i) {
				createNodeState(nodeOrArray[i], pending);
			}
		}
		else {
			var node = nodeOrArray;
			var nodeData = {
				expanded: false,
				pending: pending,
				node: node
			};
			
			nodeDataById[node.nodeId] = nodeData;
		}
	}
	
	function updateNodeState(update) {

		var nodeData = nodeDataFor(update.nodeId);	
		var existing = nodeData.node;
		
		if (update.children) {
			existing.children = update.children;
		}
		if (update.name) {
			existing.name = update.name;
		}
		if (update.icon) {
			existing.icon = update.icon;
		}
	}
	
	function childrenRequest(intArray) {
		
		var childrenStr = "";
		for (var i = 0; i < intArray.length; ++i) {
			if (i > 0) {
				childrenStr = childrenStr + ",";
			}
			childrenStr = childrenStr + intArray[i];
		}
		return childrenStr;
	}

	function updatePendingSeq(eventSeq) {
		
		if (pendingLeastSeq === undefined) {
			pendingLeastSeq = eventSeq
		}
		else if (eventSeq < pendingLeastSeq) {
			pendingLeastSeq = eventSeq;
		}
	}
	
	function rootNodeCallback(data) {
		
		var rootNode = data.nodeInfo[0];
		
		fireTreeInitialised(rootNode);
		
		createNodeState(rootNode, false);
		
		lastSeq = data.eventSeq;
	}
	
	function provideExpandCallback(parentId) {
		
		return function(data) {
			
			var nodeArray = data.nodeInfo
			
			fireNodeExpanded(parentId, nodeArray);
			
			updateNodeStateExpanded(parentId, true);
			
			createNodeState(nodeArray, true);
			updatePendingSeq(data.eventSeq);
		}; 
	}	
	
	function recursiveCollapse(nodeId) {
		
		whenNodeDataFor(nodeId, function(nodeData) {
			
			if (!nodeData.expanded) {
				return;
			}
			
			var node = nodeData.node;

			var childNodeIds = node.children;
				
			for (var i = 0; i < childNodeIds.length; ++i) {
				var childNodeId = childNodeIds[i];
				recursiveCollapse(childNodeId);
				
				delete nodeDataById[childNodeId];
			}
			
			fireNodeCollapsed(nodeId);
			
			nodeData.expanded = false;
		});
	}
	
	function insertNode(parentId, index, node) {

		createNodeState(node, true);
		
		fireNodeInserted(parentId, index, node);
	}
	
	function provideInsertedNodesCallback(childThings) {
		
		return function(data) {
			
			var nodeInfo = data.nodeInfo;
			
			var j = 0;
			
			var nodeActions = childThings.nodeActions;
			
			for (var i = 0; i < nodeActions.length; ++i) {
		
				j = j + nodeActions[i](nodeInfo[j]);
			}
			
			updatePendingSeq(data.latEventSeq);
		}
	}
	
	function updateNode(node, childThings) {
		
		var nodeData = nodeDataFor(node.nodeId);	

		var newChildren = node.children;
		
		if (nodeData.expanded && newChildren !== undefined) {

			var oldChildren = nodeData.node.children;
			
			var index = 0;
			
			compareNodeList(oldChildren, newChildren, {
				inserted: function(nodeId, index) {
					childThings.insertedNodeIds.push(nodeId);
					childThings.nodeActions.push(function (childNode) {
						insertNode(node.nodeId, index, childNode);
						return 1;
					});
				},
				deleted: function(nodeId, index) {
					childThings.nodeActions.push(function(childNode) {
						recursiveCollapse(nodeId);
						delete nodeDataById[nodeId];
						fireNodeRemoved(nodeId);
						return 0;
					});
				}
			});
		}
		
		fireNodeUpdated(node);
		updateNodeState(node);
	}

	function pollCallback(data) {
	
		var childThings = {
				nodeActions: [],
				insertedNodeIds: []
		};
		
		var nodeInfo = data.nodeInfo;
		
		for (var i = 0; i < nodeInfo.length; ++i) {
	
			var node = nodeInfo[i];
			 
			updateNode(node, childThings);
		}
		
		if (pendingLeastSeq === undefined) {
			lastSeq = data.eventSeq;
		}
		else {
			for (var property in nodeDataById) {
				if (nodeDataById.hasOwnProperty(property)) {
					nodeDataById[property].pending = false;
		        }
		    }		
			lastSeq = pendingLeastSeq;
			pendingLeastSeq = undefined;
		}
		
		if (childThings.insertedNodeIds.length > 0) {
			
			ojTreeDao.makeNodeInfoRequest(
					childrenRequest(childThings.insertedNodeIds), 
					provideInsertedNodesCallback(childThings), -1);
		}
		else {
			var nodeActions = childThings.nodeActions;
			for (var i = 0; i < nodeActions.length; ++i) {
				nodeActions[i]();
			}
		}
	}	


	return {

		init: function() {
			
			ojTreeDao.makeNodeInfoRequest('0', rootNodeCallback, -1);
		},
		
		expandNode: function(nodeId) {
			
			var nodeData = whenNodeDataFor(nodeId, function(nodeData) {
				
				var node = nodeData.node;
				var childNodes = childrenRequest(node.children);
				
				ojTreeDao.makeNodeInfoRequest(childNodes, 
						provideExpandCallback(nodeId), -1);
			})
		},
		
		collapseNode: function(nodeId) {
			
			recursiveCollapse(nodeId);
		},
		
		poll: function() {
			
			var nonePendingNodeIds = [];
			
			for (var property in nodeDataById) {
				if (nodeDataById.hasOwnProperty(property)) {
					var nodeData = nodeDataById[property];
					if (nodeData.pending === false) {
						nonePendingNodeIds.push(nodeData.node.nodeId);
					}
		        }
		    }		
				
			ojTreeDao.makeNodeInfoRequest(childrenRequest(nonePendingNodeIds), 
					pollCallback, lastSeq);
		},
		
		select: function(nodeId) {
			
			if (nodeId !== selectedNodeId) {
				fireSelectionChanged(selectedNodeId, nodeId)
				selectedNodeId = nodeId;
			}
		},		
		
		addSelectionListener: function(listener) {
			selectionListeners.push(listener);
		},
		
		addTreeChangeListener: function(listener) {
			changeListeners.push(listener);
		},
	};
};
