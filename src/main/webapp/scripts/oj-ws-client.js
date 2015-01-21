/**
 * 
 */

function addNode(node) {

	$("#0000").append("<p>Node: " + node.name + " " + node.icon + " " + node.children + " </p>");
	
	
}

function ajaxCallback(data) {
	
//	var obj = $.parseJSON(resonse);
	
	$("#0000").append("<p>Received: " + data.eventSeq + "</p>");
	
	children = data.nodeInfo;
	
	addNode(children[0]);
}

function makeNodeInfoRequest() {
	
	$.get('ojws/nodeInfo', 'nodeIds=0&eventSeq=-1',
			ajaxCallback);
}

function buttonPress() {
	makeNodeInfoRequest();
}

$(document).ready(function() {
	$("button").click(buttonPress);
});
