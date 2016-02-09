

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
	
	var ojTreeModel = ojTreeModelFactory(ojDao);
	ojTreeModel.addTreeChangeListener(ojTreeUI);
	ojTreeModel.addSelectionListener(ojTreeUI);

    var ojForm = ojFormFactory();

	var ojActions = ojJobActions(ojDao, ojForm);
	ojTreeModel.addSelectionListener(ojActions);

    var tabsModel = ojTabsModel("state");
    var tabsUI = ojDetailTabs(tabsModel);
    tabsModel.addTabSelectionListener(tabsUI);

    var ojState = ojStateFactory(ojDao, { selected: true });

    tabsModel.addTabSelectionListener(ojState);
    ojTreeModel.addSelectionListener(ojState);

    var ojConsole = ojLogger({
        fetchLogLines: function(nodeId, logSeq, ajaxCallback) {
            ojDao.fetchConsoleLines(nodeId, logSeq, ajaxCallback);
        }
    }, 'console');

    tabsModel.addTabSelectionListener(ojConsole);
    ojTreeModel.addSelectionListener(ojConsole);

    var ojLog = ojLogger(ojDao);

    tabsModel.addTabSelectionListener(ojLog);
    ojTreeModel.addSelectionListener(ojLog);

    var ojProperties = ojPropertiesFactory(ojDao);

    tabsModel.addTabSelectionListener(ojProperties);
    ojTreeModel.addSelectionListener(ojProperties);

    return ojTreeController;
};
