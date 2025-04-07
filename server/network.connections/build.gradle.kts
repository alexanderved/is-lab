plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":server:utils"))
}