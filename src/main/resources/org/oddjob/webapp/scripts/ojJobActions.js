/**
 * Create action buttons.
 *
 * @param ojActionsDao
 * @param ojForm
 * @param divId
 * @returns {{selectionChanged: selectionChanged}} A selection listener.
 */
var ojJobActions = function(ojActionsDao, ojForm, divId) {
	
	if (divId === undefined) {
		divId = 'ojJobActions';
	}

    function statusCallback(data) {

        // todo
    }

	function htmlForAction(nodeId, action) {

        var clickFunction;

        if (action.actionType === 'FORM') {
            clickFunction = function() {
                actionDialog(nodeId, action.name);
            }
        }
        else {
            clickFunction = function() {
                return ojActionsDao.executeAction(
                    nodeId, action.name, statusCallback);
            }
        }

		return $('<button>').attr(
				{ class: action.name +'_action' }
			).click(clickFunction
			).append(action.displayName);
	}	
	
	function createActionButtons(nodeId, actionList) {
		
		var actionsDiv$ = $('#' + divId);
		
		for (var i = 0; i < actionList.length; ++i) {
			actionsDiv$.append(htmlForAction(nodeId, actionList[i]));
		}
	}
	
	function removeActionButtons() {
		
		$('#' + divId).empty();
	}

	function actionsChanged(event) {
		var actionList = event.actionList;
		var nodeId = event.nodeId;
		
		if (actionList !== undefined) {
			createActionButtons(nodeId, actionList);
		}
	}
	
	function actionsCallback(nodeId) {
		
		return function(data) {
			actionsChanged({ 
				nodeId: nodeId,
				actionList: data });
		};
	}

    function actionDialog(nodeId, actionName) {

        var submitForm = function(form$) {
            ojActionsDao.formAction(nodeId, actionName, form$, statusCallback);
        };

        ojActionsDao.dialogFor(nodeId, actionName, function(data) {

            ojForm.doForm(data, submitForm);
        });
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