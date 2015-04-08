
function ojPropertiesFactory(ojPropertiesDao, tabName, divId) {

	if (tabName === undefined) {
        tabName = "properties"
	}

    if (divId === undefined) {
        divId = "oj-detail-tabs";
    }

    var selector = '#' + divId + " ." + tabName + "-content";

	var tabSelected;
	
	var selectedNodeId;
	
	function setPropertiesInTable(properties) {
		
		var container$ = $(selector);

        for (var property in properties) {
            if (properties.hasOwnProperty(property)) {
                container$.append("<tr><td>" + property + 
                    "</td><td>" + properties[property] + "</td></tr>");
            }
        }
	}
	
	function removeAll() {
		
		var container$ = $(selector);

        container$.empty();
	}
	
	function propertiesCallback(data) {
		
		if (data.nodeId !== selectedNodeId) {
			return;
		}
		
		setPropertiesInTable(data.properties);
	}
	
	function fetchProperties() {
		
		if (tabSelected !== tabName) {
			return;
		}
		
		if (selectedNodeId === undefined) {
			return;
		}
		
		ojPropertiesDao.fetchProperties(selectedNodeId, propertiesCallback);
	}
	
	return {
		
		selectionChanged: function(event) {
			removeAll();
			selectedNodeId = event.toNodeId;
			fetchProperties();
		},
	
		tabChanged: function(event) {
			tabSelected = event.newTab;
			fetchProperties();
		},
		
		poll: function() {
			fetchProperties();
		}
	};	
}