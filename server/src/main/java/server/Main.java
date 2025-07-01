package server;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
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
    public HibernateConfiguration hibernateConfiguration(@Value("${db.url}") String url,
                                                         @Value("${db.user}") String user,
                                                         @Value("${db.password}") String password) {
        return new HibernateConfiguration(url, user, password);
    }

    @Bean
    public AUserRepository userRepository(HibernateConfiguration hibernateConfiguration) {
        return new UserRepositoryImpl(hibernateConfiguration);
    }

    @Bean
    public AOrganizationRepository organizationRepository(HibernateConfiguration hibernateConfiguration) {
        return new OrganizationRepositoryImpl(hibernateConfiguration);
    }

    @Bean
    public MinioClient minioClient(@Value("${minio.endpoint}") String endpoint,
                                   @Value("${minio.accessKey}") String accessKey,
                                   @Value("${minio.secretKey}") String secretKey,
                                   @Value("${minio.bucket}") String bucket)
            throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucket)
                        .build());
        if (!found) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucket)
                            .build());
        }

        return minioClient;
    }
}