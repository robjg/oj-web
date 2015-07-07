
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
		}
	}
}();


var actionsExecuted = [];

var testActionsDao = function() {
	
	return {

		actionsFor: function(nodeId, callback) {
			
			callback([
			    { actionType: "SIMPLE",
                    name: "start",
			        displayName: "Start" },
			    { actionType: "SIMPLE",
                    name: "stop",
			        displayName: "Stop" },
			    { actionType: "SIMPLE",
                    name: "force",
			        displayName: "Force" },
                { actionType: "FORM",
                    name: "execute",
                    displayName: "Execute" }
			    ]);
		},
		
		executeAction: function(nodeId, actionName, callback) {
			actionsExecuted.push({
				actionName: actionName,
				nodeId: nodeId });

            callback({
               status: 'OK'
            });
		},

        dialogFor: function(nodeId, actionName, callback) {

            callback({ dialogType: "FORM",
                fields: [
                    { fieldType: "TEXT", label: "Favourite Fruit",  name: "favourite.fruit", value: "Apples" },
                    { fieldType: "PASSWORD", label: "A Secret", name: "some.secret" }
                ]});
        },

        formAction: function(nodeId, actionName, form$, callback) {

            actionsExecuted.push({
                actionName: actionName,
                nodeId: nodeId,
                form: form$.serialize()
            });

            callback({
                status: 'OK'
            });

        }
	};
}();

