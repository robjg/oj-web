
var fetchState_argCapture = [];

var stateCallIndex = 0;

var ojStateDao = function() {
	
	var stateData = [
	    {
	    	nodeId: 457,
	    	state: "READY",
            time: 60
	    },
        {
            nodeId: 457,
            state: "EXCEPTION",
            time: 120,
            exception: "An Exception\nWith Stack Trace"
        },
        {
            nodeId: 457,
            state: "COMPLETE",
            time: 180
        },
        {
            nodeId: 22,
            state: "INCOMPLETE",
            time: 60
        }
	];
	
	return {
		
		fetchState: function(nodeId, callback) {

			fetchState_argCapture.push( {
				nodeId: nodeId
			});
			
			callback(stateData[stateCallIndex++]);
		}
	};
	
}();
