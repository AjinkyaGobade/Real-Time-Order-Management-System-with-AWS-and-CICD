package com.example.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;

/**
 * Mock AWS configuration for local testing
 * This configuration provides local implementations of AWS services
 */
@Configuration
@Profile("test")
public class MockAwsConfig {

    @Bean
    @Primary
    public DynamoDbClient dynamoDbClient() {
        // Create a DynamoDbClient with dummy credentials and LocalStack endpoint
        return DynamoDbClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .region(Region.US_EAST_1)
                .endpointOverride(java.net.URI.create("http://localhost:8000"))
                .build();
    }

    @Bean
    @Primary
    public S3Client s3Client() {
        // Create an S3Client with dummy credentials and LocalStack endpoint
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .region(Region.US_EAST_1)
                .endpointOverride(java.net.URI.create("http://localhost:4566"))
                .build();
    }

    @Bean
    @Primary
    public SnsClient snsClient() {
        // Create an SnsClient with dummy credentials and LocalStack endpoint
        return SnsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .region(Region.US_EAST_1)
                .endpointOverride(java.net.URI.create("http://localhost:4566"))
                .build();
    }
}