package client.view.inputrequests;

import client.controller.Runner;
import client.controller.commands.Command;

/**
 * Запрос на ввод команды.
 */
public abstract class CommandInputRequest extends InputRequest<Command> {
    /**
     * Исполнитель команд.
     */
    protected final Runner runner;

    /**
     * @param runner Исполнитель команд
     */
    protected CommandInputRequest(Runner runner) {
        this.runner = runner;
    }
}