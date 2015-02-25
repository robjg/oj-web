

ojTreeController = function(idPrefix) {
	
	var initialised = false;
	
	var intervalRef;
	
	var ojTreeController = {
		
		init: function() {
			if (initialised) {
				throw "ojTreeController already initialised.";
			}
			ojTreeModel.init();
			initialised = true;
		},
		
		start: function(interval) {
			if (!initialised) {
				ojTreeController.init();
			}
			if (interval === undefined) {
				interval = 5000;
			}
			if (intervalRef !== undefined) {
				ojTreeController.stop();
			}
			intervalRef = setInterval(ojTreeController.poll, interval);
		},
		
		stop: function() {
			if (intervalRef !== undefined) {
				clearInterval(intervalRef);
				intervalRef = undefined;
			}
		},
		
		poll: function() {
			if (!initialised) {
				throw "ojTreeController not initialised.";
			}
			ojTreeModel.poll();
		},
		
		expandNode: function(nodeId) {
			if (!initialised) {
				throw "ojTreeController not initialised.";
			}
			ojTreeModel.expandNode(nodeId);
		},
	
		collapseNode: function(nodeId) {
			if (!initialised) {
				throw "ojTreeController not initialised.";
			}
			ojTreeModel.collapseNode(nodeId);
		}
	};
	
	var ojTreeUI = ojTreeUIFactory(ojTreeController, idPrefix);
	
	var ojTreeModel = ojTreeModelFactory(ojTreeUI, 
		{
			
				makeNodeInfoRequest: function(nodeIds, ajaxCallback, eventSeq) {
				
				$.get('ojws/nodeInfo', 'nodeIds=' + nodeIds + '&eventSeq=' + eventSeq,
						ajaxCallback);
			}
		});
	
	return ojTreeController;
};
