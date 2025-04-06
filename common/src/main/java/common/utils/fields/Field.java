package common.utils.fields;

/**
 * Интерфейс поля класса.
 */
public interface Field {
    /**
     * @return название поля
     */
    String getName();

    /**
     * @return название поля, которое будет отображено в представлении пользователя
     */
    String getDisplayName();

    /**
     * @return дополнительную информацию о поле
     */
    String getExtra();
}
