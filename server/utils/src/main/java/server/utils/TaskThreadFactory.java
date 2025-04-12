package server.utils;

public interface TaskThreadFactory {
    void submit(Runnable task);
}
