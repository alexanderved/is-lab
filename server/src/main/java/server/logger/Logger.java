package server.logger;

public abstract class Logger {
    private static Logger global = null;

    public abstract void info(String msg);
    public abstract void info(String msg, Throwable e);
    public abstract void warn(String msg);
    public abstract void warn(String msg, Throwable e);
    public abstract void error(String msg);
    public abstract void error(String msg, Throwable e);

    public static Logger getGlobal() {
        if (global == null) {
            global = new Log4jLogger();
        }

        return global;
    }
}
