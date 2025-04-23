package server.repository;

public final class HashedUser {
    private final int id;
    private final String name;
    private final String passwordHash;
    private final String salt;

    public HashedUser(int id, String name, String passwordHash, String salt) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }
}
