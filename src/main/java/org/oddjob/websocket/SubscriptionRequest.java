package org.oddjob.websocket;

import org.oddjob.remote.NotificationType;

import java.util.Objects;

/**
 * Object sent from notification client to server.
 */
public class SubscriptionRequest {

    public SubscriptionRequest(Action action, long remoteId, NotificationType<?> type) {
        this.action = action;
        this.remoteId = remoteId;
        this.type = type;
    }

    enum Action {
        ADD,
        REMOVE,
        HEARTBEAT,
    }

    private final Action action;

    private final long remoteId;

    private final NotificationType<?> type;

    public Action getAction() {
        return action;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public NotificationType<?> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionRequest request = (SubscriptionRequest) o;
        return remoteId == request.remoteId &&
                action == request.action &&
                Objects.equals(type, request.type);
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
