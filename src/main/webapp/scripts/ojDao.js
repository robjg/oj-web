
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

        executeAction: function(actionName, nodeId) {
            $.get('api/action/' +  nodeId +  '/' + actionName);
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