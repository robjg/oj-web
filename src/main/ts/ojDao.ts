
interface AjaxCallback {
    (data: any, textStatus?: string, jqXHR?: JQueryXHR) : any;
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
                        ajaxCallback: (data: MakeNodeInfoRequestData, textStatus?: string, jqXHR?: JQueryXHR) => void,
                        eventSeq: number): void;
}


interface ActionData {

    actionType: string;
    name: string;
    displayName: string;
}

interface DialogFieldData {
    fieldType: string;
    label: string;
    name: string;
    value: string;
}

interface DialogData {
    dialogType: string;
    fields: DialogFieldData[];
}

interface ActionStatus {
    status: string;
    message: string;
}

interface ActionDao {

    actionsFor(nodeId: number,
               ajaxCallback: (data: ActionData[], textStatus?: string, jqXHR?: JQueryXHR) => any
    ): JQueryXHR;

    dialogFor(nodeId: number,
              actionName:string,
              ajaxCallback: (data: DialogData, textStatus?: string, jqXHR?: JQueryXHR) => any
    ): JQueryXHR;

    executeAction(nodeId: number,
                  actionName:string,
                  statusCallback: (data: ActionStatus, textStatus?: string, jqXHR?: JQueryXHR) => any
    ): JQueryXHR;

    formAction(nodeId: number,
               actionName:string,
               form$: JQuery,
               statusCallback: (data: ActionStatus, textStatus?: string, jqXHR?: JQueryXHR) => any
    ): JQueryXHR;
}

interface StateData {
    nodeId: number;
    state: string;
    time: number;
    exception: string;
}

interface StateDao {

    fetchState(nodeId: string,
               ajaxCallback: (data: StateData, textStatus?: string, jqXHR?: JQueryXHR) => void): void;
}

interface LogLine {
    logSeq: number;
    level: string;
    message: string;
}

interface LinesData {

    nodeId: number;
    logLines: LogLine[];

}

interface ConsoleDao {

    fetchConsoleLines(nodeId: string,
                      logSeq: number,
                      ajaxCallback: (data: LinesData, textStatus?: string, jqXHR?: JQueryXHR) => void): void;
}

interface LoggerDao {

    fetchLogLines(nodeId: string,
                  logSeq: number,
                  ajaxCallback: (data: LinesData, textStatus?: string, jqXHR?: JQueryXHR) => void): void;
}

interface PropertiesData {

    nodeId: number;
    properties: { [key: string]: string };
}

interface PropertiesDao {
    fetchProperties(nodeId: string,
                    ajaxCallback: (data: PropertiesData, textStatus?: string, jqXHR?: JQueryXHR) => void): void;
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

    actionsFor(nodeId: number, ajaxCallback): JQueryXHR {

        return $.get(this.path + '/actionsFor/' + nodeId,
            ajaxCallback);
    }

    dialogFor(nodeId: number, actionName: string, ajaxCallback): JQueryXHR {
        return $.get(this.path + '/dialogFor/' +  nodeId +  '/' + actionName, ajaxCallback);
    }

    executeAction(nodeId: number, actionName: string,
                  statusCallback: (data: ActionStatus, textStatus?: string, jqXHR?: JQueryXHR) => any
    ): JQueryXHR {

        return $.get(this.path + '/action/' +  nodeId +  '/' + actionName, statusCallback);
    }

    formAction(nodeId: number, actionName: string,
               form$: JQuery,
               statusCallback: (data: ActionStatus, textStatus?: string, jqXHR?: JQueryXHR) => any
    ): JQueryXHR {

        var url = this.path + '/formAction/' +  nodeId +  '/' + actionName;

        return $.ajax({
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