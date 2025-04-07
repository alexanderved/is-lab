plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":server:controller"))
}

application {
    mainModule = "server"
    mainClass = "server.Main"
}
