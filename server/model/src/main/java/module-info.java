module model {
    exports server.application;
    exports server.repository;
    exports server.repository.xml;
    requires transitive utils;
    requires java.xml;
}