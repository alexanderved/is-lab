package server.network.connections;

import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UnknownDataTypeReceived;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;

public abstract class ClientConnection {
    private State state = State.READ_READY;

    public abstract void readRequest() throws NetworkCommunicationFailedException, UnknownDataTypeReceived;
    public abstract void writeResponse() throws NetworkCommunicationFailedException;

    public abstract NetworkRequest pollRequest();
    public abstract void offerResponse(NetworkResponse response);

    public boolean isDisconnected() {
        return getState() == State.DISCONNECTED;
    }

    public boolean hasPendingRequest() {
        return getState() == State.HAS_PENDING_REQUEST;
    }

    protected State getState() {
        return state;
    }

    protected void setState(State state) {
        this.state = state;
    }

    protected void disconnect() {
        setState(State.DISCONNECTED);
    }

    protected void nextState() {
        setState(switch (state) {
            case DISCONNECTED -> State.DISCONNECTED;
            case READ_READY -> State.READING;
            case READING -> State.HAS_PENDING_REQUEST;
            case HAS_PENDING_REQUEST -> State.WAITING_RESPONSE;
            case WAITING_RESPONSE -> State.WRITE_READY;
            case WRITE_READY -> State.WRITING;
            case WRITING -> State.READ_READY;
        });
    }

    protected void previousState() {
        setState(switch (state) {
            case DISCONNECTED -> State.DISCONNECTED;
            case READ_READY -> State.WRITING;
            case READING -> State.READ_READY;
            case HAS_PENDING_REQUEST -> State.READING;
            case WAITING_RESPONSE -> State.HAS_PENDING_REQUEST;
            case WRITE_READY -> State.READING;
            case WRITING -> State.WRITE_READY;
        });
    }

    protected void checkState(State expectedState) {
        if (getState() != expectedState) {
            disconnect();

            throw new IllegalStateException("Для выполнения данного метода соединение "
                    + "должно находиться в состоянии " + expectedState);
        }
    }

    protected enum State {
        DISCONNECTED,
        READ_READY,
        READING,
        HAS_PENDING_REQUEST,
        WAITING_RESPONSE,
        WRITE_READY,
        WRITING
    }
}
