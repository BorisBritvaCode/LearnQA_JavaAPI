FROM csanchez/maven:3.6.3-openjdk-14
WORKDIR /tests
COPY . .
CMD mvn clean test --no-transfer-progress