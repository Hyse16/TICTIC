plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.hyse"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val querydslVersion = "5.1.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.redisson:redisson-spring-boot-starter:3.37.0")
    implementation("com.querydsl:querydsl-jpa:${querydslVersion}:jakarta")

    runtimeOnly("com.mysql:mysql-connector-j")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("com.querydsl:querydsl-apt:${querydslVersion}:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testRuntimeOnly("com.h2database:h2")
}

val querydslDir = layout.projectDirectory.dir("src/main/generated")

sourceSets {
    named("main") {
        java.srcDir(querydslDir)
    }
}

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(querydslDir.asFile)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Delete>("clean") {
    delete(querydslDir)
}
