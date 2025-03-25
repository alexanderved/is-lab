package client.controller.commands;

import client.controller.Runner;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.InvalidArgumentFormatException;
import client.exceptions.UnexpectedEODException;
import client.exceptions.UnsupportedCommandException;
import client.exceptions.WrongNumberOfArgumentsException;

/**
 * Парсер команд.
 */
public abstract class CommandParser {
    /**
     * Парсит строку и возвращает команду, созданную на основе нее.
     * 
     * @param cmd    Строка с командой
     * @param runner Исполнитель команды
     * @return команду полученную после парсинга строки
     * @throws WrongNumberOfArgumentsException если строка с командой содержит
     *                                         неверное число аргументов
     * @throws InvalidArgumentFormatException  если формат аргументов команд неверен
     * @throws UnsupportedCommandException     если парсер не может создать команду,
     *                                         соответствующую переданной строке
     * @throws InputDeniedException            если парсер не может запросить данные
     * @throws InputFailedException            если в процессе ввода данных возникла
     *                                         ошибка
     * @throws UnexpectedEODException          если данные перестали поступать до
     *                                         окончания
     *                                         парсинга команды
     * @throws IllegalArgumentException        если исполнитель команды null
     */
    public abstract Command parse(String cmd, Runner runner)
            throws WrongNumberOfArgumentsException,
            InvalidArgumentFormatException,
            UnsupportedCommandException,
            InputDeniedException,
            InputFailedException,
            UnexpectedEODException;
}