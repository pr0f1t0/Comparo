package com.pr0f1t.comparo.catalogservice.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.url:http://localhost:9000}")
    private String url;

    @Value("${minio.access-key:comparo_minio}")
    private String accessKey;

    @Value("${minio.secret-key:secure_minio_password}")
    private String secretKey;

    @Value("${minio.bucket-name:comparo-products}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();

        try {
            boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                String policy = """
                        {
                          "Statement": [
                            {
                              "Action": ["s3:GetObject"],
                              "Effect": "Allow",
                              "Principal": "*",
                              "Resource": ["arn:aws:s3:::%s/*"]
                            }
                          ],
                          "Version": "2012-10-17"
                        }
                        """.formatted(bucketName);

                client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
                log.info("Bucket '{}' created successfully with public read access", bucketName);
            }
        } catch (Exception e) {
            log.error("Error initializing MinIO bucket", e);
        }

        return client;
    }
}
