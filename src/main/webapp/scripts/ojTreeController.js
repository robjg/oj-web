

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
				interval = 5;
			}
			if (intervalRef !== undefined) {
				clearInterval(intervalRef);
				intervalRef = undefined;
			}
			if (interval > 0) {
				intervalRef = setInterval(ojTreeController.poll, interval * 1000);
			}
		},
		
		stop: function() {
			ojTreeController.start(0);
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
