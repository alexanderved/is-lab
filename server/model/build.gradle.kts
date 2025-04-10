plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":server:utils"))
    implementation("org.postgresql:postgresql:42.1.4")
}