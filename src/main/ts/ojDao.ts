
interface AjaxCallback {
    (data: any, textStatus?: string, jqXHR?: JQueryXHR) : any;
}

interface FormData {
    serialize();
}

interface NodeInfo {
    nodeId: number;
    name: string;
    icon: string;
    children: number[];
}

interface MakeNodeInfoRequestData {
    nodeInfo: NodeInfo[];
    eventSeq: number;
}

interface IconProvider {

    iconSrcUrl(icon: string): string;
}

interface TreeDao extends IconProvider {

    makeNodeInfoRequest(nodeIds: string,
                        ajaxCallback: (data: MakeNodeInfoRequestData, textStatus?: string, jqXHR?: JQueryXHR) => any,
                        eventSeq: number): void;
}

interface ActionDao {

    actionsFor(nodeId:string, ajaxCallback:AjaxCallback): void;

    dialogFor(nodeId:string, actionName:string, ajaxCallback:AjaxCallback): void;

    executeAction(nodeId:string, actionName:string, statusCallback:AjaxCallback): void;

    formAction(nodeId:string, actionName:string, form$:FormData, statusCallback:AjaxCallback): void;
}

interface StateDao {

    fetchState(nodeId: string, ajaxCallback: AjaxCallback): void;
}

interface ConsoleDao {

    fetchConsoleLines(nodeId: string, logSeq: number, ajaxCallback: AjaxCallback): void;
}

interface LoggerDao {

    fetchLogLines(nodeId: string, logSeq: number, ajaxCallback: AjaxCallback);
}

interface PropertiesDao {
    fetchProperties(nodeId: string, ajaxCallback: AjaxCallback): void;
}

class OjDaoImpl implements TreeDao, ActionDao, StateDao, ConsoleDao, LoggerDao, PropertiesDao {

    path: string;

    constructor(path?: string) {

        if (path === undefined) {
            path = "api";
        }
        this.path = path;
    }

    // Tree

    makeNodeInfoRequest(nodeIds: string, ajaxCallback: AjaxCallback, eventSeq: number): void {

        $.get(this.path + '/nodeInfo', 'nodeIds=' + nodeIds + '&eventSeq=' + eventSeq,
            ajaxCallback);
    }

    iconSrcUrl(icon: string): string {
        return this.path + '/icon/' + icon;
    }

    // Actions

    actionsFor(nodeId: string, ajaxCallback: AjaxCallback): void {

        $.get(this.path + '/actionsFor/' + nodeId,
            ajaxCallback);
    }

    dialogFor(nodeId: string, actionName: string, ajaxCallback: AjaxCallback): void {
        $.get(this.path + '/dialogFor/' +  nodeId +  '/' + actionName, ajaxCallback);
    }

    executeAction(nodeId: string, actionName: string, statusCallback: AjaxCallback): void {
        $.get(this.path + '/action/' +  nodeId +  '/' + actionName, statusCallback);
    }

    formAction(nodeId: string, actionName: string, form$: FormData, statusCallback: AjaxCallback): void {

        var url = this.path + '/formAction/' +  nodeId +  '/' + actionName;

        $.ajax({
            type: "POST",
            url: url,
            data: form$.serialize(), // serializes the form's elements.
            success: statusCallback
        });
    }

    // State

    fetchState(nodeId: string, ajaxCallback: AjaxCallback): void {

        $.get(this.path + '/state/' + nodeId,
            ajaxCallback);
    }

    // Console

    fetchConsoleLines(nodeId: string, logSeq: number, ajaxCallback: AjaxCallback): void {

        $.get(this.path + '/consoleLines/' + nodeId, 'logSeq=' + logSeq,
            ajaxCallback);
    }

    // Logger

    fetchLogLines(nodeId: string, logSeq: number, ajaxCallback: AjaxCallback) {

        $.get(this.path + '/logLines/' + nodeId, 'logSeq=' + logSeq,
            ajaxCallback);
    }

    fetchProperties(nodeId: string, ajaxCallback: AjaxCallback): void {

        $.get(this.path + '/properties/' + nodeId,
            ajaxCallback);
    }
}