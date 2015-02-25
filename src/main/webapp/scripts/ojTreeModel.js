/**
 * 
 */

var ojTreeModelFactory = function(ojTreeUI, ojTreeDao) {
	"use strict";
	
	var nodeDataById = {};
	
	var lastSeq = -1;
	
	var pendingNodesById = {};
	
	var pendingLeastSeq;
	
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
	
	function updateNodeStateExpanded(nodeId, expanded) {
		
		var nodeData = nodeDataFor(nodeId);
		nodeData.expanded = expanded;
	}
	
	function createNodeState(nodeOrArray) {

		if (nodeOrArray.constructor === Array) {
			for (var i = 0; i < nodeOrArray.length; ++i) {
				createNodeState(nodeOrArray[i]);
			}
		}
		else {
			var nodeData = {};
			nodeData.expanded = false;
			nodeData.node = nodeOrArray;
			
			nodeDataById[nodeOrArray.nodeId] = nodeData;
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
		
		ojTreeUI.rootNode(rootNode);
		
		createNodeState(rootNode);
		
		lastSeq = data.eventSeq;
	}
	
	function provideExpandCallback(parentNodeId) {
		
		return function(data) {
			
			var nodeArray = data.nodeInfo
			
			ojTreeUI.expandNode(parentNodeId, nodeArray);
			
			updateNodeStateExpanded(parentNodeId, true);
			
			pendingNodesById[parentNodeId] = nodeArray;
			updatePendingSeq(data.eventSeq);
		}; 
	}	
	
	function recursiveCollapse(nodeId) {
		
		var nodeData = nodeDataById[nodeId]
		
		if (nodeData == undefined) {
			// still pending
			return;
		}
		
		if (!nodeData.expanded) {
			return;
		}
		
		var node = nodeData.node;

		var childNodeIds = node.children;
			
		for (var i = 0; i < childNodeIds.length; ++i) {
			var childNodeId = childNodeIds[i];
			recursiveCollapse(childNodeId);
			
			delete nodeDataById[childNodeId];
			delete pendingNodesById[childNodeId];
		}
		
		ojTreeUI.collapseNode(nodeId);
		
		nodeData.expanded = false;
	}
	
	function insertNode(parentNodeId, index, node) {

		var nodeArray = pendingNodesById[parentNodeId];
		if (nodeArray === undefined) {
			nodeArray = [node.nodeId];
			pendingNodesById[parentNodeId] = nodeArray;
		}
		else {
			nodeArray.push(node.nodeId);
		}
		
		ojTreeUI.insertChild(parentNodeId, index, node);
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

		if (nodeData.expanded) {

			var children = node.children;
			
			var oldChildren = nodeData.node.children;
			
			var index = 0;
			
			compareNodeList(oldChildren, children, {
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
						ojTreeUI.removeNode(node.nodeId, index);
						return 0;
					});
				}
			});
		}
		
		ojTreeUI.updateNode(node);
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
			for (var property in pendingNodesById) {
				if (pendingNodesById.hasOwnProperty(property)) {
		            createNodeState(pendingNodesById[property]);
		        }
		    }		
			pendingNodesById = {};
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
			
			var nodeData = nodeDataFor(nodeId);
			var node = nodeData.node;
			var childNodes = childrenRequest(node.children);
			ojTreeDao.makeNodeInfoRequest(childNodes, 
					provideExpandCallback(nodeId), -1);
		},
		
		collapseNode: function(nodeId) {
			
			recursiveCollapse(nodeId);
		},
		
		poll: function() {
			
			var nodeIds = Object.keys(nodeDataById);
			
			ojTreeDao.makeNodeInfoRequest(childrenRequest(nodeIds), 
					pollCallback, lastSeq);
		}
		
	};
};
