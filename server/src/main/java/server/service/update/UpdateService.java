package server.service.update;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.Response;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class UpdateService {
    private static final long TIMEOUT_MS = 25_000L;
    private final ConcurrentHashMap<String, Queue<AsyncResponse>> waiters = new ConcurrentHashMap<>();

    public void waitUpdate(String username, AsyncResponse res) {
        System.out.println("----- Wait by " + username);

        res.setTimeout(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        res.setTimeoutHandler(t -> {
            Queue<AsyncResponse> q = waiters.get(username);
            if (q != null) {
                q.remove(t);
            }

            if (!t.isDone()) {
                t.resume(Response.noContent().build());
            }
        });

        waiters.computeIfAbsent(username, k -> new ConcurrentLinkedQueue<>()).offer(res);
    }

    public void notifyUpdate(String username) {
        System.out.println("----- Notify by " + username);
        waiters.forEach((user, queue) -> {
            if (user.equals(username)) {
                queue.removeIf(AsyncResponse::isCancelled);
                return;
            }

            AsyncResponse ar;
            while ((ar = queue.poll()) != null) {
                if (!ar.isDone()) {
                    System.out.println("----- Notified");
                    ar.resume(Response.ok().build());
                }
            }
        });
    }
}