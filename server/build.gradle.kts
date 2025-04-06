plugins {
    id("application")
}

application {
    mainClass = "server.Main"
}

dependencies {
    implementation(project(":common"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "server.Main"
    }

    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}