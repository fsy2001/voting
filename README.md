# Voting System for FDU Graduation 2024

## Deployment

Install the required dependencies:

- MySQL
- Java 17

Execute the following SQL commands to create the database and user:

```sql
CREATE DATABASE voting;
CREATE USER 'voting'@'localhost' IDENTIFIED BY 'voting-password';
GRANT ALL PRIVILEGES ON voting.* TO 'voting'@'localhost';
FLUSH PRIVILEGES;
```

Create image directory:
```bash
mkdir -p /tmp/image/
```

Run the following command to start the application:
```bash
java -jar app.jar
```