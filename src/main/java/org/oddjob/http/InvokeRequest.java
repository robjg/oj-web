package org.oddjob.http;

import org.oddjob.remote.OperationType;

import java.util.Arrays;

/**
 * Request that is sent from client to server.
 */
public class InvokeRequest {

    private final long remoteId;

    private final OperationType<?> operationType;

    private final Object[] args;

    public InvokeRequest(long remoteId, OperationType<?> operationType, Object[] args) {
        this.remoteId = remoteId;
        this.operationType = operationType;
        this.args = args;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public OperationType<?> getOperationType() {
        return operationType;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "InvokeRequest{" +
                "remoteId=" + remoteId +
                ", operationType=" + operationType +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    public static OperationChoice forRemoteId(long remoteId) {

        return new OperationChoice((remoteId));
    }

    public static class OperationChoice {

        private final long remoteId;


        public OperationChoice(long remoteId) {
            this.remoteId = remoteId;
        }

        public ArgumentChoice withOperation(OperationType<?> operationType) {

            return new ArgumentChoice(remoteId, operationType);
        }
    }

    public static class ArgumentChoice {

        private final long remoteId;

        private final OperationType<?> operationType;

        public ArgumentChoice(long remoteId, OperationType<?> operationType) {
            this.remoteId = remoteId;
            this.operationType = operationType;
        }

        public InvokeRequest andArgs(Object... args) {

            return new InvokeRequest(remoteId,
                    operationType,
                    args);
        }

    }

}
