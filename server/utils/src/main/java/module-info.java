module utils {
    exports server.utils;
    exports server.logger;
    exports server.exceptions;
    requires transitive common;
    requires org.apache.logging.log4j;
}