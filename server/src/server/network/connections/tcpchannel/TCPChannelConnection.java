package server.network.connections.tcpchannel;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import common.exceptions.NetworkCommunicationFailedException;
import common.network.requests.NetworkRequest;
import common.network.responses.NetworkResponse;
import server.network.connections.Connection;

public class TCPChannelConnection extends Connection implements Closeable {
    public Set<SingleTCPChannelConnection> clients = new HashSet<>();

    @Override
    public void respond(Function<NetworkRequest, NetworkResponse> responder)
            throws NetworkCommunicationFailedException {
        var it = clients.iterator();
        while (it.hasNext()) {
            SingleTCPChannelConnection client = it.next();
            try {
                client.respond(responder);
            } catch (NetworkCommunicationFailedException e) {
                try {
                    it.remove();
                    client.close();
                } catch (IOException io) {
                    server.Main.logger.warning("Не удалось закрыть соединение с клиентом");
                }
            }
        }
    }

    public void addClient(SocketChannel channel) {
        try {
            clients.add(new SingleTCPChannelConnection(channel));

            try {
                server.Main.logger.info("Получено подключение от клиента по адресу " + channel.getRemoteAddress());
            } catch (IOException e) {
                server.Main.logger.info("Получено подключение от клиента");
            }
        } catch (IOException e) {
            try {
                channel.close();
            } catch (IOException io) {
                server.Main.logger.warning("Не удалось принять подключение от клиента");
            }
        }
    }

    @Override
    public void close() throws IOException {
        for (var client : clients) {
            client.close();
        }
    }
}
