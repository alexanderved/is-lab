package common.network.responses;

public class UserNotFoundResponse extends  FailureResponse {
    public UserNotFoundResponse() {
        super("Пользователь не найден");
    }
}
