plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common"))
    compileOnly("org.jspecify:jspecify:1.0.0")
    api("org.apache.logging.log4j:log4j-core:2.24.0")
    api("org.apache.logging.log4j:log4j-api:2.24.0")
}