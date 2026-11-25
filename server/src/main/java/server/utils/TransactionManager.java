package server.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import server.exceptions.InvalidFieldFormatException;
import server.exceptions.UniquenessConstraintViolationException;

@ApplicationScoped
public class TransactionManager {
    private static final int MAX_ATTEMPTS = 5;
    private static final long BASE_DELAY_MS = 50;

    @PersistenceContext(unitName = "default")
    protected EntityManager em;

    public void makeSerializable() {
        em.createNativeQuery("SET TRANSACTION ISOLATION LEVEL SERIALIZABLE").executeUpdate();
    }

    public <T> T run(SupplierWithCheckedException<T> action) {
        Exception last = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return action.get();
            } catch (Exception e) {
                if ((e instanceof InvalidFieldFormatException ce)) {
                    throw ce;
                }

                if ((e instanceof UniquenessConstraintViolationException ce)) {
                    throw ce;
                }

                if (attempt == MAX_ATTEMPTS) {
                    if (e instanceof RuntimeException re) throw re;
                    throw new RuntimeException(e);
                }

                last = e;
                sleep(BASE_DELAY_MS * attempt);
            }
        }
        throw new RuntimeException("Unreachable", last);
    }

    private static void sleep(long baseMs) {
        try {
            Thread.sleep(baseMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during retry backoff", e);
        }
    }

    @FunctionalInterface
    public interface SupplierWithCheckedException<T> {
        T get() throws Exception;
    }
}