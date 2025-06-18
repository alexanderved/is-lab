import * as http from './http';

export type UploadRequest = {
    dataURL: string,
    filename: string,
};

export const upload = async (req: UploadRequest) => {
    const response = await http.request("/upload", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(req),
    });
    
    if (!response.ok) {
        throw new Error(response.statusText);
    }
}

export const download = async (filename: string) => {
    const response = await http.request(`/download/${filename}`, {
        method: "GET"
    });
    
    if (!response.ok) {
        throw new Error(response.statusText);
    }

    return response.text();
}