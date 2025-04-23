package common.network.responses;

public class UserNameAlreadyTakenResponse extends FailureResponse {
    private final String name;

    public UserNameAlreadyTakenResponse(String name) {
        super("Имя пользователя '" + name + "' уже занято");

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
