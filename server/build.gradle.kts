plugins {
    id("application")
}

repositories {
    mavenCentral()
}

application {
    mainClass = "server.Main"
}

dependencies {
    implementation(project(":common"))
    implementation("org.apache.logging.log4j:log4j-core:2.24.0")
    implementation("org.apache.logging.log4j:log4j-api:2.24.0")
}