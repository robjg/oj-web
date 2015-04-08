
var fetchProperties_argCapture = [];

var propertiesCallIndex = 0;

var ojPropertiesDao = function() {
	
	var propertiesData = [
	    {
	    	nodeId: 457,
	    	properties: { 'favourite.fruit': 'apple', 'favourite.colour': null }
	    },
        {
            nodeId: 457,
            properties: { 'favourite.fruit': 'orange', 'favourite.colour': 'red'}
        },
        {
            nodeId: 457,
            properties: { 'favourite.fruit': null, 'favourite.colour': 'green'}
        },
        {
            nodeId: 22,
            properties: { 'city': 'London', 'age': '23'}
        }
	];
	
	return {
		
		fetchProperties: function(nodeId, callback) {

			fetchProperties_argCapture.push( {
				nodeId: nodeId
			});
			
			callback(propertiesData[propertiesCallIndex++]);
		}
	};
	
}();
