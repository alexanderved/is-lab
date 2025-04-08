package server.network.connections;

import server.logger.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Исполнитель задач чтения-записи
 */
public class TaskExecutor {
    private final ExecutorService readExec;
    private final ExecutorService writeExec;

    /**
     * @param readExec исполнитель задач чтения
     * @param writeExec исполнитель задач записи
     */
    public TaskExecutor(ExecutorService readExec, ExecutorService writeExec) {
        this.readExec = readExec;
        this.writeExec = writeExec;
    }

    public void submitRead(Runnable readTask) {
        Logger.getGlobal().info("Запланировано чтение запроса");

        readExec.submit(readTask);
    }

    public void submitWrite(Runnable writeTask) {
        Logger.getGlobal().info("Запланирована запись ответа");

        writeExec.submit(writeTask);
    }
}
