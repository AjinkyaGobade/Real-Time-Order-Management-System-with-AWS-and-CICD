# Test Configuration

This directory contains test resources for the Order Service application.

## Running in Test Mode

To run the application in test mode, use the following command:

```
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

In test mode, the application uses mock implementations of AWS services:

1. `MockAwsConfig` - Provides mock AWS clients with LocalStack endpoints:
   - DynamoDB: http://localhost:8000
   - S3: http://localhost:4566
   - SNS: http://localhost:4566

2. `MockS3Service` - Provides an in-memory implementation of S3 service

## LocalStack Setup

If you want to use LocalStack instead of the mock implementations, make sure to:

1. Install Docker
2. Run the setup-localstack.ps1 script:
   ```
   ./setup-localstack.ps1
   ```

3. Run the application without the test profile:
   ```
   mvn spring-boot:run
   ```

## Troubleshooting

### Connection Refused

If you encounter a "Connection refused" error when trying to connect to LocalStack:

1. Make sure Docker is installed and running
2. Check if the LocalStack container is running:
   ```
   docker ps | findstr localstack
   ```

3. If not running, start it:
   ```
   docker start localstack
   ```

4. If the container doesn't exist, run the setup script:
   ```
   ./setup-localstack.ps1
   ```