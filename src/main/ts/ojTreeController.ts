/**
 *
 */
interface TreeController {

    expandNode(nodeId: number): void;

    collapseNode(nodeId: number): void;

    isSelectEnabled(): boolean;

    select(nodeId: number): void;
}

class OjTreeController implements TreeController {

    private ojTreeModel: TreeModel;

    private ojPollController: PollController;

    private initialised = false;

    selectEnabled = true;

    constructor(ojTreeModel: TreeModel,
            ojPollController: PollController = {

                stopRefresh: function() {
                    return function() {};
                }
            }) {

        this.ojTreeModel = ojTreeModel;
        this.ojPollController = ojPollController;
    }

    init(): void {
        if (this.initialised) {
            throw "ojTreeController already initialised.";
        }
        this.ojTreeModel.init();
        this.initialised = true;
    }

    expandNode(nodeId: number): void {
        if (!this.initialised) {
            throw "ojTreeController not initialised.";
        }

        var resume = this.ojPollController.stopRefresh();

        this.ojTreeModel.poll();

        this.ojTreeModel.expandNode(nodeId);

        resume();
    }

    collapseNode(nodeId: number): void {
        if (!this.initialised) {
            throw "ojTreeController not initialised.";
        }

        var resume = this.ojPollController.stopRefresh();

        this.ojTreeModel.collapseNode(nodeId);

        this.ojTreeModel.poll();

        resume();
    }

    isSelectEnabled(): boolean {
        return this.selectEnabled;
    }

    select(nodeId: number): void {

        if (!this.initialised) {
            throw "ojTreeController not initialised.";
        }

        var resume = this.ojPollController.stopRefresh();

        this.ojTreeModel.poll();

        this.ojTreeModel.select(nodeId);

        resume();
    }
}
