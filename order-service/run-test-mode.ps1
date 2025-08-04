# PowerShell script to run the Order Service in test mode

Write-Host "Starting Order Service in test mode..."
Write-Host "This will use mock implementations of AWS services."
Write-Host ""

# Check if Java is installed
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java detected: $javaVersion"
} catch {
    Write-Host "Error: Java is not installed or not in the PATH. Please install Java 11 or higher."
    exit 1
}

# Check if Maven is installed
try {
    $mvnVersion = mvn --version 2>&1
    Write-Host "Maven detected."
} catch {
    Write-Host "Error: Maven is not installed or not in the PATH. Please install Maven."
    exit 1
}

Write-Host "Building the application..."
try {
    mvn clean compile
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error: Maven build failed."
        exit 1
    }
} catch {
    Write-Host "Error during Maven build: $_"
    exit 1
}

Write-Host "Running the application with test profile..."
Write-Host "Press Ctrl+C to stop the application."
Write-Host ""

try {
    mvn spring-boot:run -Dspring-boot.run.profiles=test
} catch {
    Write-Host "Error running the application: $_"
    exit 1
}