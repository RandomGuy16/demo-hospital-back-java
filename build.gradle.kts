import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm

plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.evergreen.generalhospital"
version = "0.0.1-SNAPSHOT"
description = "Evergreen General Hospital API"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // spring boot necessary imports
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // this one is the Spring Data JPA dependency with transaction.annotation.Transactional
    // dont confuse with jakarta.transaction.Transactional
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    implementation("jakarta.validation:jakarta.validation-api") // Use the latest stable version

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // this one is for beautiful swagger docs
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1")

    // this one if to enable auto reload
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // enforce validations with this one
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // implement flyway
    implementation("org.springframework.boot:spring-boot-flyway")  // in spring 4 is now a separate module
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql") // support for postgresql

    // backend validates JWTs issued by the external identity provider
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")  // handle google auth
    testImplementation("org.springframework.security:spring-security-test")
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // properties migrator
    runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

    // spring 4: modularization (adding modularized modules)
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<UpdateDaemonJvm>("updateDaemonJvm") {
    languageVersion = JavaLanguageVersion.of(21)
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}

tasks.test {
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
