mvn clean package;
scp target/voting.jar my:app.jar
ssh my ./run