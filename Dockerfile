FROM maven:3.8.3-openjdk-17 as builder
WORKDIR /app
COPY pom.xml ./
COPY src ./src/

RUN mvn package -DskipTests

FROM openjdk:17-oracle
COPY --from=builder /app/target/InvenTrack-RESTful-Service-1.0.0-SNAPSHOT.jar /application.jar
ENV PORT 8080
EXPOSE 8080

CMD ["java","-Dserver.port=${PORT}", "-jar", "/application.jar"]