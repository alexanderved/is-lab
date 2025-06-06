package common.network.responses;

/**
 * Ответ от сервера об успешном добавлении нового элемента коллекции.
 */
public class AddResponse extends SuccessResponse {
    private final int addedId;

    public AddResponse(int addedId) {
        this.addedId = addedId;
    }

    public int getAddedID() {
        return addedId;
    }
}