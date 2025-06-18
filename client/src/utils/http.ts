import { getAccessToken, logout, refresh } from "./auth";

const SERVER_API_URL = "http://localhost:8080/api";

export const request = async (url: string, init?: RequestInit) => fetch(SERVER_API_URL + url, init);
export const redirect = (url: string) => window.location.replace(url);

const authInit = (init?: RequestInit): RequestInit => {
    if (init != undefined) {
        let newInit = { ...init };
        newInit.headers = { ...init.headers, "Authorization": `Bearer ${getAccessToken()}` };

        return newInit;
    } else {
        return {
            method: "GET",
            headers: { "Authorization": `Bearer ${getAccessToken()}` }
        };
    }
};

export const requestAuthorized = async (url: string, init?: RequestInit) => {
    let accessResponse = await request(url, authInit(init));
    if (accessResponse.ok) {
        return accessResponse;
    } else if (accessResponse.status == 401) {
        let refreshResponse;
        try {
            refreshResponse = await refresh();
        } catch (e) {
            logout();
        }
        accessResponse = await request(url, authInit(init));

        if (accessResponse.status == 401) {
            logout();
        }
    }

    return accessResponse;
};