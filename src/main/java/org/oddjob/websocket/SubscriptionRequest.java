package org.oddjob.websocket;

import java.util.Objects;

/**
 * Object sent from notification client to server.
 */
public class SubscriptionRequest {

    enum Action {
        ADD,
        REMOVE
    }

    private Action action;

    private long remoteId;

    private String type;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionRequest request = (SubscriptionRequest) o;
        return remoteId == request.remoteId &&
                action == request.action &&
                type.equals(request.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, remoteId, type);
    }

    @Override
    public String toString() {
        return "SubscriptionRequest{" +
                "action=" + action +
                ", remoteId=" + remoteId +
                ", type='" + type + '\'' +
                '}';
    }
}
