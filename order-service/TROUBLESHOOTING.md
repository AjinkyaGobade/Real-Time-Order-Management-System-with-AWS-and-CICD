# Troubleshooting Guide

## Connection Issues with AWS Services

### Problem: Connection Refused to LocalStack

If you encounter the following error when trying to connect to LocalStack:

```
java.net.ConnectException: Connection refused: getsockopt
```

This indicates that your application is trying to connect to a LocalStack service, but the connection is being refused. This could be due to several reasons:

1. **LocalStack is not running**
   - Make sure Docker is installed and running
   - Check if the LocalStack container is running: `docker ps | findstr localstack`
   - If not running, start it: `docker start localstack`
   - If the container doesn't exist, run the setup script: `./setup-localstack.ps1`

2. **Incorrect endpoint configuration**
   - Verify that the endpoint URLs in your configuration match the ports exposed by LocalStack:
     - DynamoDB: http://localhost:8000
     - S3 and SNS: http://localhost:4566

3. **Test profile not using LocalStack**
   - When running with the test profile, make sure your mock AWS clients are configured to use LocalStack endpoints
   - Check `MockAwsConfig.java` to ensure it has the correct endpoint overrides

### Solution

#### Option 1: Run with LocalStack

1. Install Docker if not already installed
2. Run the setup script to start LocalStack and create required resources:
   ```
   ./setup-localstack.ps1
   ```
3. Run the application without the test profile:
   ```
   mvn spring-boot:run
   ```

#### Option 2: Run in Test Mode

If you don't want to use LocalStack, you can run the application in test mode, which uses mock implementations of AWS services:

```
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

## AWS CLI Not Found

If you encounter the error `aws : The term 'aws' is not recognized as the name of a cmdlet, function, script file, or operable program`, you need to install the AWS CLI. Run the provided PowerShell script:

```
./install-aws-cli.ps1
```

After installation, you may need to restart your terminal or computer for the changes to take effect.

## Docker Not Found

If you encounter the error `docker : The term 'docker' is not recognized as the name of a cmdlet, function, script file, or operable program`, you need to install Docker. Download and install Docker Desktop from:

https://www.docker.com/products/docker-desktop/

After installation, you may need to restart your terminal or computer for the changes to take effect.

## Maven Not Found

If you encounter the error `mvn : The term 'mvn' is not recognized as the name of a cmdlet, function, script file, or operable program`, you need to install Maven. Download and install Maven from:

https://maven.apache.org/download.cgi

After installation, you need to add Maven to your PATH environment variable and restart your terminal or computer for the changes to take effect.