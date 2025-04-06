package client.view.inputrequests;

/**
 * Запрос на ввод типа данных.
 */
public abstract class DataInputRequest<T> extends InputRequest<T> {
    private final String requestText;
    private final String commentText;

    /**
     * @param requestText Текст выводимый при запросе
     */
    protected DataInputRequest(String requestText) {
        this(requestText, null);
    }

    /**
     * @param requestText Текст выводимый при запросе
     * @param commentText Дополнительная информация, которая может быть выведена при
     *                    запросе
     */
    protected DataInputRequest(String requestText, String commentText) {
        this.requestText = requestText;
        this.commentText = commentText;
    }

    /**
     * @return текст выводимый при запросе
     */
    public String getRequestText() {
        return requestText;
    }

    /**
     * @return дополнительную информацию, которая может быть выведена при
     *         запросе
     */
    public String getCommentText() {
        return commentText;
    }
}