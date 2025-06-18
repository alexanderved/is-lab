package server.utils.logger;

/**
 * Логгер сервера.
 */
public abstract class Logger {
    private static Logger global = null;

    /**
     * Логирует сообщение с уровнем INFO.
     *
     * @param msg Сообщение
     */
    public abstract void info(String msg);

    /**
     * Логирует сообщение и ошибку с уровнем INFO.
     *
     * @param msg Сообщение
     * @param e Ошибка или исключение
     */
    public abstract void info(String msg, Throwable e);

    /**
     * Логирует сообщение с уровнем WARN.
     *
     * @param msg Сообщение
     */
    public abstract void warn(String msg);

    /**
     * Логирует сообщение и ошибку с уровнем WARN.
     *
     * @param msg Сообщение
     * @param e Ошибка или исключение
     */
    public abstract void warn(String msg, Throwable e);

    /**
     * Логирует сообщение с уровнем ERROR.
     *
     * @param msg Сообщение
     */
    public abstract void error(String msg);

    /**
     * Логирует сообщение и ошибку с уровнем ERROR.
     *
     * @param msg Сообщение
     * @param e Ошибка или исключение
     */
    public abstract void error(String msg, Throwable e);

    /**
     * @return глобальный логгер сервера
     */
    public static Logger getGlobal() {
        if (global == null) {
            global = new Log4jLogger();
        }

        return global;
    }
}
