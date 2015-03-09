

ojTreeController = function(idPrefix) {
	
	var initialised = false;
	
	var intervalRef;
	
	var lastInterval;
	
	function stopRefresh() {
		
		if (intervalRef !== undefined) {
			clearInterval(intervalRef);
			intervalRef = undefined;
			
			return function() {
				
				ojTreeController.start(lastInterval);
			}
		}
		else {
			return function() {
				
			}
		}
	}
	
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
			
			stopRefresh();
			
			if (interval > 0) {
				intervalRef = setInterval(ojTreeController.poll, interval * 1000);
			}
			
			lastInterval = interval;
		},
		
		stop: function() {
			ojTreeController.start(0);
		},
		
		poll: function() {
			if (!initialised) {
				throw "ojTreeController not initialised.";
			}
			
			var resume = stopRefresh();
			
			ojTreeModel.poll();
			
			resume();
		},
		
		expandNode: function(nodeId) {
			if (!initialised) {
				throw "ojTreeController not initialised.";
			}
			
			var resume = stopRefresh();
			
			ojTreeModel.poll();
			
			ojTreeModel.expandNode(nodeId);
			
			resume();
		},
	
		collapseNode: function(nodeId) {
			if (!initialised) {
				throw "ojTreeController not initialised.";
			}
			
			var resume = stopRefresh();
			
			ojTreeModel.collapseNode(nodeId);
			
			ojTreeModel.poll();
			
			resume();
		},
		
		select: function(nodeId) {
			
			if (!initialised) {
				throw "ojTreeController not initialised.";
			}
			
			var resume = stopRefresh();
			
			ojTreeModel.poll();
			
			ojTreeModel.select(nodeId);
			
			resume();
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
