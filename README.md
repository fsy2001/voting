```sql
CREATE DATABASE voting;
CREATE USER 'voting'@'localhost' IDENTIFIED BY 'voting-password';
GRANT ALL PRIVILEGES ON voting.* TO 'voting'@'localhost';
FLUSH PRIVILEGES;
```