var results = [];

var makeNodeInfoRequest_argCapture = [];


var ojTreeDao = function() {

	var makeNodeInfoRequest_data = [
	    // Test1: init	                               
	    { eventSeq: 457, nodeInfo : [
           	{ "nodeId" : 0, "name" : "Some Jobs", "icon" : "ready" },
       	] },
       	// Test2: poll
	    { eventSeq: 572, nodeInfo : [
 	        { "nodeId" : 0, "children" : [12, 13] },
 	    ] },
       	// Test2: expand
	    { eventSeq: 572, nodeInfo : [
 	        { "nodeId" : 12, "name" : "Jobs One", "icon" : "executing", "children" : [78,79,80] },
 	        { "nodeId" : 13, "name" : "Jobs Two", "icon" : "ready", "children" : [92]  }
 	    ] },
       	// Test3: poll
	    { eventSeq: 572, nodeInfo : [
 	        { "nodeId" : 0, "icon" : "executing" },
 	        { "nodeId" : 12, "icon" : "executing" },
 	    ] },
 	    // Test4: poll
	    { eventSeq: 572, nodeInfo : [
	        { "nodeId" : 0, "icon" : "ready" }
	    ] },
 	    // Test5: poll
	    { eventSeq: 572, nodeInfo : [
	    ] }
	];
	
	var callCount = -1;
	
	return {
      
		makeNodeInfoRequest: function(nodeIds, ajaxCallback, eventSeq) {
        
			var argCapture = {};
			argCapture.nodeIds = nodeIds;
			argCapture.eventSeq = eventSeq;
			
			++callCount;
			
			makeNodeInfoRequest_argCapture[callCount] = argCapture;
			
			ajaxCallback(makeNodeInfoRequest_data[callCount]);
			
		},
	
		getCallCount: function() {
			return callCount;
		}
	}
}();

var ojTreeUI_capture = [];

var ojTreeUI = function() {
	
	return {
		rootNode: function(node) {
			
			var capture = {
				func: 'rootNode',
				args: arguments
			};
			
			ojTreeUI_capture.push(capture);
		},
		
		insertChild: function(parentId, index, node) {
			
			var capture = {
					func: 'insertChild',
					args: arguments
				};
				
			ojTreeUI_capture.push(capture);
		},
		
 		removeNode: function(nodeId) {
 			
			var capture = {
					func: 'removeNode',
					args: arguments
				};
				
			ojTreeUI_capture.push(capture);
		},
		
		expandNode: function(parentId, nodeList) {
			
			var capture = {
					func: 'expandNode',
					args: arguments
			};
				
			ojTreeUI_capture.push(capture);
		},
		
		collapseNode: function(parentId) {
			
 			throw "Unexpected";
		},
		
		updateNode: function(node) {
			var capture = {
					func: 'updateNode',
					args: arguments
			};
				
			ojTreeUI_capture.push(capture);
		}

	};
}();

