package common.network.responses;

/**
 * Ответ от сервера об успешном добавлении нового элемента коллекции.
 */
public class AddResponse extends SuccessResponse {
    private final int addedId;

    public AddResponse(int addedId) {
        super("Новый элемент с id = " + addedId + " успешно добавлен в коллекцию");

        this.addedId = addedId;
    }

    public int getAddedID() {
        return addedId;
    }
}