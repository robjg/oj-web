/**
 *
 */
ojTreeControllerFactory = function(ojTreeModel, ojPollController) {

	if (ojPollController === undefined) {
        ojPollController = {

            stopRefresh: function() {}
        };
    }

	var initialised = false;

	return {
		
		init: function() {
			if (initialised) {
				throw "ojTreeController already initialised.";
			}
			ojTreeModel.init();
			initialised = true;
		},
		
		expandNode: function(nodeId) {
			if (!initialised) {
				throw "ojTreeController not initialised.";
			}
			
			var resume = ojPollController.stopRefresh();
			
			ojTreeModel.poll();
			
			ojTreeModel.expandNode(nodeId);

            resume();
		},
	
		collapseNode: function(nodeId) {
			if (!initialised) {
				throw "ojTreeController not initialised.";
			}
			
			var resume = ojPollController.stopRefresh();
			
			ojTreeModel.collapseNode(nodeId);
			
			ojTreeModel.poll();

            resume();
		},
		
		select: function(nodeId) {
			
			if (!initialised) {
				throw "ojTreeController not initialised.";
			}
			
			var resume = ojPollController.stopRefresh();
			
			ojTreeModel.poll();
			
			ojTreeModel.select(nodeId);

            resume();
		}
	};
}
