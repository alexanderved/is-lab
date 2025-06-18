import * as http from './http';

export type UserInfo = {
    id: number,
    username: string,
    email: string,
    firstName: string,
    secondName: string,
    role: string,
};

export const findByName = async (username: string) => {
    const response = await http.request(`/users/${username}`, { method: "GET" });

    if (!response.ok) {
        return null;
    }

    return response.json() as Promise<UserInfo>;
};