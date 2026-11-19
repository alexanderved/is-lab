import * as http from './http';

type Listener = () => void;

const listeners = new Set<Listener>();
let running = false;
let stopped = false;
let inflight: AbortController | null = null;
let backoffMs = 1000;

export const onUpdate = (listener: Listener): (() => void) => {
    listeners.add(listener);
    return () => listeners.delete(listener);
};

const emit = () => listeners.forEach((l) => l());

const loop = async () => {
    if (running) {
        console.log("Already running");
        return;
    }
    running = true;
    stopped = false;

    console.log("Start loop");
    while (!stopped) {
        inflight = new AbortController();
        try {
            console.log("Send Wait");
            const response = await http.requestAuthorized('/update/wait', {
                method: 'GET',
                signal: inflight.signal,
            });
            console.log("Wait Got Response");

            backoffMs = 1000;

            if (response.status === 200) {
                emit();
            } else if (response.status === 401) {
                break;
            }
        } catch (e: any) {
            if (e?.name === 'AbortError') break;

            await new Promise((r) => setTimeout(r, backoffMs));
            backoffMs = Math.min(backoffMs * 2, 30_000);
        }
    }

    running = false;
};

export const startUpdates = () => {
    console.log("Start update");
    void loop();
};

export const stopUpdates = () => {
    stopped = true;
    inflight?.abort();
    inflight = null;
    running = false;
};

export const notifyUpdate = async () => {
    try {
        await http.requestAuthorized('/update/notify', { method: 'POST' });
    } catch {
        /* ignore */
    }
};