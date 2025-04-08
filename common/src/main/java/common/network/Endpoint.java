package common.network;

import common.exceptions.EndpointDisconnectedException;
import common.exceptions.NetworkCommunicationFailedException;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * Конечная точка в сети.
 */
public abstract class Endpoint implements Closeable {
    /**
     * Читает данные из сети.
     *
     * @param buffer Буфер для записи полученных данных
     * @throws NetworkCommunicationFailedException если произошла ошибка сети
     * @throws EndpointDisconnectedException       если соединение было разорвано
     */
    public abstract void read(ByteBuffer buffer)
            throws NetworkCommunicationFailedException, EndpointDisconnectedException;

    /**
     * Отправляет данные в сеть.
     *
     * @param buffer Буфер с данными
     * @throws NetworkCommunicationFailedException если произошла ошибка сети
     * @throws EndpointDisconnectedException       если соединение было разорвано
     */
    public abstract void write(ByteBuffer buffer)
            throws NetworkCommunicationFailedException, EndpointDisconnectedException;

    /**
     * @return поток ввода конечной точки
     * @throws NetworkCommunicationFailedException если произошла ошибка сети
     * @throws EndpointDisconnectedException       если соединение было разорвано
     */
    public abstract InputStream getInputStream()
            throws NetworkCommunicationFailedException, EndpointDisconnectedException;

    /**
     * @return поток вывода конечной точки
     * @throws NetworkCommunicationFailedException если произошла ошибка сети
     * @throws EndpointDisconnectedException       если соединение было разорвано
     */
    public abstract OutputStream getOutputStream()
            throws NetworkCommunicationFailedException, EndpointDisconnectedException;

    /**
     * @return адрес устройства к которому подключена конечная точка
     * @throws NetworkCommunicationFailedException если произошла ошибка сети
     * @throws EndpointDisconnectedException       если соединение было разорвано
     */
    public abstract SocketAddress getRemoteAddress()
            throws NetworkCommunicationFailedException, EndpointDisconnectedException;
}
