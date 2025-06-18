package server.utils.logger;

import org.apache.logging.log4j.LogManager;

/**
 * Логгер, использующий Log4j.
 */
public class Log4jLogger extends Logger {
    private org.apache.logging.log4j.Logger logger;

    public Log4jLogger() {
        logger = LogManager.getLogger("Server Log");
    }

    @Override
    public void info(String msg) {
        //if (logger.isInfoEnabled()) {
            logger.info(msg);
        //}
    }

    @Override
    public void info(String msg, Throwable e) {
        if (logger.isInfoEnabled()) {
            logger.info(msg, e);
        }
    }

    @Override
    public void warn(String msg) {
        if (logger.isWarnEnabled()) {
            logger.warn(msg);
        }
    }

    @Override
    public void warn(String msg, Throwable e) {
        if (logger.isWarnEnabled()) {
            logger.warn(msg, e);
        }
    }

    @Override
    public void error(String msg) {
        if (logger.isErrorEnabled()) {
            logger.error(msg);
        }
    }

    @Override
    public void error(String msg, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(msg, e);
        }
    }
}
