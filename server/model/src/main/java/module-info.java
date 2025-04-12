module model {
    exports server.application;
    exports server.repository;
    exports server.repository.postgresql;
    requires transitive utils;
    requires java.sql;
    requires org.postgresql.jdbc;
}