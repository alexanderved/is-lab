package server.resource.validators;

public abstract class ResourceValidator<R> {
    public abstract boolean isValid(R res);
}
