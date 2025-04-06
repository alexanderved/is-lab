package client.view.inputrequests;

import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import client.view.View;

/**
 * Запрос пользовательского ввода. 
 */
public abstract class InputRequest<T> {
    /**
     * @return представление, из которого читвется пользовательский ввод.
     */
    public abstract View getView();
    
    /**
     * Выполняет запрос ввода.
     * 
     * @return объект данного типа
     * @throws InputDeniedException   если не удалось запросить данные
     * @throws InputFailedException   если в процессе ввода возникла ошибка
     * @throws UnexpectedEODException если данные перестали поступать до окончания
     *                                запроса
     */
    public abstract T request()
        throws InputDeniedException, InputFailedException, UnexpectedEODException;
}