package common.utils;

/**
 * Интерфейс объекта, состояние которого можно проверить на валидность
 */
public interface Validatable {
    /**
     * @return {@code true} если состояние объекта валидно
     */
    boolean isValid();
}
