package server.service.filestorage;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class FileStorageService {
    private final String minioBucket;
    private final MinioClient minioClient;

    public FileStorageService(MinioClient minioClient, @Value("${minio.bucket}") String minioBucket) {
        this.minioBucket = minioBucket;
        this.minioClient = minioClient;
    }

    public void upload(String dataURL, String filename)
            throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = decodeDataURL(dataURL);
        String mimeType = getMimeType(dataURL);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioBucket)
                            .contentType(mimeType)
                            .object(filename)
                            .stream(inputStream, inputStream.available(), -1)
                            .build());
        }
    }

    public String download(String filename)
            throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        String mimeType;
        try {
            var info = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioBucket)
                            .object(filename)
                            .build());
            mimeType = info.contentType();
        } catch (ErrorResponseException e) {
            return null;
        }

        var stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioBucket)
                        .object(filename)
                        .build());

        byte[] res = new byte[0];
        byte[] buf = new byte[16384];

        int bytesRead;
        while ((bytesRead = stream.read(buf)) >= 0) {
            byte[] newRes = new byte[res.length + bytesRead];
            System.arraycopy(res, 0, newRes, 0, res.length);
            System.arraycopy(buf, 0, newRes, res.length, bytesRead);
            res = newRes;
        }

        stream.close();

        return encodeDataURL(mimeType, res);
    }

    private byte[] decodeDataURL(String dataURL) {
        return Base64.getMimeDecoder()
                .decode(dataURL.replaceAll("data:.*?/.*?(?:;base64)?,", ""));
    }

    private String encodeDataURL(String mimeType, byte[] data) {
        String dataBase64 = Base64.getMimeEncoder().encodeToString(data);

        return "data:" + mimeType + ";base64," + dataBase64;
    }

    private String getMimeType(String dataURL) {
        return dataURL.replaceAll("data:", "")
                .replaceAll("(?:;base64)?,.*", "");
    }
}
