package server.controller;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/updates")
public class UpdateWebSocket {
    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        SESSIONS.remove(session);
    }

    public static void broadcast(String message) {
        SESSIONS.removeIf(s -> !s.isOpen());

        for (Session session : SESSIONS) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                SESSIONS.remove(session);
            }
        }
    }

    public static void notifyUpdate() {
        broadcast("update");
    }
}