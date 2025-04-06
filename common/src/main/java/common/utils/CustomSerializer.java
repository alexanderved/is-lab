package common.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class CustomSerializer {
    public static void serializeTo(OutputStream outputStream, Object o) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);

        objectOutput.writeObject(o);
        byte[] serializedObject = byteOutput.toByteArray();

        DataOutputStream dataOutput = new DataOutputStream(outputStream);
        dataOutput.writeInt(serializedObject.length);
        dataOutput.write(serializedObject);
        dataOutput.flush();
    }

    public static ByteBuffer serialize(Object o) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);

        objectOutput.writeObject(o);        
        byte[] serializedObject = byteOutput.toByteArray();

        ByteBuffer buffer = ByteBuffer.allocate(serializedObject.length + Integer.BYTES);
        buffer.putInt(serializedObject.length);
        buffer.put(serializedObject);
        buffer.rewind();

        try {
            objectOutput.close();
        } catch (IOException e) {

        }

        return buffer;
    }
}
