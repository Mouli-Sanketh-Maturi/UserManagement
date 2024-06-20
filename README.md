# Introduction

This project is a Spring Boot Java REST server designed to function within a micro-service architecture.
The application uses cloud-based MongoDB version 7.X to manage data across three main collections: `Cycle`, `User`, and `Daily_Usage`.

## Purpose

The REST server provides APIs to handle operations related to mobile phone usage and billing cycles for customers.
It is capable of managing vast amounts of data from the design to handle millions of documents efficiently.
The APIs allow for querying current and historical usage data, and managing user information.

## Design

The server uses MongoDB. The collections are structured as follows:

- **Cycle Collection**: Stores the billing cycle data for each mobile number(mdn).
- **User Collection**: Maintains user profiles, including authentication details.
- **Daily_Usage Collection**: Records data usage per mobile number on a daily basis.

The application assumes secure user identification via JWTs from a higher-level micro-service, ensuring reliable and secure operation within a distributed system.

This documentation covers the setup, usage, and features of the REST server, providing all necessary information to integrate with or further develop the server.

# Requirements

This project is built using Java and Spring Boot, and is containerized using Docker for easy deployment and scalability.
Below are the specific requirements needed to run the application:

## Software and Tools

- **Java 17**: Ensure Java 17 is installed on your system.
- **Spring Boot**: The application is developed with Spring Boot.
- **Gradle Wrapper**: This project uses Gradle as its build tool. The included Gradle Wrapper scripts (`gradlew`) ensure that you do not need a pre-installed Gradle setup. This will automatically handle the correct Gradle version and required dependencies.
- **Docker**: Docker is used for creating a containerized version of the application, and for running MongoDB server as a TestContainer for Repository based Unit Tests and Data Tests. Please ensure Docker is installed and running on your machine.

## Environment Setup

- Access to a MongoDB server (version 7.X preferably), either locally or a free version of cloud-based instance like MongoDB Atlas.

# Installation

This section provides a step-by-step guide to get the project up and running on your local machine using Docker.

## Cloning the Repository

Start by cloning the repository to your local machine:

```bash
git clone https://github.com/Mouli-Sanketh-Maturi/UserManagement.git
cd UserManagement
```
## Building the Application using Gradle

```bash
./gradlew clean build
```

If you want to skip the tests (or if docker isn't running on your setup), you can use the following command:

```bash
./gradlew clean build -xtest
```

## Creating Docker Image

The next step is to create a Docker image for the application. Run the following command:

```bash
docker build -t user-management .
```

This command builds a Docker image named user-management based on the instructions in the provided Dockerfile.

## Running the Application

Once the image is built, run your application inside a Docker container:

```bash
docker run -p 8080:8080 -e SPRING_DATA_MONGODB_URI=mongodb://your_mongodb_uri_here user-management
```

This command starts a Docker container running your application, mapping port 8080 of the container to port 8080 on your host,and sets the SPRING_DATA_MONGODB_URI environment variable to your MongoDB connection string.
This allows you to access the application via localhost:8080 in your web browser or Postman.

You can get the MongoDB connection string from your MongoDB Atlas account or your local MongoDB instance.
More information on how to get the connection string can be found in the MongoDB documentation [here](https://www.mongodb.com/docs/manual/reference/connection-string/). 

## Verifying the Application Run

To verify that the application is running, open your web browser and navigate to `http://localhost:8080/swagger-ui/index.html`.

This loads up the Swagger UI, which provides a user-friendly interface to interact with the REST APIs, along with detailed schema documentation.

## API Documentation

For detailed information on the API endpoints and schemas, see the [API Documentation](./DOCS.md) or refer to the Swagger UI.

# Testing Documentation

## Overview

The testing approach ensures that the application behaves as expected in an environment that closely resembles production.
JUnit is used as the testing framework and MongoDB Test Containers to provide a real MongoDB instance for each test.

Please ensure docker is up and running on your machine before running the tests, else the Testcontainers will fail to start the MongoDB instance, and the tests will fail.

## Running Tests

To execute all tests, including both unit tests and data tests, run the following command:

```bash
./gradlew test
```

The Mongodb for tests is autoconfigured, and comes up and down with the tests, so you don't need to worry about starting or stopping the MongoDB instance.

# Future Improvements

The current version of the application provides a solid foundation for managing user data and cycles. However, below are some areas that can be improved in future versions:
- **Additional APIs**: Add more APIs to support updating passwords, POST cycle and data usage information(which are currently read-only), and more.
- **Enhanced Security**: Implement additional security measures like SSL/TLS, authentication, and authorization mechanisms.
- **Monitoring and Logging**: Integrate monitoring tools like Prometheus and Grafana for performance tracking and logging tools like ELK stack for log management.
- **Caching**: Implement caching mechanisms to improve performance and reduce database load.
- **Error Handling**: Enhance error handling to provide more detailed error messages and improve user experience.
- **Performance Evaluation**: Conduct performance tests to identify bottlenecks and optimize the application for better scalability.
