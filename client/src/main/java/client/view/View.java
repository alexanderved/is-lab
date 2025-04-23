package client.view;

import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import client.view.inputrequests.InputRequest;
import client.view.inputrequests.InputRequestFactory;
import common.exceptions.InvalidFieldFormatException;
import client.controller.user.UnauthorizedUser;

/**
 * Пользовательское представление.
 */
public abstract class View {
    /**
     * Осуществляет вход пользователя.
     *
     * @return неавторизованного пользователя
     * @throws InputDeniedException   если не удалось запросить данные
     * @throws InputFailedException   если в процессе ввода возникла ошибка
     * @throws UnexpectedEODException если данные перестали поступать до окончания
     *                                запроса
     */
    public abstract UnauthorizedUser auth()
            throws InvalidFieldFormatException, InputDeniedException, InputFailedException, UnexpectedEODException;

    /**
     * Выводит сообщение для пользователя.
     * 
     * @param msg Сообщение
     */
    public abstract void output(String msg);

    /**
     * Выводит ошибку.
     * 
     * @param msg Сообщение об ошибки.
     */
    public abstract void error(String msg);

    /**
     * Запрашивает пользовательский ввод строки.
     * 
     * @param msg Сообщение, выводимое при запросе ввода
     * @return строку
     * @throws InputDeniedException   если не удалось запросить данные
     * @throws UnexpectedEODException если данные перестали поступать до окончания
     *                                запроса
     */
    public abstract String input(String msg)
            throws InputDeniedException, UnexpectedEODException;

    /**
     * Запрашивает пользовательский ввод типа данных.
     * 
     * @param <T> Заприашиваемый тип данных
     * @param req Запрос ввода
     * @return объект данного типа
     * @throws InputDeniedException   если не удалось запросить данные
     * @throws InputFailedException   если в процессе ввода возникла ошибка
     * @throws UnexpectedEODException если данные перестали поступать до окончания
     *                                запроса
     */
    public abstract <T> T input(InputRequest<T> req)
            throws InputDeniedException, InputFailedException, UnexpectedEODException;

    /**
     * @return фабрику для создания запросов ввода.
     */
    public abstract InputRequestFactory getInputRequestFactory();

    /**
     * Запрашивает пользовательский ввод строки без вывода сообщения.
     * 
     * @return строку
     * @throws InputDeniedException   если не удалось запросить данные
     * @throws UnexpectedEODException если данные перестали поступать до окончания
     *                                запроса
     */
    public String input()
            throws InputDeniedException, UnexpectedEODException {
        return input((String) null);
    }
}