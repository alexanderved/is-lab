package common.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

/**
 * Десериализатор объектов.
 */
public final class CustomDeserializer {
    /**
     * Десериализует объект, полученный из данного потока.
     *
     * @param inputStream Поток ввода, из которого читается объект
     * @return десериализованный объект
     * @throws IOException            если при десериализации возникла ошибка ввода-вывода
     * @throws ClassNotFoundException если не удается загрузить класс объекта
     */
    public static Object deserializeFrom(InputStream inputStream) throws IOException, ClassNotFoundException {
        DataInputStream dataInput = new DataInputStream(inputStream);
        dataInput.readInt();
        ObjectInputStream objectInput = new ObjectInputStream(dataInput);

        return objectInput.readObject();
    }

    /**
     * Десериализует объект из данного набора байт.
     *
     * @param bytes Байтовое представление объекта
     * @return десериализованный объект
     * @throws IOException            если при десериализации возникла ошибка ввода-вывода
     * @throws ClassNotFoundException если не удается загрузить класс объекта
     */
    public static Object deserialize(ByteBuffer bytes) throws IOException, ClassNotFoundException {
        if (bytes.hasArray()) {
            return deserializeFrom(new ByteArrayInputStream(bytes.array()));
        } else {
            byte[] arr = new byte[bytes.capacity()];
            bytes.get(arr);

            return deserializeFrom(new ByteArrayInputStream(arr));
        }
    }
}
