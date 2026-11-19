plugins {
    id("java")
    id("war")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.platform:jakarta.jakartaee-api:11.0.0")

    compileOnly("org.eclipse.persistence:org.eclipse.persistence.jpa:5.0.2")
    compileOnly("org.eclipse.persistence:org.eclipse.persistence.jpa.jpql:5.0.2")

    implementation("org.postgresql:postgresql:42.7.5")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    compileOnly("org.eclipse.microprofile:microprofile:6.1")

    implementation(project(":common"))
}