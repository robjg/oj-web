/**
 * Creates The Oddjob Tree UI Component. This component is a Selection Listener and a
 * Tree Change Listener and must be added as a listener to a Model to receive the
 * structure to display.
 *
 *
 * @param {Object} ojTreeController
 * @param {Function} [ojTreeController.select] - function called when a user selects node.
 * @param {Function} ojTreeController.expandNode - function called when user expands node.
 * @param {Function} ojTTreeController.collapseNode - function called when user collapses node.
 * @param {string} [idPrefix=ojNode]
 *
 * @returns {{
 *     treeInitialised: function,
 *     nodeInserted: function,
 *     nodeRemoved: function,
 *     nodeExpanded: function,
 *     nodeCollapsed: function,
 *     nodeUpdated: function,
 *     selectionChanged: function
 *     }}
 *       A facade onto the Trees User Interface.
 */
var ojTreeUIFactory = function(ojTreeController, iconProvider, idPrefix) {
	"use strict";

	if (idPrefix === undefined) {
		idPrefix = 'ojNode';
	}
	
	var label;
	
	if (ojTreeController.select === undefined) {
		label = function(node) {
			return node.name;			
		}
	}
	else {
		label = function(node) {
			return $('<a>' + node.name + '</a>').click(function() {
				ojTreeController.select(node.nodeId);
			});
		}
	}
	
	function expandImage(nodeId) {
		return $('<img>').attr(
				{ class: 'toggle',
				  src: 'gfx/plus.png',
				  alt: 'expand'
				}
			).click(function() {
				return ojTreeController.expandNode(nodeId);
			}
		);
	}
	
	function collapseImage(nodeId) {
		return $('<img>').attr(
				{ class: 'toggle',
				  src: 'gfx/minus.png', 
				  alt: 'collapse' 
				}
			).click(function() {
				return ojTreeController.collapseNode(nodeId)
			}
		);
	}
	
	function iconImage(iconName) {
		
		return $('<img>').attr(
				{ class: 'icon',
				  src: iconProvider.iconSrcUrl(iconName),
				  alt: iconName 
				}
		);
	}

	function changeToggleImageToExpand(li$, nodeId) {
		
		li$.find('>img.toggle').replaceWith(
				expandImage(nodeId));
	}
	
	function changeToggleImageToCollapse(li$, nodeId) {
		
		var img$ = li$.find('>img.toggle');
		
		if (img$.length === 0) {
			li$.prepend(expandImage(nodeId));
		}
		else {
			img$.replaceWith(
					collapseImage(nodeId));
		}
	}
	
	function removeToggleImage(li$) {
		
		li$.children('img.toggle').remove();
	}
	
	function changeIconImage(li$, iconName) {
		
		li$.children('img.icon').replaceWith(
				iconImage(iconName));
	}
	
	function nodeIdSelector(nodeId) {
		return '#' + idPrefix  + nodeId;
	}
	
	function htmlForNode(node) {
		
		var li$ = $('<li>').attr('id', 'ojNode' + node.nodeId);
		
		if (node.children !== undefined && node.children.length > 0) {
			li$.append(expandImage(node.nodeId));
		}
		
		if (node.icon !== undefined) {
			li$.append(iconImage(node.icon));
		}
		
		li$.append(label(node));
		
	    li$.append('<ul>');
	    
	    return li$;
	}	
	
	function insertChildFirst(parent$, node) {
		
		parent$.find('>ul').prepend(htmlForNode(node));
	}

	function insertChild(parent$, index, node) {
		
	    parent$.find('>ul>li:nth-child('+ index +')'
				).after(htmlForNode(node));
	}
	
	function appendChild(parent$, node) {
		
		parent$.find('>ul'
				).append(htmlForNode(node));
	}

	return {
		
		treeInitialised: function(event) {
			
			var node = event.rootNode;
			
			insertChildFirst($(nodeIdSelector('Root')), node);
		},
		
		/*
		 * Create the DOM for a node and make it a child of the
		 * given parent at the given index.
		 */
		nodeInserted: function(event) {
			
			var parentId = event.parentId;
			var index = event.index;
			var node = event.node;
			
			var parent$ = $(nodeIdSelector(parentId));
			
			if (parent$.children('ul li').length == 0) {
				changeToggleImageToCollapse(parent$, parentId)
			}
			
			if(index === 0) {
				insertChildFirst(parent$, node);        
			}
			else {
				insertChild(parent$, index, node);
			}

		},
		
 		nodeRemoved: function(event) {

 			var nodeId = event.nodeId;
 			
 			var node$ = $(nodeIdSelector(nodeId));
 			
 			var parent$ = node$.parent('ul').parent('li');
 			
			node$.remove();	
			
			if (parent$.find('>ul>li').length == 0) {
				removeToggleImage(parent$)
			}
		},
		
		nodeExpanded: function(event) {
			
			var parentId = event.parentId;
			var nodeList = event.nodeList;
			
			var parent$ = $(nodeIdSelector(parentId));
			
			for (var i = 0; i < nodeList.length; ++i) {
				appendChild(parent$, nodeList[i]);
			}
			
			changeToggleImageToCollapse(parent$, parentId)
		},
		
		nodeCollapsed: function(event) {

			var parentId = event.parentId;
			
			var parent$ = $(nodeIdSelector(parentId));

			parent$.find('>ul>li').remove();
			
			changeToggleImageToExpand(parent$, parentId);
		},
		
		nodeUpdated: function(event) {
			
			var node = event.node;
			
			var li$ = $(nodeIdSelector(node.nodeId));
			
			if (node.children !== undefined) {
				
				if (node.children.length > 0) {
					if (li$.find('ul>li').length === 0) {
						li$.prepend(expandImage(node.nodeId));
					}
				}
				else {
					removeToggleImage(li$);
				}
			}
			
			if (node.icon !== undefined) {
				changeIconImage(li$, node.icon);
			}
			
			// Todo: Change text.
		},
		
		selectionChanged: function(event) {
			var fromNodeId = event.fromNodeId;
			var toNodeId = event.toNodeId;
			
			if (fromNodeId !== undefined) {
				$(nodeIdSelector(fromNodeId) + ">a").removeAttr(
				'class');
			}
			
			if (toNodeId !== undefined) {
				$(nodeIdSelector(toNodeId) + ">a").attr(
						'class', 'selected');
			}
		}
		
	};
};
