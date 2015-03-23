
var ojTabsModel = function() {
	
	var currentTab;
	
	var tabSelectionListeners = [];
	
	return {
		
		addTabSelectionListener: function(listener) {
			tabSelectionListeners.push(listener);
		},
	
		tabSelected: function(tab) {
			
			var event = {
					newTab: tab,
					oldTab: currentTab
			};
			
			for (var i = 0; i < tabSelectionListeners.length; ++i) {
				var callback = tabSelectionListeners[i].tabChanged;
				if (callback !== undefined) {
					callback(event);
				}
			}

			currentTab = tab;
		}
	};
	
}