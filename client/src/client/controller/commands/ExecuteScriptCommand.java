package client.controller.commands;

import java.io.IOException;

import client.controller.Runner;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;
import client.view.fileinput.FileInputView;
import common.utils.CommandInfo;

/**
 * Команда 'execute_script', считывающая и исполняющая скрипт из указанного файла.
 */
public class ExecuteScriptCommand extends Command {
    private static final String CMD_NAME = CommandInfo.EXECUTE_SCRIPT.getName();
    private static final int NB_ARGUMENTS = CommandInfo.EXECUTE_SCRIPT.getArguments().length;

    private final String script;

    private ExecuteScriptCommand(String script, Runner runner) {
        super(runner);

        this.script = script;
    }

    @Override
    public void execute() {
        /* runner.getView().output("Начинается исполнение скрипта `" + script + "`");

        try (FileInputView fileInputView = new FileInputView(script, runner.getView())) {
            Runner scripRunner = new Runner(
                fileInputView, runner.getCollectionManager(), runner.getCommandParser());
            scripRunner.run();

            runner.getView().output("Исполнение скрипта `" + script + "` завершено");
        } catch (IOException e) {
            runner.getView().error("Ошибка исполнения скрипта: " + e.getMessage());
        } */

        throw new UnsupportedOperationException();
    }

    /**
     * Парсер команды 'execute_script'.
     */
    public static final class Parser extends CommandParser {
        @Override
        public Command parse(String cmd, Runner runner)
        throws
            WrongNumberOfArgumentsException,
            UnsupportedCommandException
        {
            String[] args = cmd.split(" ");
            if (!args[0].equals(CMD_NAME)) {
                throw new UnsupportedCommandException(args[0]);
            }

            if (args.length - 1 != NB_ARGUMENTS) {
                throw new WrongNumberOfArgumentsException(args[0],
                        args.length - 1, NB_ARGUMENTS);
            }

            return new ExecuteScriptCommand(args[1], runner);
        }
    }
}
