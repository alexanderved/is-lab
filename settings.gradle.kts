rootProject.name = "proglab-sem2"
include("common", "client")
include("server", "server:controller",
    "server:model", "server:network.connections", "server:network.providers", "server:utils")
