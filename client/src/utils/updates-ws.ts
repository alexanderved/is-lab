import { SERVER_API_URL } from './http';

type Listener = () => void;

const listeners = new Set<Listener>();
let socket: WebSocket | null = null;
let reconnectTimer: number | null = null;

const getWebSocketUrl = (): string => {
    const base = SERVER_API_URL.replace(/^http/, 'ws')
        .replace(/\/api\/?$/, '');
    return `${base}/ws/updates`;
};

export const onUpdate = (listener: Listener): (() => void) => {
    listeners.add(listener);
    return () => listeners.delete(listener);
};

const emit = () => listeners.forEach((l) => l());

const connect = () => {
    if (socket && (socket.readyState === WebSocket.OPEN || socket.readyState === WebSocket.CONNECTING)) {
        return;
    }

    socket = new WebSocket(getWebSocketUrl());

    socket.onopen = () => {
        console.log('WebSocket connected');
        if (reconnectTimer) {
            clearTimeout(reconnectTimer);
            reconnectTimer = null;
        }
    };

    socket.onmessage = (event) => {
        if (event.data === 'update') {
            console.log('WebSocket update received');
            emit();
        }
    };

    socket.onclose = () => {
        console.log('WebSocket disconnected');
        socket = null;
        scheduleReconnect();
    };

    socket.onerror = (err) => {
        console.error('WebSocket error', err);
        socket?.close();
    };
};

const scheduleReconnect = () => {
    if (reconnectTimer) return;
    reconnectTimer = window.setTimeout(() => {
        reconnectTimer = null;
        connect();
    }, 3000);
};

export const startUpdates = () => connect();

export const stopUpdates = () => {
    if (reconnectTimer) {
        clearTimeout(reconnectTimer);
        reconnectTimer = null;
    }
    if (socket) {
        socket.close();
        socket = null;
    }
};
