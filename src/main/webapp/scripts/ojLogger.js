

function ojLogger(ojLoggerDao, divId) {

	if (divId === undefined) {
		divId = "oj-detail-tabs";
	}
	
	var tabSelected;
	
	var selectedNodeId;
	
	var lastLogSeq = -1;
	
	function selector() {
		return '#' + divId + " .log-content";
	}
	
	function addLogLines(lines) {
		
		var container$ = $(selector());
		
		for (var i = 0; i < lines.length; ++i) {
			container$.append(lines[i].message);
			lastLogSeq = lines[i].logSeq
		}

        var height = container$[0].scrollHeight;
        container$.scrollTop(height);
	}
	
	function removeAll() {
		
		var container$ = $(selector());
		
		container$.empty();
	}
	
	function logLinesCallback(data) {
		
		if (data.nodeId !== selectedNodeId) {
			return;
		}
		
		var lines = data.logLines;
		
		addLogLines(lines);
	}
	
	function fetchLogLines() {
		
		if (tabSelected !== "log") {
			return;
		}
		
		if (selectedNodeId === undefined) {
			return;
		}
		
		ojLoggerDao.fetchLogLines(selectedNodeId, lastLogSeq, logLinesCallback);
	}
	
	return {
		
		selectionChanged: function(event) {
			removeAll();
			selectedNodeId = event.toNodeId;
            lastLogSeq = -1;
			fetchLogLines();
		},
	
		tabChanged: function(event) {
			tabSelected = event.newTab;
			fetchLogLines();
		},
		
		poll: function() {
			fetchLogLines();
		}
	};	
}