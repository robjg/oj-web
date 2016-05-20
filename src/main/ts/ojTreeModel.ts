/**
 * 
 */

interface ChangeListener {

    treeInitialised(event: {rootNode: NodeInfo}): void;

    nodeInserted(event: {parentId: number, index: number, node: NodeInfo}): void;

    nodeRemoved(event: {nodeId: number}): void;

    nodeExpanded(event: {parentId: number, nodeList: NodeInfo[]}): void;

    nodeCollapsed(event: {parentId: number}): void;

    nodeUpdated(event: {node: NodeInfo}): void;
}

interface SelectionEvent {

    fromNodeId: number;
    toNodeId: number;
}

interface SelectionListener {

    selectionChanged(event: SelectionEvent): void;
}

interface TreeModel {

    init(): void;

    expandNode(nodeId: number): void;

    collapseNode(nodeId: number): void;

    poll(): void;

    select(nodeId: number): void;

    addSelectionListener(listener: SelectionListener): void;

    addTreeChangeListener(listener: ChangeListener): void;
}

interface NodeData {
    pending: boolean;
    expanded: boolean;
    node: NodeInfo;
}

class OjTreeModel implements TreeModel {

	private nodeDataById: {
        [nodeId: number] : NodeData;
	} = {};
	
	lastSeq: number = -1;
	
	pendingLeastSeq: number;
	
	selectedNodeId: number;
	
	selectionListeners: SelectionListener[] = [];
	
	changeListeners: ChangeListener[] = [];

    ojTreeDao: TreeDao;

    constructor(ojTreeDao: TreeDao) {
        this.ojTreeDao = ojTreeDao;
    }
	
	private fireSelectionChanged(fromNodeId: number, toNodeId: number): void {
		var event = {
			fromNodeId: fromNodeId,
			toNodeId: toNodeId
		};
		
		for (var i = 0; i < this.selectionListeners.length; ++i) {
			var callback = this.selectionListeners[i].selectionChanged;
			if (callback !== undefined) {
				callback(event);
			}
		}
	}

    private fireTreeInitialised(node: NodeInfo): void {
		var event = {
			rootNode: node
		};
		
		for (var i = 0; i < this.changeListeners.length; ++i) {
			var callback = this.changeListeners[i].treeInitialised;
			if (callback !== undefined) {
				callback(event);
			}
		}
	}

    private fireNodeInserted(parentId: number, index: number, node: NodeInfo): void {
		var event = {
			parentId: parentId,
			index: index,
			node: node
		};
		
		for (var i = 0; i < this.changeListeners.length; ++i) {
			var callback = this.changeListeners[i].nodeInserted;
			if (callback !== undefined) {
				callback(event);
			}
		}
	}

    private fireNodeRemoved(nodeId: number): void {
		var event = {
			nodeId: nodeId
		};
		
		for (var i = 0; i < this.changeListeners.length; ++i) {
			var callback = this.changeListeners[i].nodeRemoved;
			if (callback !== undefined) {
				callback(event);
			}
		}
	}

    private fireNodeExpanded(parentId: number, nodeArray: NodeInfo[]): void {
		var event = {
			parentId: parentId,
			nodeList: nodeArray
		};
		
		for (var i = 0; i < this.changeListeners.length; ++i) {
			var callback = this.changeListeners[i].nodeExpanded;
			if (callback !== undefined) {
				callback(event);
			}
		}
	}

    private fireNodeCollapsed(parentId: number): void {
		var event = {
			parentId: parentId
		};
		
		for (var i = 0; i < this.changeListeners.length; ++i) {
			var callback = this.changeListeners[i].nodeCollapsed;
			if (callback !== undefined) {
				callback(event);
			}
		}
	}

    private fireNodeUpdated(node: NodeInfo): void {
		var event = {
			node: node
		};
		
		for (var i = 0; i < this.changeListeners.length; ++i) {
			var callback = this.changeListeners[i].nodeUpdated;
			if (callback !== undefined) {
				callback(event);
			}
		}
	}

    private compareNodeList(nodes1: number[], nodes2: number[],
                            callbacks: {
                                inserted: (nodeId: number, index: number) => void;
                                deleted: (nodeId: number, index: number) => void;
                            }) {
								
		var lastI = 0, insertPoint = 0;
		
		for (var j = 0; j < nodes2.length; ++j) {

			var found = false;
			
			for (var i = lastI; i < nodes1.length; ++i) {
			
				if (nodes2[j] == nodes1[i]) {
				
					for (; lastI < i; ++lastI) {
						callbacks.deleted(nodes1[lastI], insertPoint);
					}
					++lastI;
					++insertPoint;
					found = true;
					break;
				}
			}
			
			if (!found) {
				callbacks.inserted(nodes2[j], insertPoint++);
			}
		}			

		for (var i = lastI; i < nodes1.length; ++i) {
			callbacks.deleted(nodes1[i], insertPoint);
		}
	}

    private nodeDataFor(nodeId: number): NodeData {
		
		let nodeData: NodeData = this.nodeDataById[nodeId];
		if (nodeData === undefined) {
			throw "No node data for node id [" + nodeId + "]"
		}
		return nodeData;
	}

    private whenNodeDataFor = (nodeId: number, then: (nodeData: NodeData) => any): any => {
		
		let nodeData: NodeData = this.nodeDataById[nodeId];
		
		if (nodeData !== undefined) {
			return then(nodeData);
		}
	}

    private updateNodeStateExpanded(nodeId: number, expanded: boolean): void {
		
		let nodeData: NodeData = this.nodeDataFor(nodeId);
		nodeData.expanded = expanded;
	}

    private createNodeStates(nodes: NodeInfo[], pending: boolean = false): void {

        for (var i = 0; i < nodes.length; ++i) {
            this.createNodeState(nodes[i], pending);
        }
    }

    private createNodeState(node: NodeInfo, pending: boolean = false): void {

        var nodeData = {
            expanded: false,
            pending: pending,
            node: node
        };

        this.nodeDataById[node.nodeId] = nodeData;
	}

    private updateNodeState(update: { nodeId: number, children: number[], name: string, icon: string }) {

		var nodeData = this.nodeDataFor(update.nodeId);
		var existing = nodeData.node;
		
		if (update.children) {
			existing.children = update.children;
		}
		if (update.name) {
			existing.name = update.name;
		}
		if (update.icon) {
			existing.icon = update.icon;
		}
	}

    private childrenRequest(intArray: number[]): string {
		
		let childrenStr: string = "";
		for (var i = 0; i < intArray.length; ++i) {
			if (i > 0) {
				childrenStr = childrenStr + ",";
			}
			childrenStr = childrenStr + intArray[i];
		}
		return childrenStr;
	}

    private updatePendingSeq(eventSeq: number): void {
		
		if (this.pendingLeastSeq === undefined) {
			this.pendingLeastSeq = eventSeq
		}
		else if (eventSeq < this.pendingLeastSeq) {
			this.pendingLeastSeq = eventSeq;
		}
	}

    private rootNodeCallback = (data: MakeNodeInfoRequestData): void => {
		
		let rootNode: NodeInfo = data.nodeInfo[0];
		
		this.fireTreeInitialised(rootNode);
		
		this.createNodeState(rootNode, false);
		
		this.lastSeq = data.eventSeq;
	}

    private provideExpandCallback = (parentId: number): (data: MakeNodeInfoRequestData) => void => {
		
		return (data: MakeNodeInfoRequestData): void => {
			
			let nodeArray: NodeInfo[] = data.nodeInfo
			
			this.fireNodeExpanded(parentId, nodeArray);
			
			this.updateNodeStateExpanded(parentId, true);
			
			this.createNodeStates(nodeArray, true);
			this.updatePendingSeq(data.eventSeq);
		}; 
	}

    private recursiveCollapse(nodeId: number): void {
		
		this.whenNodeDataFor(nodeId,
            (nodeData: NodeData): void => {
			
			if (!nodeData.expanded) {
				return;
			}
			
			let node: NodeInfo = nodeData.node;

			let childNodeIds: number[] = node.children;
				
			for (var i = 0; i < childNodeIds.length; ++i) {
				let childNodeId: number = childNodeIds[i];
				this.recursiveCollapse(childNodeId);
				
				delete this.nodeDataById[childNodeId];
			}
			
			this.fireNodeCollapsed(nodeId);
			
			nodeData.expanded = false;
		});
	}

    private insertNode(parentId: number, index: number, node: NodeInfo): void {

		this.createNodeState(node, true);
		
		this.fireNodeInserted(parentId, index, node);
	}

    private provideInsertedNodesCallback = (childThings: {
        nodeActions: ((nodeInfo: NodeInfo) => number)[];
    }): (data: MakeNodeInfoRequestData) => void => {
		
		return (data: MakeNodeInfoRequestData): void => {
			
			let nodeInfo: NodeInfo[] = data.nodeInfo;
			
			let j: number = 0;
			
			let nodeActions: ((nodeInfo: NodeInfo) => number)[]
                = childThings.nodeActions;
			
			for (var i = 0; i < nodeActions.length; ++i) {
		
				j = j + nodeActions[i](nodeInfo[j]);
			}
			
			this.updatePendingSeq(data.eventSeq);
		}
	}

    private updateNode(node: NodeInfo,
                       childThings: {
                           insertedNodeIds: number[];
                           nodeActions: ((nodeInfo: NodeInfo) => number)[];
                       }) {

        let self = this;

		let nodeData: NodeData = this.nodeDataFor(node.nodeId);

		let newChildren: number[] = node.children;
		
		if (nodeData.expanded && newChildren !== undefined) {

			var oldChildren = nodeData.node.children;
			
			var index = 0;

			this.compareNodeList(oldChildren, newChildren, {
				inserted: (nodeId: number, index: number): void => {
					childThings.insertedNodeIds.push(nodeId);
					childThings.nodeActions.push((childNode: NodeInfo): number => {
						self.insertNode(node.nodeId, index, childNode);
						return 1;
					});
				},
				deleted: function(nodeId, index) {
					childThings.nodeActions.push((childNode: NodeInfo): number => {
						self.recursiveCollapse(nodeId);
						delete self.nodeDataById[nodeId];
						self.fireNodeRemoved(nodeId);
						return 0;
					});
				}
			});
		}
		
		this.fireNodeUpdated(node);
		this.updateNodeState(node);
	}

    private pollCallback = (data: MakeNodeInfoRequestData): void => {
	
		var childThings = {
				nodeActions: [],
				insertedNodeIds: []
		};
		
		let nodeInfo: NodeInfo[] = data.nodeInfo;
		
		for (var i = 0; i < nodeInfo.length; ++i) {
	
			let node: NodeInfo = nodeInfo[i];
			 
			this.updateNode(node, childThings);
		}
		
		if (this.pendingLeastSeq === undefined) {
			this.lastSeq = data.eventSeq;
		}
		else {
			for (var property in this.nodeDataById) {
				if (this.nodeDataById.hasOwnProperty(property)) {
					this.nodeDataById[property].pending = false;
		        }
		    }		
			this.lastSeq = this.pendingLeastSeq;
			this.pendingLeastSeq = undefined;
		}
		
		if (childThings.insertedNodeIds.length > 0) {
			
			this.ojTreeDao.makeNodeInfoRequest(
					this.childrenRequest(childThings.insertedNodeIds),
					this.provideInsertedNodesCallback(childThings), -1);
		}
		else {
			var nodeActions = childThings.nodeActions;
			for (var i = 0; i < nodeActions.length; ++i) {
				nodeActions[i]();
			}
		}
	}	


    init(): void {

        this.ojTreeDao.makeNodeInfoRequest('0', this.rootNodeCallback, -1);
    }
		
    expandNode(nodeId: number): void {

        var nodeData = this.whenNodeDataFor(nodeId, (nodeData: NodeData): void => {

            let node: NodeInfo = nodeData.node;
            var childNodes = this.childrenRequest(node.children);

            this.ojTreeDao.makeNodeInfoRequest(childNodes,
                    this.provideExpandCallback(nodeId), -1);
        })
    }
		
    collapseNode(nodeId: number): void {

        this.recursiveCollapse(nodeId);
    }

    poll(): void {

        var nonePendingNodeIds = [];

        for (var property in this.nodeDataById) {
            if (this.nodeDataById.hasOwnProperty(property)) {
                var nodeData = this.nodeDataById[property];
                if (nodeData.pending === false) {
                    nonePendingNodeIds.push(nodeData.node.nodeId);
                }
            }
        }

        this.ojTreeDao.makeNodeInfoRequest(this.childrenRequest(nonePendingNodeIds),
                this.pollCallback, this.lastSeq);
    }

    select(nodeId: number): void {

        if (nodeId !== this.selectedNodeId) {
            this.fireSelectionChanged(this.selectedNodeId, nodeId)
            this.selectedNodeId = nodeId;
        }
    }

    addSelectionListener(listener: SelectionListener): void {
        this.selectionListeners.push(listener);
    }

    addTreeChangeListener(listener: ChangeListener): void {
        this.changeListeners.push(listener);
    }
}
