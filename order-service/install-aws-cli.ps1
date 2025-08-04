# PowerShell script to install AWS CLI v2 on Windows

# Set the download URL for AWS CLI v2 installer
$installerUrl = "https://awscli.amazonaws.com/AWSCLIV2.msi"
$installerPath = "$env:TEMP\AWSCLIV2.msi"

Write-Host "Downloading AWS CLI v2 installer..."
try {
    Invoke-WebRequest -Uri $installerUrl -OutFile $installerPath
    Write-Host "Download completed successfully."
} catch {
    Write-Host "Error downloading AWS CLI installer: $_"
    exit 1
}

Write-Host "Installing AWS CLI v2..."
try {
    Start-Process msiexec.exe -Wait -ArgumentList "/i $installerPath /quiet /norestart"
    Write-Host "AWS CLI v2 installed successfully."
} catch {
    Write-Host "Error installing AWS CLI: $_"
    exit 1
}

# Clean up the installer file
Remove-Item -Path $installerPath -Force

# Verify installation
Write-Host "Verifying AWS CLI installation..."
try {
    $awsVersion = aws --version
    Write-Host "AWS CLI installed: $awsVersion"
    
    Write-Host ""
    Write-Host "To configure AWS CLI, run the following command:"
    Write-Host "aws configure"
    Write-Host ""
    Write-Host "You will need to provide:"
    Write-Host "  - AWS Access Key ID"
    Write-Host "  - AWS Secret Access Key"
    Write-Host "  - Default region name (e.g., us-east-1)"
    Write-Host "  - Default output format (e.g., json)"
    Write-Host ""
    Write-Host "For local development with AWS services, you can use:"
    Write-Host "  - Access Key: test"
    Write-Host "  - Secret Key: test"
    Write-Host "  - Region: us-east-1"
    Write-Host "  - Output format: json"
} catch {
    Write-Host "AWS CLI verification failed: $_"
    Write-Host "You may need to restart your terminal or computer for the installation to take effect."
}