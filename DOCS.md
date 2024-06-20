# REST API Documentation

This documentation provides detailed information about the available REST APIs for managing users, billing cycles, and usage data. The server is hosted at `https://takehome-53ykf.ondigitalocean.app`.

## Endpoints

### User Operations

#### Create a New User

| **Attribute** | **Details** |
| --------------|-------------|
| **URL**       | `/api/v1/user` |
| **Method**    | `POST` |
| **Description** | Creates a new user with the provided details. |
| **Request Body** (required) | - **firstName** (string): First name of the user.<br> - **lastName** (string): Last name of the user.<br> - **email** (string): Email of the user.<br> - **password** (string): Password for the user (min length: 8). |
| **Responses** | - **201**: Successfully created user. Returns the user details.<br> - **400**: Invalid input provided. |

#### Update Existing User

| **Attribute** | **Details** |
| --------------|-------------|
| **URL**       | `/api/v1/user` |
| **Method**    | `PUT` |
| **Description** | Updates an existing user's details based on the user ID provided. |
| **Request Body** (required) | - **id** (string): User ID.<br> - **firstName** (string): First name of the user.<br> - **lastName** (string): Last name of the user.<br> - **email** (string): Email of the user. |
| **Responses** | - **200**: Successfully updated user details. Returns updated user details.<br> - **400**: Invalid input provided. |

### Cycle and Usage Data Operations

#### Get Cycle History

| **Attribute** | **Details**                                                                                                                                                                                        |
| --------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **URL**       | `/api/v1/cycle-history`                                                                                                                                                                            |
| **Method**    | `GET`                                                                                                                                                                                              |
| **Description** | Retrieves the cycle history for a given mobile number (mdn) and user ID.                                                                                                                           |
| **Query Parameters** | - **userId** (string): User ID.<br> - **mdn** (string): Mobile number.                                                                                                                             |
| **Responses** | - **200**: Successfully retrieved cycle history. Returns an array of cycle information.<br> - **400**: Invalid input provided. <br> - **404**: No cycle history available for this userId and mdn. |

#### Get Current Cycle Daily Usage Report

| **Attribute** | **Details**                                                                                                                                                                                                  |
| --------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **URL**       | `/api/v1/current-cycle-report`                                                                                                                                                                               |
| **Method**    | `GET`                                                                                                                                                                                                        |
| **Description** | Retrieves the current cycle's daily usage report for a given mobile number (mdn) and user ID.                                                                                                                |
| **Query Parameters** | - **userId** (string): User ID.<br> - **mdn** (string): Mobile number.                                                                                                                                       |
| **Responses** | - **200**: Successfully retrieved current cycle report. Returns an array of daily usage reports.<br> - **400**: Invalid input provided. <br> - **404**: No active current cycle for provided userId and mdn. |

## Schemas

### `UpdateUserRequest`

| **Field** | **Type** | **Description** |
|-----------|----------|-----------------|
| id        | string   | User ID. |
| firstName | string   | First name. |
| lastName  | string   | Last name. |
| email     | string   | Email address. |

### `CreateUserRequest`

| **Field** | **Type** | **Description** |
|-----------|----------|-----------------|
| firstName | string   | First name. |
| lastName  | string   | Last name. |
| email     | string   | Email address. |
| password  | string   | Password for the user account. |

### `UserResponse`

| **Field** | **Type** | **Description** |
|-----------|----------|-----------------|
| id        | string   | User ID. |
| firstName | string   | First name. |
| lastName  | string   | Last name. |
| email     | string   | Email address. |

### `CycleInfo`

| **Field**    | **Type**            | **Description** |
|--------------|---------------------|-----------------|
| cycleId      | string              | Unique identifier for the billing cycle. |
| startDate    | string, date-time   | Start date of the cycle. |
| endDate      | string, date-time   | End date of the cycle. |

### `DailyUsageReport`

| **Field**    | **Type**            | **Description** |
|--------------|---------------------|-----------------|
| date         | string, date-time   | Date of usage. |
| dailyUsage   | integer             | Data used on the specified date. |

### `ProblemDetail`

| **Field**    | **Type**            | **Description** |
|--------------|---------------------|-----------------|
| type         | string, uri         | A URI reference that identifies the problem type. |
| title        | string              | A short, human-readable summary of the problem type. |
| status       | integer             | The HTTP status code generated by the origin server for this occurrence of the problem. |
| detail       | string              | A human-readable explanation specific to this occurrence of the problem. |
| instance     | string, uri         | A URI reference that identifies the specific occurrence of the problem. |
