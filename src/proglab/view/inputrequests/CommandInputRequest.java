package proglab.view.inputrequests;

import proglab.controller.Runner;
import proglab.controller.commands.Command;
import proglab.controller.commands.CommandParser;

/**
 * Запрос на ввод команды.
 */
public abstract class CommandInputRequest extends InputRequest<Command> {
    /**
     * Исполнитель команд.
     */
    protected final Runner runner;
    /**
     * Парсер команд.
     */
    protected final CommandParser parser;

    /**
     * @param runner Исполнитель команд
     * @param parser Парсер команд
     */
    protected CommandInputRequest(Runner runner, CommandParser parser) {
        this.runner = runner;
        this.parser = parser;
    }
}