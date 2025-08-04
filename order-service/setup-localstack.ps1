# PowerShell script to set up LocalStack for local AWS service emulation

Write-Host "Setting up LocalStack for local AWS service emulation..."

# Check if Docker is installed
try {
    $dockerVersion = docker --version
    Write-Host "Docker detected: $dockerVersion"
} catch {
    Write-Host "Error: Docker is not installed or not in the PATH. Please install Docker."
    Write-Host "Download Docker Desktop from: https://www.docker.com/products/docker-desktop/"
    exit 1
}

# Check if AWS CLI is installed
try {
    $awsVersion = aws --version
    Write-Host "AWS CLI detected: $awsVersion"
} catch {
    Write-Host "Error: AWS CLI is not installed or not in the PATH."
    Write-Host "Please run the install-aws-cli.ps1 script first."
    exit 1
}

# Start LocalStack container
Write-Host "Starting LocalStack container..."
try {
    # Check if container already exists
    $containerExists = docker ps -a --filter "name=localstack" --format "{{.Names}}"
    
    if ($containerExists -eq "localstack") {
        # Check if container is running
        $containerRunning = docker ps --filter "name=localstack" --format "{{.Names}}"
        
        if ($containerRunning -eq "localstack") {
            Write-Host "LocalStack container is already running."
        } else {
            Write-Host "Starting existing LocalStack container..."
            docker start localstack
        }
    } else {
        Write-Host "Creating and starting new LocalStack container..."
        docker run -d --name localstack -p 4566:4566 -p 8000:8000 localstack/localstack
    }
} catch {
    Write-Host "Error starting LocalStack container: $_"
    exit 1
}

# Wait for LocalStack to be ready
Write-Host "Waiting for LocalStack to be ready..."
Start-Sleep -Seconds 10

# Create DynamoDB table
Write-Host "Creating DynamoDB table 'orders'..."
try {
    aws dynamodb create-table `
        --table-name orders `
        --attribute-definitions AttributeName=orderId,AttributeType=S `
        --key-schema AttributeName=orderId,KeyType=HASH `
        --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 `
        --endpoint-url http://localhost:8000 `
        --no-cli-pager
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Warning: Failed to create DynamoDB table. It might already exist."
    } else {
        Write-Host "DynamoDB table 'orders' created successfully."
    }
} catch {
    Write-Host "Warning: Failed to create DynamoDB table: $_"
    Write-Host "If the table already exists, you can ignore this warning."
}

# Create S3 bucket
Write-Host "Creating S3 bucket 'order-management-invoices'..."
try {
    aws s3 mb s3://order-management-invoices --endpoint-url http://localhost:4566 --no-cli-pager
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Warning: Failed to create S3 bucket. It might already exist."
    } else {
        Write-Host "S3 bucket 'order-management-invoices' created successfully."
    }
} catch {
    Write-Host "Warning: Failed to create S3 bucket: $_"
    Write-Host "If the bucket already exists, you can ignore this warning."
}

# Create SNS topic
Write-Host "Creating SNS topic 'order-notifications'..."
try {
    $topicArn = aws sns create-topic --name order-notifications --endpoint-url http://localhost:4566 --output text --no-cli-pager
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Warning: Failed to create SNS topic. It might already exist."
    } else {
        Write-Host "SNS topic 'order-notifications' created successfully."
        Write-Host "Topic ARN: $topicArn"
    }
} catch {
    Write-Host "Warning: Failed to create SNS topic: $_"
    Write-Host "If the topic already exists, you can ignore this warning."
}

Write-Host ""
Write-Host "LocalStack setup completed!"
Write-Host ""
Write-Host "LocalStack endpoints:"
Write-Host "  - DynamoDB: http://localhost:8000"
Write-Host "  - S3 and SNS: http://localhost:4566"
Write-Host ""
Write-Host "You can now run the application with:"
Write-Host "  mvn spring-boot:run"
Write-Host ""
Write-Host "To stop LocalStack when you're done, run:"
Write-Host "  docker stop localstack"