FROM maven:3.8.1-openjdk-17 AS website-backend-builder

WORKDIR /student-management-platform

COPY src ./src
COPY pom.xml .

RUN mvn clean package -DskipTests

FROM openjdk:17

COPY --from=website-backend-builder /website-backend/target/*.jar /student-management-service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/student-management-service.jar"]
