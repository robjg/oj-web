/**
 *
 */



ojMainFactory = function(idPrefix, factories) {
	
	var initialised = false;
	
	var intervalRef;
	
	var lastInterval;

	var pollController = {

        stopRefresh: function() {
		
            if (intervalRef !== undefined) {
                clearInterval(intervalRef);
                intervalRef = undefined;

                return function() {

                    main.start(lastInterval);
                }
            }
            else {
                return function() {

                }
            }
        }
    };

    function doPoll() {

        ojTreeModel.poll();
        ojLog.poll();
        ojConsole.poll();
    }

    var ojDao = new OjDaoImpl();

    var ojTreeModel = new OjTreeModel(ojDao);

    var ojTreeController = new OjTreeController(ojTreeModel, pollController);

	var ojTreeUI = new OjTreeUI(ojTreeController, ojDao, idPrefix);
	
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

    var main = {

        start: function(interval) {
            if (!initialised) {
                ojTreeController.init();
                initialised = true;
            }
            if (interval === undefined) {
                interval = 5;
            }

            pollController.stopRefresh();

            if (interval > 0) {
                intervalRef = setInterval(this.poll, interval * 1000);
            }

            lastInterval = interval;
        },

        stop: function() {
            start(0);
        },

        poll: function() {
            if (!initialised) {
                throw "ojMain not initialised.";
            }

            var resume = pollController.stopRefresh();

            doPoll();

            resume();
        },

    };

    return main;
};
