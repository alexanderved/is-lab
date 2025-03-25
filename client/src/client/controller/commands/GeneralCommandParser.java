package client.controller.commands;

import java.util.LinkedHashMap;
import java.util.Map;

import client.controller.Runner;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.InvalidArgumentFormatException;
import client.exceptions.UnexpectedEODException;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;

/**
 * Парсер, который может обрабатывать различные команды.
 */
public class GeneralCommandParser extends CommandParser {
    private Map<String, CommandParser> parsers = new LinkedHashMap<>();

    @Override
    public Command parse(String cmd, Runner runner)
    throws
        WrongNumberOfArgumentsException,
        InvalidArgumentFormatException,
        UnsupportedCommandException,
        InputDeniedException,
        InputFailedException,
        UnexpectedEODException
    {
        String cmdName = cmd.split(" ")[0];
        if (!parsers.containsKey(cmdName)) {
            throw new UnsupportedCommandException(cmdName);
        }

        return parsers.get(cmdName).parse(cmd, runner);
    }

    /**
     * Добавляет парсер для одной из команд.
     * @param cmd Имя команды, которую будет обрабатывать парсер
     * @param parser Парсер команды
     */
    public void register(String cmd, CommandParser parser) {
        parsers.put(cmd, parser);
    }
}