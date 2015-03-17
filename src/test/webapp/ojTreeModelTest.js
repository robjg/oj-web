var results = [];

var makeNodeInfoRequest_argCapture = [];


var ojTreeDao = function() {

	var makeNodeInfoRequest_data = [
	    // Test1: init	                               
	    { eventSeq: 457, nodeInfo : [
           	{ "nodeId" : 0, "name" : "Some Jobs", "icon" : "executing", "children" : [12,13] },
       	] },
 	    // Test2: first poll
	    { eventSeq: 457, nodeInfo : [
   	    ] },
       	// Test3: expand
	    { eventSeq: 572, nodeInfo : [
 	        { "nodeId" : 12, "name" : "Jobs One", "icon" : "executing", "children" : [78,79,80] },
 	        { "nodeId" : 13, "name" : "Jobs Two", "icon" : "ready" }
 	    ] },
 	    // Test4: second poll
	    { eventSeq: 587, nodeInfo : [
   	    ] },
 	    // Test5: third poll 
	    { eventSeq: 612, nodeInfo : [
  	        { "nodeId" : 12, "icon" : "ready", "children" : []},
  	        { "nodeId" : 13, "icon" : "executing", "children" : [81, 82]}
  	    ] },
 	    // Test6: fourth poll
	    { eventSeq: 724, nodeInfo : [
  	        { "nodeId" : 0, "children" : [20,12,21,22] },
  	    ] },
  	    // and callback for new nodes
	    { eventSeq: 572, nodeInfo : [
  	        { "nodeId" : 20, "name" : "Now First", "icon" : "executing" },
  	        { "nodeId" : 21, "name" : "Now Third", "icon" : "ready" },
  	        { "nodeId" : 22, "name" : "Now Fourth", "icon" : "ready" }
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

var rootNode_argCapture = [];

var ojTreeUI_capture = [];

var ojTreeUI = function() {
	
	return {
		treeInitialised: function(event) {
			
			var capture = {
					func: 'treeInitialised',
					event: event
				};
				
			ojTreeUI_capture.push(capture);
		},
		
		nodeInserted: function(event) {
			
			var capture = {
					func: 'nodeInserted',
					event: event
			};
				
			ojTreeUI_capture.push(capture);
		},
		
 		nodeRemoved: function(event) {

			var capture = {
					func: 'nodeRemoved',
					event: event
			};
				
			ojTreeUI_capture.push(capture);
		},
		
		nodeExpanded: function(event) {
			
			var capture = {
					func: 'nodeExpanded',
					event: event
			};
				
			ojTreeUI_capture.push(capture);
		},
		
		nodeCollapsed: function(event) {

			var capture = {
					func: 'nodeCollapsed',
					event: event
			};
				
			ojTreeUI_capture.push(capture);
		},
		
		nodeUpdated: function(event) {
			
			var capture = {
					func: 'nodeUpdated',
					event: event
			};
				
			ojTreeUI_capture.push(capture);
		}

	};
}();

