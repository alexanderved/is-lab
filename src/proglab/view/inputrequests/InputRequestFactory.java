package proglab.view.inputrequests;

import proglab.controller.Runner;
import proglab.view.inputrequests.CompositeDataInputRequest.CompositeDataParser;
import proglab.view.inputrequests.PrimitiveDataInputRequest.PrimitiveDataParser;

/**
 * Абстрактная фабрика запросов пользовательского ввода.
 */
public abstract class InputRequestFactory {
    /**
     * Создает запрос на ввод примитивного типа данных.
     * 
     * @param <T>         Примитивный тип данных
     * @param requestText Текст выводимый при запросе
     * @param commentText Дополнительная информация, которая может быть выведена при
     *                    запросе
     * @param parser      Парсер примитивного типа данных
     * @return запрос на ввод примитивного типа данных
     */
    public abstract <T> PrimitiveDataInputRequest<T> createPrimitiveDataInputRequest(
            String requestText, String commentText, PrimitiveDataParser<T> parser);

    /**
     * Создает запрос на ввод составного типа данных.
     * 
     * @param <E>         Тип поля составного типа данных
     * @param <T>         Составной тип данных
     * @param requestText Текст выводимый при запросе
     * @param commentText Дополнительная информация, которая может быть выведена при
     *                    запросе
     * @param parser      Парсер составного типа данных
     * @return запрос на ввод составного типа данных.
     */
    public abstract <E, T> CompositeDataInputRequest<E, T> createCompositeDataInputRequest(
            String requestText, String commentText, CompositeDataParser<E, T> parser);

    /**
     * Создает опциональный запрос на ввод.
     * 
     * @param <T> Тип данных
     * @param req Запрос на ввод, который должен быть опциональным
     * @return опциональный запрос на ввод
     */
    public abstract <T> OptionalDataInputRequest<T> createOptionalInputRequest(DataInputRequest<T> req);

    /**
     * Создает запрос на ввод команды.
     * 
     * @param runner Исполнитель команд
     * @return запрос на ввод команды
     */
    public abstract CommandInputRequest createCommandInputRequest(Runner runner);

    /**
     * Создает запрос на ввод примитивного типа данных без дополнительной информации.
     * 
     * @param <T>         Примитивный тип данных
     * @param requestText Текст выводимый при запросе
     * @param parser      Парсер примитивного типа данных
     * @return запрос на ввод примитивного типа данных
     */
    public <T> PrimitiveDataInputRequest<T> createPrimitiveDataInputRequest(
            String requestText, PrimitiveDataParser<T> parser) {
        return createPrimitiveDataInputRequest(requestText, null, parser);
    }

    /**
     * Создает запрос на ввод составного типа данных без дополнительной информации.
     * 
     * @param <E>         Тип поля составного типа данных
     * @param <T>         Составной тип данных
     * @param requestText Текст выводимый при запросе
     * @param parser      Парсер составного типа данных
     * @return запрос на ввод составного типа данных.
     */
    public <E, T> CompositeDataInputRequest<E, T> createCompositeDataInputRequest(
            String requestText, CompositeDataParser<E, T> parser) {
        return createCompositeDataInputRequest(requestText, null, parser);
    }
}