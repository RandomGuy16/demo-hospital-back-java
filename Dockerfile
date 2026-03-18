FROM eclipse-temurin:21.0.10_7-jdk-jammy AS build

WORKDIR /workspace

COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN chmod +x gradlew

COPY src src

RUN ./gradlew --no-daemon clean bootJar

FROM eclipse-temurin:21.0.10_7-jre-jammy

WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
