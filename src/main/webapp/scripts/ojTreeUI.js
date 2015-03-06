/**
 * 
 */

var ojTreeUIFactory = function(ojTreeController, idPrefix) {
	"use strict";

	if (idPrefix === undefined) {
		idPrefix = 'ojNode';
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
				  src: 'ojws/icon/' + iconName, 
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
		
		li$.append(node.name);
		
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
		
		rootNode: function(node) {
						
			insertChildFirst($(nodeIdSelector('Root')), node);
		},
		
		/*
		 * Create the DOM for a node and make it a child of the
		 * given parent at the given index.
		 */
		insertChild: function(parentId, index, node) {
			
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
		
 		removeNode: function(nodeId) {

 			var node$ = $(nodeIdSelector(nodeId));
 			
 			var parent$ = node$.parent('ul').parent('li');
 			
			node$.remove();	
			
			if (parent$.find('>ul>li').length == 0) {
				removeToggleImage(parent$)
			}
		},
		
		expandNode: function(parentId, nodeList) {
			
			var parent$ = $(nodeIdSelector(parentId));
			
			for (var i = 0; i < nodeList.length; ++i) {
				appendChild(parent$, nodeList[i]);
			}
			
			changeToggleImageToCollapse(parent$, parentId)
		},
		
		collapseNode: function(parentId) {

			var parent$ = $(nodeIdSelector(parentId));

			parent$.find('>ul>li').remove();
			
			changeToggleImageToExpand(parent$, parentId);
		},
		
		updateNode: function(node) {
			
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
		}
	};
};
