# Multi-stage build for DomPet unified API + Flutter Web static assets
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./
# Pre-fetch dependencies (no sources yet for better layer caching)
RUN ./mvnw -q -DskipTests dependency:go-offline
# Copy sources
COPY src ./src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
# Copy only the fat jar
COPY --from=build /app/target/api-0.0.1-SNAPSHOT.jar app.jar
# Environment defaults (override in Render)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
# Expose default Spring Boot port
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}"]
