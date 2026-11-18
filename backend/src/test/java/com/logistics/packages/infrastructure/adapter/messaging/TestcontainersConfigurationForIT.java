package com.logistics.packages.infrastructure.adapter.messaging;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Testcontainers para tests de integración: PostgreSQL + LocalStack (SQS, S3).
 *
 * Spring Boot 3.2 no soporta @DynamicPropertySource dentro de una @TestConfiguration,
 * así que las propiedades de AWS se registran inyectando DynamicPropertyRegistry
 * en el método @Bean del contenedor.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfigurationForIT {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.2-alpine"));
    }

    @Bean
    LocalStackContainer localstackContainer(DynamicPropertyRegistry registry) {
        LocalStackContainer localstack = new LocalStackContainer(
                DockerImageName.parse("localstack/localstack:3.4.0")
        ).withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.S3);
        localstack.start();

        registry.add("spring.cloud.aws.endpoint", () -> localstack.getEndpoint().toString());
        registry.add("spring.cloud.aws.sqs.endpoint", () -> localstack.getEndpoint().toString());
        registry.add("spring.cloud.aws.s3.endpoint", () -> localstack.getEndpoint().toString());
        registry.add("spring.cloud.aws.credentials.access-key", localstack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localstack::getSecretKey);
        registry.add("spring.cloud.aws.region.static", localstack::getRegion);
        registry.add("spring.cloud.aws.s3.path-style-access-enabled", () -> "true");
        registry.add("aws.s3.bucket-name", () -> "logistics-packages-test");
        registry.add("aws.sqs.ruta-request-queue", () -> "test-solicitar-ruta-queue");
        registry.add("aws.sqs.ruta-response-queue", () -> "test-respuestas-ruta-queue");
        return localstack;
    }

    /** Spring Cloud AWS only auto-configures SqsAsyncClient; the ITs use a sync client to inspect queues. */
    @Bean
    SqsClient sqsClient(LocalStackContainer localstack) {
        return SqsClient.builder()
                .endpointOverride(localstack.getEndpoint())
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .build();
    }
}
