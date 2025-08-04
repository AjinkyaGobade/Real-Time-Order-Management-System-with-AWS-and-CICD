# Order Service

This is a Spring Boot application that provides REST APIs for order management. It uses AWS services (DynamoDB, S3, and SNS) for data storage and notifications.

## Prerequisites

- Java 11 or higher
- Maven
- AWS CLI (for local development with AWS services)

## AWS Configuration

This application uses AWS services. For local development, you can use:

1. Install AWS CLI by running the provided script:
   ```
   ./install-aws-cli.ps1
   ```

2. Configure AWS CLI with test credentials:
   ```
   aws configure
   ```
   Use the following values:
   - AWS Access Key ID: `test`
   - AWS Secret Access Key: `test`
   - Default region name: `us-east-1`
   - Default output format: `json`

## Local Development

### Running the Application

1. Build the application:
   ```
   mvn clean install
   ```

2. Run the application:
   ```
   mvn spring-boot:run
   ```

3. For testing with mock AWS services, use the test profile:
   ```
   mvn spring-boot:run -Dspring-boot.run.profiles=test
   ```

### Testing

Run the tests using Maven:
```
mvn test
```

## API Endpoints

### Health Check
- `GET /health` - Check if the application is running

### Order Management
- `POST /orders` - Create a new order
  - Parameters:
    - `customerName` (string, required)
    - `orderAmount` (string, required)
    - `orderDate` (string, required, format: YYYY-MM-DD)
    - `invoiceFile` (file, optional)

- `GET /orders` - Get all orders

- `GET /orders/{orderId}` - Get a specific order by ID

- `GET /orders/{orderId}/invoice` - Download the invoice for a specific order

## Local AWS Services

For local development, you can use LocalStack to emulate AWS services:

1. Install Docker

2. Run LocalStack:
   ```
   docker run --name localstack -p 4566:4566 -p 8000:8000 localstack/localstack
   ```

3. Create required resources:
   ```
   # Create DynamoDB table
   aws dynamodb create-table \
     --table-name orders \
     --attribute-definitions AttributeName=orderId,AttributeType=S \
     --key-schema AttributeName=orderId,KeyType=HASH \
     --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
     --endpoint-url http://localhost:8000

   # Create S3 bucket
   aws s3 mb s3://order-management-invoices --endpoint-url http://localhost:4566

   # Create SNS topic
   aws sns create-topic --name order-notifications --endpoint-url http://localhost:4566
   ```

## Troubleshooting

### AWS CLI Not Found

If you encounter the error `aws : The term 'aws' is not recognized as the name of a cmdlet, function, script file, or operable program`, you need to install the AWS CLI. Run the provided PowerShell script:

```
./install-aws-cli.ps1
```

After installation, you may need to restart your terminal or computer for the changes to take effect.

### Connection Issues with AWS Services

If you encounter connection issues with AWS services, ensure that:

1. LocalStack is running (if using local development)
2. The endpoint URLs in `AwsConfig.java` match your LocalStack configuration
3. AWS credentials are properly configured

### Testing Mode

For testing without actual AWS services, use the test profile which uses mock implementations:

```
mvn spring-boot:run -Dspring-boot.run.profiles=test
```