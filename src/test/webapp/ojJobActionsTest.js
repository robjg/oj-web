
var testTreeModel = function() {
	
	var listner;
	
	return {
		
		addSelectionListener: function(l) {
			listener = l;
		},
		
		fireSelectionChanged: function(from, to) {
			listener.selectionChanged(
					{fromNodeId: from,
					 toNodeId: to});
		},
	}
}();


var actionsExecuted = [];

var testActionsDao = function() {
	
	return {

		actionsFor: function(nodeId, callback) {
			
			callback([
			    { name: "run",
			      displayName: "Run" }, 
			    { name: "stop",
			      displayName: "Stop" }, 
			    { name: "force",
			      displayName: "Force" }
			    ]);
		},
		
		executeAction: function(actionName, nodeId) {
			actionsExecuted.push({
				actionName: actionName,
				nodeId: nodeId });
		}
	};
}();
