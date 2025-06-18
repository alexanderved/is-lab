plugins {
    id("org.springframework.boot") version("3.4.5")
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.5"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.postgresql:postgresql:42.7.5")
    implementation(platform("org.hibernate.orm:hibernate-platform:6.6.0.Final"))
    implementation("org.hibernate.orm:hibernate-core")
    implementation("jakarta.transaction:jakarta.transaction-api")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    implementation(project(":common"))
    compileOnly("org.jspecify:jspecify:1.0.0")
    implementation("org.apache.logging.log4j:log4j-core:2.24.0")
    implementation("org.apache.logging.log4j:log4j-api:2.24.0")

    implementation("io.minio:minio:8.5.17")
}