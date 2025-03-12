package proglab.view.inputrequests;

import proglab.exceptions.InputDeniedException;
import proglab.exceptions.InputFailedException;
import proglab.exceptions.UnexpectedEODException;
import proglab.view.View;

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