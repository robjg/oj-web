var results = [];

var makeNodeInfoRequest_argCapture = [];


var ojTreeDao = function() {

	var makeNodeInfoRequest_data = [
	    // Test1: init	                               
	    { eventSeq: 457, nodeInfo : [
           	{ "nodeId" : 0, "name" : "Some Jobs", "icon" : "executing", "children" : [12,13] },
       	] },
       	// Test2: expand
	    { eventSeq: 572, nodeInfo : [
 	        { "nodeId" : 12, "name" : "Jobs One", "icon" : "executing", "children" : [78,79,80] },
 	        { "nodeId" : 13, "name" : "Jobs Two", "icon" : "ready", "children" : [92]  }
 	    ] },
       	// Test3: expand and
	    { eventSeq: 572, nodeInfo : [
 	        { "nodeId" : 78, "name" : "Do Stuff", "icon" : "executing" },
 	        { "nodeId" : 79, "name" : "Do More Stuff", "icon" : "ready" },
 	        { "nodeId" : 80, "name" : "And Yet More", "icon" : "ready" }
 	    ] },
 	    // Test4: collapse root
 	    // Test5: poll
	    { eventSeq: 587, nodeInfo : [
   	    ] },
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
			throw "Unexpected";
		},
		
 		removeNode: function(nodeId) {
 			throw "Unexpected";
		},
		
		expandNode: function(parentId, nodeList) {
			
			var capture = {
					func: 'expandNode',
					args: arguments
			};
				
			ojTreeUI_capture.push(capture);
		},
		
		collapseNode: function(parentId) {
			
			var capture = {
					func: 'collapseNode',
					args: arguments
			};
				
			ojTreeUI_capture.push(capture);
		},
		
		updateNode: function(node) {
 			throw "Unexpected";
		}

	};
}();

