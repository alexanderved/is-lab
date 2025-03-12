package proglab.view.inputrequests;

/**
 * Запрос на ввод необязательных данных.
 */
public abstract class OptionalDataInputRequest<T> extends DataInputRequest<T> {
    /**
     * Запрос на ввод данных.
     */
    protected final DataInputRequest<T> req;

    /**
     * @param req Запрос на ввод данных, которые не являются обязательными
     */
    protected OptionalDataInputRequest(DataInputRequest<T> req) {
        super(req.getRequestText(), req.getCommentText());

        this.req = req;
    }
}
