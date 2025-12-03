# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory inside the container
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application (skip tests to speed up)
RUN mvn -B -DskipTests clean package


# ---------- Run stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render sets PORT env var; default to 8080 if not present
ENV PORT=8080
EXPOSE 8080

# Start the Spring Boot app, binding to Render's PORT
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
