import * as http from './http';
import type { UserInfo } from './user';
import * as user from './user';

export type AuthState = "LOGGEDOUT" | "USER" | "ADMIN";

type TokenPair = {
    access: string,
    refresh: string,
};

export type LoginRequest = {
    username: string,
    password: string,
};

export type RegistrationRequest = {
    username: string,
    email: string,
    firstName: string,
    secondName: string,
    password: string,
};

const getAuthState = (): AuthState => {
    const userInfo = getUserInfo();

    switch (userInfo?.role) {
        case "ROLE_USER":
            return "USER";

        case "ROLE_ADMIN":
        case "ROLE_SUPERADMIN":
            return "ADMIN";

        default:
            return "LOGGEDOUT";
    }
};

export const isLoggedOut = () => getAuthState() == "LOGGEDOUT";
export const isAdmin = () => getAuthState() == "ADMIN";

export const getAccessToken = () => localStorage.getItem("accessToken");
export const getRefreshToken = () => localStorage.getItem("refreshToken");

const setTokenPair = (tokens: TokenPair) => {
    localStorage.setItem("accessToken", tokens.access);
    localStorage.setItem("refreshToken", tokens.refresh);
};

const removeTokenPair = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
};

export const getUserInfo = (): UserInfo | null => {
    const json = localStorage.getItem("userInfo");
    if (json == null) {
        return null;
    }

    return JSON.parse(json);
};

const setUserInfo = async (username: string) => {
    const userInfo = await user.findByName(username);
    localStorage.setItem("userInfo", JSON.stringify(userInfo));
};

export const updateUserInfo = async () => {
    const username = getUserInfo()?.username;
    if (username == undefined) {
        return;
    }

    await setUserInfo(username);
};

const removeUserInfo = () => {
    localStorage.removeItem("userInfo");
}

export const logout = async () => {
    localStorage.removeItem("username");
    removeTokenPair();
    removeUserInfo();

    http.redirect("/signin");
};

export const registration = async (req: RegistrationRequest) => {
    const response = await http.request("/auth/registration", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(req),
    });

    if (!response.ok) {
        throw new Error(response.statusText);
    }
};

export const login = async (req: LoginRequest) => {
    const response = await http.request("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(req),
    });

    if (!response.ok) {
        throw new Error(response.statusText);
    }

    const tokens: TokenPair = await response.json();

    localStorage.setItem("username", req.username);
    setTokenPair(tokens);
    await setUserInfo(req.username);
};

export const refresh = async () => {
    const response = await http.request("/auth/refresh", {
        method: "POST",
        body: getRefreshToken(),
    });

    if (!response.ok) {
        throw new Error(response.statusText);
    }

    const tokens: TokenPair = await response.json();

    setTokenPair(tokens);
    await updateUserInfo();
};