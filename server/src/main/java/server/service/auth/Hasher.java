package server.service.auth;

public abstract class Hasher {
    public abstract String hash(String s);
}
