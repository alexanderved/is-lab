package common.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public final class CustomSerializer {
    public static void serializeTo(OutputStream outputStream, Object o) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);

        objectOutput.writeObject(o);
        
        byte[] serializedObject = byteOutput.toByteArray();
        byteOutput.reset();

        DataOutputStream dataOutput = new DataOutputStream(outputStream);
        dataOutput.writeInt(serializedObject.length);
        dataOutput.write(serializedObject);
        dataOutput.flush();
    }
}
