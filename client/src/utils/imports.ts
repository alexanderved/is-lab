import * as http from './http';

export type ImportStatus = 'SUCCESS' | 'FAILED';
export type EntityType = 'worker' | 'organization' | 'person';

export type ImportOperation = {
    id: number;
    status: ImportStatus;
    objectsCount: number | null;
    userId: number | null;
};

export type ImportResult = {
    operationId: number;
    status: ImportStatus;
    objectsCount: number;
};

export type EntrySummary = {
    type: EntityType;
    count: number;
};

export type FilePreview = {
    entries: EntrySummary[];
    totalCount: number;
};

const ALLOWED_TYPES: EntityType[] = ['worker', 'organization', 'person'];

export const previewImportFile = async (file: File): Promise<FilePreview> => {
    if (file.size === 0) throw new Error('Файл пустой');
    if (file.size > 10 * 1024 * 1024) throw new Error('Файл больше 10 МБ');

    const text = await file.text();
    let parsed: any;
    try { parsed = JSON.parse(text); }
    catch { throw new Error('Файл не является корректным JSON'); }

    if (!Array.isArray(parsed)) {
        throw new Error('Корень файла должен быть массивом [ {type, body}, ... ]');
    }
    if (parsed.length === 0) {
        throw new Error('Файл пуст — нет записей для импорта');
    }

    const entries: EntrySummary[] = [];
    let total = 0;

    for (let i = 0; i < parsed.length; i++) {
        const item = parsed[i];
        const pos = `Элемент ${i + 1}`;

        if (typeof item?.type !== 'string') {
            throw new Error(`${pos}: отсутствует поле 'type'`);
        }
        if (!ALLOWED_TYPES.includes(item.type)) {
            throw new Error(
                `${pos}: неизвестный тип '${item.type}'. Ожидается: ${ALLOWED_TYPES.join(', ')}`,
            );
        }

        const body = item.body;
        if (!body) {
            throw new Error(`${pos}: 'body' отсутствует`);
        } else if (body && typeof body !== 'object') {
            throw new Error(`${pos}: 'body' должен быть объектом`);
        }

        entries.push({ type: item.type as EntityType, count: 1 });
        total += 1;
    }

    return { entries, totalCount: total };
};

export const uploadImport = async (file: File): Promise<ImportResult> => {
    const form = new FormData();
    form.append('file', file);

    const response = await http.requestAuthorized('/import', {
        method: 'POST',
        body: form,
    });

    if (!response.ok) {
        let msg = `Ошибка импорта (${response.status})`;
        try {
            const body = await response.json();
            if (body?.message) msg = body.message;
        } catch { /* */ }
        throw new Error(msg);
    }
    return response.json();
};

export const fetchImportHistory = async (): Promise<ImportOperation[]> => {
    const response = await http.requestAuthorized('/import/history', { method: 'GET' });
    if (!response.ok) throw new Error(`Не удалось загрузить историю (${response.status})`);
    return response.json();
};