
var ojDaoFactory = function(path) {

    if (path === undefined) {
        path = "api";
    }

    return {

        // Tree

        makeNodeInfoRequest: function(nodeIds, ajaxCallback, eventSeq) {

            $.get(path + '/nodeInfo', 'nodeIds=' + nodeIds + '&eventSeq=' + eventSeq,
                ajaxCallback);
        },

        // Todo: Test This
        iconSrcUrl: function(icon) {
            return path + '/icon/' + icon;
        },

        // Actions

        actionsFor: function(nodeId, ajaxCallback) {

            $.get(path + '/actionsFor/' + nodeId,
                ajaxCallback);
        },

        dialogFor: function(nodeId, actionName, ajaxCallback) {
            $.get(path + '/dialogFor/' +  nodeId +  '/' + actionName, ajaxCallback);
        },

        executeAction: function(nodeId, actionName, statusCallback) {
            $.get(path + '/action/' +  nodeId +  '/' + actionName, statusCallback);
        },

        formAction: function(nodeId, actionName, form$, statusCallback) {

            var url = path + '/formAction/' +  nodeId +  '/' + actionName;

            $.ajax({
                type: "POST",
                url: url,
                data: form$.serialize(), // serializes the form's elements.
                success: statusCallback
            });
        },

        // State

        fetchState: function(nodeId, ajaxCallback) {

            $.get(path + '/state/' + nodeId,
                ajaxCallback);
        },

        // Console

        fetchConsoleLines: function(nodeId, logSeq, ajaxCallback) {

            $.get(path + '/consoleLines/' + nodeId, 'logSeq=' + logSeq,
                ajaxCallback);
        },

        // Logger

        fetchLogLines: function(nodeId, logSeq, ajaxCallback) {

            $.get(path + '/logLines/' + nodeId, 'logSeq=' + logSeq,
                ajaxCallback);
        },

        fetchProperties: function(nodeId, ajaxCallback) {

            $.get(path + '/properties/' + nodeId,
                ajaxCallback);
        }
    };
};