package server;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import server.service.auth.Hasher;
import server.service.auth.SHA512Hasher;
import server.repository.AOrganizationRepository;
import server.repository.AUserRepository;
import server.config.HibernateConfiguration;
import server.repository.impl.OrganizationRepositoryImpl;
import server.repository.impl.UserRepositoryImpl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Hasher hasher() {
        try {
            return new SHA512Hasher();
        } catch (NoSuchAlgorithmException e) {
            throw new BeanCreationException("hasher", "Не удалось создать Hasher", e);
        }
    }

    @Bean
    public HibernateConfiguration postgreSQLContext() {
        return new HibernateConfiguration("jdbc:postgresql:studs",
                "alex", "password");
    }

    @Bean
    public AUserRepository userDAO() {
        return new UserRepositoryImpl(postgreSQLContext());
    }

    @Bean
    public AOrganizationRepository organizationDAO() {
        return new OrganizationRepositoryImpl(postgreSQLContext());
    }

    @Bean
    public MinioClient minioClient()
            throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://127.0.0.1:9000")
                .credentials("minioadmin", "minioadmin")
                .build();

        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket("proglab")
                        .build());
        if (!found) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket("proglab")
                            .build());
        }

        return minioClient;
    }
}