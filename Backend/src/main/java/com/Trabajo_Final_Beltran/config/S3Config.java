package com.Trabajo_Final_Beltran.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@ConditionalOnProperty(
    name = "storage.provider",
    havingValue = "S3"
)
public class S3Config {

  @Bean
  public S3Client s3Client(
      StorageProperties storageProperties
  ) {

    StorageProperties.S3 config =
        storageProperties.getS3();

    AwsBasicCredentials credentials =
        AwsBasicCredentials.create(
            config.getAccessKey(),
            config.getSecretKey()
        );

    S3Configuration s3Configuration =
        S3Configuration.builder()
            .pathStyleAccessEnabled(true)

            /*
             * Cloudflare R2:
             * Si el proveedor configurado es R2,
             * se debe desactivar el chunked encoding:
             *
             * .chunkedEncodingEnabled(false)
             *
             * R2 puede devolver un error 403 por
             * signature mismatch si se utiliza el
             * chunked transfer encoding por defecto
             * del AWS SDK.
             *
             * No aplicar mientras el proveedor sea
             * AWS S3 u otro storage compatible,
             * hasta verificar sus requisitos.
             */

            .build();

    return S3Client.builder()
        .endpointOverride(
            URI.create(config.getEndpoint())
        )
        .credentialsProvider(
            StaticCredentialsProvider.create(
                credentials
            )
        )
        .region(
            Region.of(config.getRegion())
        )
        .serviceConfiguration(
            s3Configuration
        )
        .build();
  }
}