package proglab.controller.commands;

import proglab.controller.Runner;
import proglab.exceptions.InputDeniedException;
import proglab.exceptions.InputFailedException;
import proglab.exceptions.InvalidArgumentFormatException;
import proglab.exceptions.UnexpectedEODException;
import proglab.exceptions.UnsupportedCommandException;
import proglab.exceptions.WrongNumberOfArgumentsException;

/**
 * Парсер команд.
 */
public abstract class CommandParser implements CommandsInfoSource {
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