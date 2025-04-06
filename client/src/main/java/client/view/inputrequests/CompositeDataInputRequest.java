package client.view.inputrequests;

import java.util.LinkedHashMap;
import java.util.Map;

import common.exceptions.DataParserException;

/**
 * Запрос на ввод составного типа данных.
 */
public abstract class CompositeDataInputRequest<E, T>
        extends DataInputRequest<T> {
    /**
     * Парсер составного типа данных.
     */
    protected final CompositeDataParser<E, T> parser;
    /**
     * Запросы на ввод полей составного типа данных.
     */
    protected final Map<E, InputRequest<?>> fieldRequests;

    /**
     * @param requestText Текст выводимый при запросе
     * @param parser      Парсер для данного типа данных
     */
    protected CompositeDataInputRequest(String requestText, CompositeDataParser<E, T> parser) {
        this(requestText, null, parser);
    }

    /**
     * @param requestText Текст выводимый при запросе
     * @param commentText Дополнительная информация, которая может быть выведена при
     *                    запросе
     * @param parser      Парсер для данного типа данных
     */
    protected CompositeDataInputRequest(String requestText, String commentText, CompositeDataParser<E, T> parser) {
        super(requestText, commentText);

        this.parser = parser;
        this.fieldRequests = new LinkedHashMap<>();
    }

    /**
     * Добавляет запрос на получение поля составного типа данных.
     * 
     * @param field   Поле, которое будет получено при выполнении запроса
     * @param request Запрос на ввод данного поля
     * @return запрос на ввод составного типа данных
     */
    public CompositeDataInputRequest<E, T> addFieldRequest(
            E field, InputRequest<?> request) {
        fieldRequests.put(field, request);

        return this;
    }

    /**
     * Интерфейс парсера составного типа данных.
     */
    @FunctionalInterface
    public interface CompositeDataParser<E, T> {
        /**
         * Возращает объект данного типа, получаемый из значений его полей.
         * 
         * @param data Значения полей объекта
         * @return объект данного типа
         * @throws DataParserException если в процессе парсинга возникла ошибка
         */
        T parse(Map<E, Object> data) throws DataParserException;
    }
}