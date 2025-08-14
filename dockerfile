# Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# COPY .env .
RUN mvn clean package -DskipTests

# Execution
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# COPY --from=build /app/.env .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]