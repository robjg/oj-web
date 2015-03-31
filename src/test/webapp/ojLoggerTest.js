
var fetchLogLines_argCapture = [];

var logLinesCallIndex = 0;

var ojLoggerDao = function() {
	
	var logLinesData = [
	    {
	    	nodeId: 457,
	    	logLines: [
	    	    { logSeq: 1000, level: "INFO", message: "First Line.\n" },
	    	    { logSeq: 1001, level: "INFO", message: "Second Line.\n" }
	    	]
	    },
        {
            nodeId: 457,
            logLines: [
                { logSeq: 1005, level: "INFO", message: "Third Line.\n" },
            ]
        },
        {
            nodeId: 457,
            logLines: [
                { logSeq: 1006, level: "INFO", message: "Fourth Line.\n" },
            ]
        },
        {
            nodeId: 22,
            logLines: [
                { logSeq: 100, level: "INFO", message: "New Logger.\n" },
            ]
        }
	];
	
	return {
		
		fetchLogLines: function(nodeId, logSeq, callback) {

			fetchLogLines_argCapture.push( {
				nodeId: nodeId,
				logSeq: logSeq
			});
			
			callback(logLinesData[logLinesCallIndex++]);
		}
	};
	
}();
