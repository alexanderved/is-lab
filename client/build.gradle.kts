plugins {
    id("application")
}

application {
    mainClass = "client.Main"
}

dependencies {
    implementation(project(":common"))
}

sourceSets {                                
    main {                                  
        java.srcDir("src/client/")
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "client.Main"
    }

    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}