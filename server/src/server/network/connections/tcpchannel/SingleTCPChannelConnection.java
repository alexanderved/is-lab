package server.network.connections.tcpchannel;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Function;

import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import common.utils.CustomDeserializer;
import common.utils.CustomSerializer;
import server.network.connections.Connection;

class SingleTCPChannelConnection extends Connection implements Closeable {
    private SocketChannel channel;
    private ByteBuffer reqLengthBuffer = ByteBuffer.allocate(Integer.BYTES);
    private Deque<ByteBuffer> bufferedInput = new LinkedList<>();
    private Deque<ByteBuffer> bufferedOutput = new LinkedList<>();
    private ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

    SingleTCPChannelConnection(SocketChannel channel) throws IOException {
        this.channel = channel;
        this.channel.configureBlocking(false);
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
                channel.read(reqLengthBuffer);
                if (reqLengthBuffer.remaining() > 0) {
                    break;
                }

                reqLengthBuffer.rewind();
                int length = reqLengthBuffer.getInt();
                reqLengthBuffer.clear();

                last = ByteBuffer.allocate(length + Integer.BYTES);
                last.putInt(length);
                bufferedInput.addLast(last);
            }

            channel.read(last);
            if (last.remaining() > 0) {
                break;
            }
        }
    }

    private void handleRequests(Function<NetworkRequest, NetworkResponse> responder)
            throws IOException, ClassNotFoundException, ClassCastException {
        while (!bufferedInput.isEmpty()) {
            ByteBuffer serializedRequest = bufferedInput.pollFirst();
            if (serializedRequest.remaining() > 0) {
                bufferedInput.offerFirst(serializedRequest);

                break;
            }
            serializedRequest.rewind();

            NetworkRequest req = (NetworkRequest) CustomDeserializer.deserialize(serializedRequest);
            NetworkResponse res = responder.apply(req);
            System.out.println(res.toString());

            CustomSerializer.serializeTo(byteOutput, res);
            ByteBuffer serializedResponse = ByteBuffer.wrap(byteOutput.toByteArray());
            byteOutput.reset();

            bufferedOutput.addLast(serializedResponse);
        }
    }

    private void sendResponses() throws IOException {
        while (!bufferedOutput.isEmpty()) {
            ByteBuffer first = bufferedOutput.peekFirst();
            if (channel.write(first) <= 0) {
                break;
            }

            if (first.remaining() == 0) {
                bufferedOutput.pollFirst();
            }
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
