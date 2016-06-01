/// <reference path="../../main/ts/jquery.d.ts" />
/// <reference path="../../main/ts/ojDao.ts" />
/// <reference path="../../main/ts/ojTreeModel.ts" />
/// <reference path="../../main/ts/PollController.ts" />
/// <reference path="../../main/ts/ojTreeController.ts" />

class OjTreeControllerTest implements TreeController {

	private nodeId = 0;

    treeChangeListener: TreeChangeListener;

/**	private firstExpand() {
		
		ojUI.firstChild("0", { nodeId: 4, name: "First Child",  icon: "executing" } );
		ojUI.insertChild("4", { nodeId: 5, name: "Second Child",  icon: "ready" } );
		ojUI.firstChild("0", { nodeId: 6, name: "New First",  icon: "executing", children: [10] } );
		ojUI.insertChild("6", { nodeId: 7, name: "Inserted Child",  icon: "executing" } );
	}
	
	private firstCollapse() {
		
		ojUI.removeNode("4");
		ojUI.removeNode("5");
		ojUI.removeNode("6");
		ojUI.removeNode("7");
	}
*/

	private createNode(children?: number[]): NodeInfo {
		
		let node: NodeInfo = {
				nodeId: ++this.nodeId,
				name: 'Job Number ' + this.nodeId,
                icon: null,
                children: []
		};
		
		if (this.nodeId % 3 === 0) {
			node.icon = "ready";
		}
		else {
			node.icon = "executing";
		}
	
		if (children !== undefined) {
			node.children = children;
		}
		
		return node;
	}

    expandNode = (nodeId) => {
			let nodeList: NodeInfo[] = [
					this.createNode(), this.createNode(), this.createNode()
			];
			
			this.treeChangeListener.nodeExpanded({
				parentId: nodeId, 
				nodeList: nodeList
			});
		}
	
    collapseNode = (nodeId) => {
			this.treeChangeListener.nodeCollapsed({
				parentId: nodeId 
			});
		}


    isSelectEnabled():boolean {
        return false;
    }

    select(nodeId:number):void {
    }
}

class IconProviderTest implements IconProvider {
    iconSrcUrl(icon) {
        return 'api/icon/' + icon;
    }
}
