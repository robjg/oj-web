
var ojDetailTabs = function(selectable, tabsId) {
	
	if (tabsId === undefined) {
		tabsId = "oj-detail-tabs"
	}
		
	function tabLinkSelector(tab) {
		return('#' + tabsId + " ." + tab + " a");
	}
	
	function tabSelector(tab) {
		return('#' + tabsId + " ." + tab);
	}
	
	function changeUITab(oldTab, newTab) {
		if (oldTab !== undefined) {
			$(tabSelector(oldTab)).attr(
					{ class: oldTab + ' notSelected',
					});
		}
		
		if (newTab !== undefined) {
			$(tabSelector(newTab)).attr(
					{ class: newTab + ' selected',
					});
		}
	}
	
	$(tabLinkSelector("state")).click(function() {
		selectable.tabSelected("state");
	});
	$(tabLinkSelector("console")).click(function() {
		selectable.tabSelected("console");
	});
	$(tabLinkSelector("log")).click(function() {
		selectable.tabSelected("log");
	});
	$(tabLinkSelector("properties")).click(function() {
		selectable.tabSelected("properties");
	});
	
	return {
		tabChanged: function(event) {
				changeUITab(event.oldTab, event.newTab);
		}
	}
}
