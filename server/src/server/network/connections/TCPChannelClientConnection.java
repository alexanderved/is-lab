package server.network.connections;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import common.exceptions.NetworkCommunicationFailedException;
import common.exceptions.UnknownDataTypeReceived;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import common.utils.CustomDeserializer;
import common.utils.CustomSerializer;

public class TCPChannelClientConnection extends ClientConnection implements Closeable {
    private SocketChannel channel;
    private ByteBuffer bufferedInput;
    private ByteBuffer bufferedOutput;
    private NetworkRequest pendingRequest;

    public TCPChannelClientConnection(SocketChannel channel) throws IOException {
        this.channel = channel;
        try {
            this.channel.configureBlocking(false);
        } catch (IOException e) {
            this.channel.close();

            throw e;
        }
    }

    public SocketChannel getChannel() {
        return channel;
    }

    @Override
    public void readRequest() throws NetworkCommunicationFailedException, UnknownDataTypeReceived {
        if (!tryStartRead()) {
            return;
        }

        if (bufferedInput == null) {
            bufferedInput = ByteBuffer.allocate(Integer.BYTES);
        }

        if (bufferedInput.capacity() == Integer.BYTES) {
            try {    
                if (channel.read(bufferedInput) < 0) {
                    disconnect();

                    return;
                }
            } catch (IOException e) {
                disconnect();

                throw new NetworkCommunicationFailedException(e);
            }

            if (bufferedInput.hasRemaining()) {
                endRead();

                return;
            }

            bufferedInput.rewind();
            int length = bufferedInput.getInt();

            bufferedInput = ByteBuffer.allocate(length + Integer.BYTES);
            bufferedInput.putInt(length);
        }

        try {    
            if (channel.read(bufferedInput) < 0) {
                disconnect();

                return;
            }
        } catch (IOException e) {
            disconnect();

            throw new NetworkCommunicationFailedException(e);
        }

        if (bufferedInput.hasRemaining()) {
            endRead();

            return;
        }

        try {
            bufferedInput.rewind();
            pendingRequest = (NetworkRequest) CustomDeserializer.deserialize(bufferedInput);
        } catch (IOException | ClassNotFoundException e) {
            disconnect();

            throw new UnknownDataTypeReceived(e);
        }

        bufferedInput = null;
        nextState();
    }

    private boolean tryStartRead() throws NetworkCommunicationFailedException {
        if (isDisconnected()) {
            throw new NetworkCommunicationFailedException("Соединение с клиентом закрыто");
        }

        if (getState() != State.READ_READY) {
            return false;
        }
        nextState();

        return true;
    }

    private void endRead() {
        checkState(State.READING);

        previousState();
    }

    @Override
    public void writeResponse() throws NetworkCommunicationFailedException {
        if (!tryStartWrite()) {
            return;
        }

        try {
            channel.write(bufferedOutput);
        } catch (IOException e) {
            disconnect();

            throw new NetworkCommunicationFailedException(e);
        }

        if (bufferedOutput.hasRemaining()) {
            endWrite();

            return;
        }

        bufferedOutput = null;
        nextState();
    }

    private boolean tryStartWrite() throws NetworkCommunicationFailedException {
        if (isDisconnected()) {
            throw new NetworkCommunicationFailedException("Соединение с клиентом закрыто");
        }

        if (getState() != State.WRITE_READY) {
            return false;
        }
        nextState();

        if (bufferedOutput == null) {
            disconnect();

            throw new IllegalStateException("Отсутствует ответ на запрос");
        }

        return true;
    }

    private void endWrite() {
        checkState(State.WRITING);

        previousState();
    }

    @Override
    public NetworkRequest pollRequest() {
        if (getState() != State.HAS_PENDING_REQUEST) {
            return null;
        }

        NetworkRequest req = pendingRequest;
        pendingRequest = null;

        nextState();

        return req;
    }

    @Override
    public void offerResponse(NetworkResponse response) {
        checkState(State.WAITING_RESPONSE);

        try {
            bufferedOutput = CustomSerializer.serialize(response);

            nextState();
        } catch (IOException e) {
            disconnect();
        }
    }

    @Override
    public void close() throws IOException {
        disconnect();

        channel.close();
    }
}
