
var ojDao = function() {

    return {

        // Tree

        makeNodeInfoRequest: function(nodeIds, ajaxCallback, eventSeq) {

            $.get('api/nodeInfo', 'nodeIds=' + nodeIds + '&eventSeq=' + eventSeq,
                ajaxCallback);
        },

        // Actions

        actionsFor: function(nodeId, ajaxCallback) {

            $.get('api/actionsFor/' + nodeId,
                ajaxCallback);
        },

        dialogFor: function(nodeId, actionName, ajaxCallback) {
            $.get('api/dialogFor/' +  nodeId +  '/' + actionName, ajaxCallback);
        },

        executeAction: function(nodeId, actionName, statusCallback) {
            $.get('api/action/' +  nodeId +  '/' + actionName, statusCallback);
        },

        formAction: function(nodeId, actionName, form$, statusCallback) {

            var url = 'api/formAction/' +  nodeId +  '/' + actionName;

            $.ajax({
                type: "POST",
                url: url,
                data: form$.serialize(), // serializes the form's elements.
                success: statusCallback
            });
        },

        // State

        fetchState: function(nodeId, ajaxCallback) {

            $.get('api/state/' + nodeId,
                ajaxCallback);
        },

        // Console

        fetchConsoleLines: function(nodeId, logSeq, ajaxCallback) {

            $.get('api/consoleLines/' + nodeId, 'logSeq=' + logSeq,
                ajaxCallback);
        },

        // Logger

        fetchLogLines: function(nodeId, logSeq, ajaxCallback) {

            $.get('api/logLines/' + nodeId, 'logSeq=' + logSeq,
                ajaxCallback);
        },

        fetchProperties: function(nodeId, ajaxCallback) {

            $.get('api/properties/' + nodeId,
                ajaxCallback);
        }
    };
}();