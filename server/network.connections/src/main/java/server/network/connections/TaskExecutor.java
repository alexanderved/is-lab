package server.network.connections;

import server.logger.Logger;
import server.utils.TaskThreadFactory;

import java.util.concurrent.ExecutorService;

/**
 * Исполнитель задач чтения-записи
 */
public class TaskExecutor {
    private final TaskThreadFactory readExec;
    private final TaskThreadFactory writeExec;

    /**
     * @param readExec исполнитель задач чтения
     * @param writeExec исполнитель задач записи
     */
    public TaskExecutor(TaskThreadFactory readExec, TaskThreadFactory writeExec) {
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
