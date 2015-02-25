

ojTreeController = function() {
	"use strict";
	
	function firstExpand() {
		
		ojUI.firstChild("0", { nodeId: 4, name: "First Child",  icon: "executing" } );
		ojUI.insertChild("4", { nodeId: 5, name: "Second Child",  icon: "ready" } );
		ojUI.firstChild("0", { nodeId: 6, name: "New First",  icon: "executing", children: [10] } );
		ojUI.insertChild("6", { nodeId: 7, name: "Inserted Child",  icon: "executing" } );
	}
	
	function firstCollapse() {
		
		ojUI.removeNode("4");
		ojUI.removeNode("5");
		ojUI.removeNode("6");
		ojUI.removeNode("7");
	}
	
	var nodeId = 0;
	
	function createNode(children) {
		
		var node = {
				nodeId: ++nodeId,
				name: 'Job Number ' + nodeId
		};
		
		if (nodeId % 3 === 0) {
			node.icon = "ready";
		}
		else {
			node.icon = "executing";
		}
	
		if (children !== undefined) {
			node.children = children;
		}
		
		return node;
	}
	
	
	return {
		
		expandNode: function(nodeId) {
			var nodeList = [
					createNode(), createNode(), createNode()
			];
			
			ojTreeUI.expandNode(nodeId, nodeList)
		},
	
		collapseNode: function(nodeId) {
			ojTreeUI.collapseNode(nodeId);
		}
	};
	
}();