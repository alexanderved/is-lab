package common.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

public final class CustomDeserializer {
    public static Object deserializeFrom(InputStream inputStream) throws IOException, ClassNotFoundException {
        DataInputStream dataInput = new DataInputStream(inputStream);
        dataInput.readInt();
        ObjectInputStream objectInput = new ObjectInputStream(dataInput);

        return objectInput.readObject();
    }

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
