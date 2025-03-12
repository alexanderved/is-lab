package proglab.view.inputrequests;

import proglab.exceptions.DataParserException;

/**
 * Запрос на ввод примитивного типа данных.
 */
public abstract class PrimitiveDataInputRequest<T> extends DataInputRequest<T> {
    /**
     * Парсер примитивного типа данных.
     */
    protected final PrimitiveDataParser<T> parser;

    /**
     * @param requestText Текст выводимый при запросе
     * @param parser      Парсер для данного типа данных
     */
    protected PrimitiveDataInputRequest(String requestText, PrimitiveDataParser<T> parser) {
        this(requestText, null, parser);
    }

    /**
     * @param requestText Текст выводимый при запросе
     * @param commentText Дополнительная информация, которая может быть выведена при
     *                    запросе
     * @param parser      Парсер для данного типа данных
     */
    protected PrimitiveDataInputRequest(String requestText, String commentText, PrimitiveDataParser<T> parser) {
        super(requestText, commentText);

        this.parser = parser;
    }

    /**
     * Интерфейс парсера примитивного типа данных.
     */
    @FunctionalInterface
    public interface PrimitiveDataParser<T> {
        /**
         * Возращает объект данного типа, получаемый путем парсинга строки.
         * 
         * @param data Строка
         * @return объект данного типа
         * @throws DataParserException если в процессе парсинга возникла ошибка
         */
        T parse(String data) throws DataParserException;
    }
}