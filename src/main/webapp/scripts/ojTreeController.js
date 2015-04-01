

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

    function doPoll() {

        ojTreeModel.poll();
        ojLog.poll();
        ojConsole.poll();
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
			
            doPoll();

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
	
	var ojTreeDao = {
			
			makeNodeInfoRequest: function(nodeIds, ajaxCallback, eventSeq) {
			
			$.get('api/nodeInfo', 'nodeIds=' + nodeIds + '&eventSeq=' + eventSeq,
					ajaxCallback);
		}
	};
	
	var ojTreeModel = ojTreeModelFactory(ojTreeDao);
	ojTreeModel.addTreeChangeListener(ojTreeUI);
	ojTreeModel.addSelectionListener(ojTreeUI);
	
	var ojActionsDao = {
			
		actionsFor: function(nodeId, ajaxCallback) {
			
			$.get('api/actionsFor/' + nodeId,
					ajaxCallback);
		},
		
		executeAction: function(actionName, nodeId) {
			$.get('api/action/' +  nodeId +  '/' + actionName);
		}
	};
	
	var ojActions = ojJobActions(ojActionsDao);
	ojTreeModel.addSelectionListener(ojActions);

    var tabsModel = ojTabsModel();
    var tabsUI = ojDetailTabs(tabsModel);
    tabsModel.addTabSelectionListener(tabsUI);

    var ojConsoleDao = {

        fetchLogLines: function(nodeId, logSeq, ajaxCallback) {

        $.get('api/consoleLines/' + nodeId, 'logSeq=' + logSeq,
            ajaxCallback);
        }
    };

    var ojConsole = ojLogger(ojConsoleDao, 'console');

    tabsModel.addTabSelectionListener(ojConsole);
    ojTreeModel.addSelectionListener(ojConsole);

    var ojLoggerDao = {

        fetchLogLines: function(nodeId, logSeq, ajaxCallback) {

            $.get('api/logLines/' + nodeId, 'logSeq=' + logSeq,
                ajaxCallback);
        }
    };

    var ojLog = ojLogger(ojLoggerDao);

    tabsModel.addTabSelectionListener(ojLog);
    ojTreeModel.addSelectionListener(ojLog);

    return ojTreeController;
};
