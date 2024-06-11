# Voting System for FDU Graduation 2024

## Build

```shell
mvn clean package
```

## Dependencies

Install the required dependencies:

- MySQL
- Java 17
- Nginx

## Deployment

### Environment Values

Set the following environment values before running:

- `DB_URL`, `DB_USER`, `DB_PASSWORD`: Information needed to connect to the database
- `CLIENT_ID`, `CLIENT_SECRET`: OAuth credentials for UIS system
- `IMAGE_LOCATION`: Where to store user-uploaded photos
- (Optional) `VOTING_DDL`, `VOTING_VISIBLE_DDL`: Deadlines for voting and votes visibility
- (Optional) `SERVER_PORT`: The server port, default 8080

### Run

Setup nginx as reverse proxy, make sure it forward client IP in header `X-Real-IP`, and run

```shell
java -jar app.jar
```