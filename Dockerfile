
# 1st phase: build the jar file
# use the image for java 21 and call this container "build"
FROM eclipse-temurin:21.0.10_7-jdk-jammy AS build

WORKDIR /workspace

# copy gradle staff
COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
RUN chmod +x gradlew  # and make gradlew executable

# copy all the code
COPY src src

# create the jar executable
RUN ./gradlew --no-daemon clean bootJar


# 2nd phase: run the jar
# use eclipse java 21
FROM eclipse-temurin:21.0.10_7-jre-jammy

WORKDIR /app

# copy from build the compiled jar to the current directory (/app/app.jar)
COPY --from=build /workspace/build/libs/*.jar app.jar

# open the port 8080
EXPOSE 8080

# run the app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

