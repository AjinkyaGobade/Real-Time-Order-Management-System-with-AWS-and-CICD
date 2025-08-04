# Real-Time Order Management System

A full-stack Order Management System built with React.js frontend, Spring Boot backend, and AWS integrations.

## Project Structure

- `/order-service` - Spring Boot backend application
- `/order-ui` - React.js frontend application
- `/.github/workflows` - CI/CD configuration using GitHub Actions

## Features

- Create, view, and list orders
- Upload invoice PDFs to AWS S3
- Store order data in AWS DynamoDB
- Send notifications via AWS SNS
- CI/CD pipeline with GitHub Actions

## Backend (Spring Boot)

### API Endpoints

- `POST /orders` - Create a new order
- `GET /orders/{id}` - Fetch order by ID
- `GET /orders` - List all orders

### AWS Integrations

- **DynamoDB**: Store order data
- **S3**: Upload and store invoice PDFs
- **SNS**: Send notifications on order creation

## Frontend (React.js)

### Pages

- **Dashboard** (`/`) - Display all orders in a table
- **Create Order** (`/create`) - Form to create a new order
- **Order Detail** (`/orders/:id`) - View order details and download invoice

## Setup Instructions

### Prerequisites

- Java 11+
- Node.js 14+
- AWS Account with appropriate permissions
- Maven
- npm or yarn

### AWS Setup

1. **DynamoDB Setup**
   - Create a DynamoDB table named `orders` with primary key `orderId`

2. **S3 Setup**
   - Create an S3 bucket for storing invoice PDFs
   - Configure CORS for the bucket to allow frontend access

3. **SNS Setup**
   - Create an SNS topic for order notifications
   - Create a subscription for the topic (email or SMS)

4. **IAM Setup**
   - Create an IAM user with permissions for DynamoDB, S3, and SNS
   - Generate access keys for the IAM user

### Local Development Setup

#### Backend (Spring Boot)

1. Navigate to the `order-service` directory
2. Configure AWS credentials in `application.properties`
3. Run `mvn spring-boot:run`

#### Frontend (React.js)

1. Navigate to the `order-ui` directory
2. Install dependencies: `npm install`
3. Configure API endpoint in `.env` file
4. Run development server: `npm start`

## CI/CD Pipeline

The project uses GitHub Actions for CI/CD. The workflow is defined in `.github/workflows/deploy.yml` and includes:

1. Building the Spring Boot application
2. Running tests
3. Building the React.js application
4. Deploying to AWS

## License

MIT