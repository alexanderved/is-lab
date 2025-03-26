package server.network.connections.tcpchannel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Function;

import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import server.network.connections.Connection;

class SingleTCPChannelConnection extends Connection implements Closeable {
    private SocketChannel channel;
    private ByteBuffer reqLengthBuffer = ByteBuffer.allocate(Integer.BYTES);
    private ByteBuffer resLengthBuffer = ByteBuffer.allocate(Integer.BYTES);
    private Deque<ByteBuffer> bufferedInput = new LinkedList<>();
    private Deque<ByteBuffer> bufferedOutput = new LinkedList<>();
    private ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
    private ObjectOutputStream responseOutput;

    SingleTCPChannelConnection(SocketChannel channel) throws IOException {
        this.channel = channel;
        this.channel.configureBlocking(false);
        responseOutput = new ObjectOutputStream(byteOutput);
    }

    @Override
    public void respond(Function<NetworkRequest, NetworkResponse> responder)
            throws NetworkCommunicationFailedException {
        try {
            receiveRequests();
            handleRequests(responder);
            sendResponses();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new NetworkCommunicationFailedException(e);
        }
    }

    private void receiveRequests() throws IOException {
        while (true) {
            ByteBuffer last = bufferedInput.peekLast();

            if (last == null || last.remaining() == 0) {
                if (channel.read(reqLengthBuffer) <= 0) {
                    break;
                }

                int length = reqLengthBuffer.getInt();
                reqLengthBuffer.clear();

                last = ByteBuffer.allocate(length);
                bufferedInput.addLast(last);
            }

            if (channel.read(last) <= 0) {
                break;
            }
        }
    }

    private void handleRequests(Function<NetworkRequest, NetworkResponse> responder)
            throws IOException, ClassNotFoundException, ClassCastException {
        while (!bufferedInput.isEmpty()) {
            ByteBuffer serializedRequest = bufferedInput.pollFirst();
            try (ObjectInputStream requestInput = new ObjectInputStream(
                    new ByteArrayInputStream(serializedRequest.array()))) {
                NetworkRequest req = (NetworkRequest)requestInput.readObject();
                NetworkResponse res = responder.apply(req);

                responseOutput.writeObject(res);
                ByteBuffer serializedResponse = ByteBuffer.wrap(byteOutput.toByteArray());
                byteOutput.reset();

                bufferedOutput.addLast(serializedResponse);
            }
        }
    }

    private void sendResponses() throws IOException {
        while (!bufferedOutput.isEmpty()) {
            if (resLengthBuffer.remaining() == resLengthBuffer.capacity()) {
                channel.write(resLengthBuffer);
            }

            if (resLengthBuffer.remaining() > 0) {
                break;
            }

            ByteBuffer first = bufferedOutput.peekFirst();
            if (channel.write(first) <= 0) {
                break;
            }

            if (first.remaining() == 0) {
                bufferedOutput.pollFirst();

                if (!bufferedOutput.isEmpty()) {
                    resLengthBuffer.clear();
                    resLengthBuffer.putInt(bufferedOutput.peekFirst().capacity());
                    resLengthBuffer.rewind();
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
        responseOutput.close();
        byteOutput.close();
    }
}
