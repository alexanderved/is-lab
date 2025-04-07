module controller {
    exports server.controller;
    exports server.controller.executors;
    requires transitive model;
    requires transitive network.providers;
}