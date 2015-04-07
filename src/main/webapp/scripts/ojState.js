
function ojStateFactory(ojStateDao, tabName, divId) {

	if (tabName === undefined) {
        tabName = "state"
	}

    if (divId === undefined) {
        divId = "oj-detail-tabs";
    }

    var selector = '#' + divId + " ." + tabName + "-content";

	var tabSelected;
	
	var selectedNodeId;
	
	function setStateInTable(state) {
		
		var container$ = $(selector);

        container$.find(".state-state-cell").text(state.state);
        container$.find(".state-time-cell").text(new Date(state.time));
        container$.find(".state-exception-cell").text(state.exception);
	}
	
	function removeAll() {
		
		var container$ = $(selector);

        container$.find(".state-state-cell").empty();
        container$.find(".state-time-cell").empty();
        container$.find(".state-exception-cell").empty();
	}
	
	function stateCallback(data) {
		
		if (data.nodeId !== selectedNodeId) {
			return;
		}
		
		setStateInTable(data);
	}
	
	function fetchState() {
		
		if (tabSelected !== tabName) {
			return;
		}
		
		if (selectedNodeId === undefined) {
			return;
		}
		
		ojStateDao.fetchState(selectedNodeId, stateCallback);
	}
	
	return {
		
		selectionChanged: function(event) {
			removeAll();
			selectedNodeId = event.toNodeId;
			fetchState();
		},
	
		tabChanged: function(event) {
			tabSelected = event.newTab;
			fetchState();
		},
		
		poll: function() {
			fetchState();
		}
	};	
}