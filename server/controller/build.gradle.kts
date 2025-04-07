plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":server:model"))
    api(project(":server:network.providers"))
}