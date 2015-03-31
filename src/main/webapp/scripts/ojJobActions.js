
var ojJobActions = function(ojActionsDao, divId) {
	
	if (divId === undefined) {
		divId = 'ojJobActions';
	}
	
	function htmlForAction(action, nodeId) {
		
		return $('<button>').attr(
				{ class: action.name +'_action' }
			).click(function() {
				return ojActionsDao.executeAction(
						action.name, nodeId);
			}).append(action.displayName);
	}	
	
	function createActionButtons(actionList, nodeId) {
		
		var actionsDiv$ = $('#' + divId);
		
		for (var i = 0; i < actionList.length; ++i) {
			actionsDiv$.append(htmlForAction(actionList[i], nodeId));
		}
	}
	
	function removeActionButtons() {
		
		$('#' + divId).empty();
	}
	
	function actionsChanged(event) {
		var actionList = event.actionList;
		var nodeId = event.nodeId;
		
		if (actionList !== undefined) {
			createActionButtons(actionList, nodeId);
		}
	}
	
	function actionsCallback(nodeId) {
		
		return function(data) {
			actionsChanged({ 
				nodeId: nodeId,
				actionList: data });
		};
	}
	
	return {
		selectionChanged: function(event) {
			
			var to = event.toNodeId;
			
			removeActionButtons();
			
			if (to !== undefined) {
				ojActionsDao.actionsFor(to, actionsCallback(to));
			}
		}
	};	
};