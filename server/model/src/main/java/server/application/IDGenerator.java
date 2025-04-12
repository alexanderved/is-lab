package server.application;

/**
 * Генератор уникальных ID для организаций.
 */
public abstract class IDGenerator {
    /**
     * Генерирует ID организаций.
     * @return новый ID
     */
    public abstract int generateID();
}