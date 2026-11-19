import * as http from '../utils/http';

export type AddType = 'worker' | 'organization' | 'person';

export const POSITIONS = ['DIRECTOR', 'LABORER', 'HEAD_OF_DIVISION', 'MANAGER_OF_CLEANING'] as const;
export const STATUSES = ['FIRED', 'RECOMMENDED_FOR_PROMOTION', 'REGULAR'] as const;
export const ORGANIZATION_TYPES = ['GOVERNMENT', 'PRIVATE_LIMITED_COMPANY', 'OPEN_JOINT_STOCK_COMPANY'] as const;
export const COLORS = ['RED', 'BLACK', 'YELLOW', 'WHITE'] as const;
export const COUNTRIES = ['USA', 'SPAIN', 'ITALY', 'SOUTH_KOREA', 'JAPAN'] as const;

export const ENDPOINTS: Record<AddType, string> = {
    worker: 'workers',
    organization: 'organizations',
    person: 'persons',
};

export const ADD_TITLES: Record<AddType, string> = {
    worker: 'Add Worker',
    organization: 'Add Organization',
    person: 'Add Person',
};

export const UPDATE_TITLES: Record<AddType, string> = {
    worker: 'Update Worker',
    organization: 'Update Organization',
    person: 'Update Person',
};

export const numOrNull = (v: any): number | null => {
    if (v === '' || v == null) return null;
    const n = Number(v);
    return Number.isNaN(n) ? null : n;
};

export const strOrNull = (v: any): string | null => {
    if (v == null) return null;
    const s = String(v);
    return s.length === 0 ? null : s;
};

export const enumData = (values: readonly string[]) =>
    values.map((v) => ({ label: v, value: v }));

export const buildAddress = (v: Record<string, any>, prefix: string) => {
    const street = strOrNull(v[`${prefix}Street`]);
    const tx = numOrNull(v[`${prefix}Tx`]);
    if (street == null && tx == null) return null;
    return {
        street,
        town:
            tx == null
                ? null
                : {
                    x: tx,
                    y: numOrNull(v[`${prefix}Ty`]) ?? 0,
                    z: numOrNull(v[`${prefix}Tz`]) ?? 0,
                    name: v[`${prefix}Tname`] ?? '',
                },
    };
};

export const formatDateForInput = (d: any): string => {
    if (!d) return '';
    const date = new Date(d);
    if (Number.isNaN(date.getTime())) return '';
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
};

export const fetchAll = (endpoint: string): Promise<any[]> =>
    http.requestAuthorized(
        `/${endpoint}?order=ASC&column=id&pageNumber=1&perPageNumber=1000&pageCount=1&filter=`,
        { method: 'GET' },
    )
        .then((r) => (r.ok ? r.json() : []))
        .catch(() => []);

export const fetchById = async (endpoint: string, id: number): Promise<any | null> => {
    const all = await fetchAll(endpoint);
    return all.find((r) => r.id === id) ?? null;
};

const buildWorkerBody = (v: Record<string, any>) => ({
    name: v.name,
    coordinates: { x: numOrNull(v.cx) ?? 0, y: numOrNull(v.cy) ?? 0 },
    organizationId: numOrNull(v.organizationId),
    salary: numOrNull(v.salary) ?? 0,
    rating: numOrNull(v.rating),
    position: v.position || null,
    status: v.status || null,
    personId: numOrNull(v.personId),
});

const buildOrganizationBody = (v: Record<string, any>) => ({
    annualTurnover: numOrNull(v.annualTurnover),
    employeesCount: numOrNull(v.employeesCount),
    fullName: strOrNull(v.fullName),
    rating: numOrNull(v.rating),
    type: v.type,
    officialAddress: buildAddress(v, 'off'),
    postalAddress: buildAddress(v, 'post'),
});

const buildPersonBody = (v: Record<string, any>) => ({
    eyeColor: v.eyeColor,
    hairColor: v.hairColor,
    location: {
        x: numOrNull(v.lx) ?? 0,
        y: numOrNull(v.ly) ?? 0,
        z: numOrNull(v.lz) ?? 0,
        name: v.lname ?? '',
    },
    birthday: v.birthday ? new Date(v.birthday).toISOString() : null,
    weight: numOrNull(v.weight) ?? 0,
    nationality: v.nationality || null,
});

export const buildBody: Record<AddType, (v: Record<string, any>) => any> = {
    worker: buildWorkerBody,
    organization: buildOrganizationBody,
    person: buildPersonBody,
};

const fromWorkerDomain = (w: any) => ({
    name: w.name ?? '',
    cx: w.coordinates?.x ?? '',
    cy: w.coordinates?.y ?? '',
    organizationId: w.organization?.id ?? null,
    salary: w.salary ?? '',
    rating: w.rating ?? '',
    position: w.position ?? null,
    status: w.status ?? null,
    personId: w.person?.id ?? null,
});

const fromOrganizationDomain = (o: any) => {
    const addr = (a: any, prefix: string) => {
        if (!a) {
            return {
                [`${prefix}Street`]: '',
                [`${prefix}Tx`]: '',
                [`${prefix}Ty`]: '',
                [`${prefix}Tz`]: '',
                [`${prefix}Tname`]: '',
            };
        }
        return {
            [`${prefix}Street`]: a.street ?? '',
            [`${prefix}Tx`]: a.town?.x ?? '',
            [`${prefix}Ty`]: a.town?.y ?? '',
            [`${prefix}Tz`]: a.town?.z ?? '',
            [`${prefix}Tname`]: a.town?.name ?? '',
        };
    };

    return {
        annualTurnover: o.annualTurnover ?? '',
        employeesCount: o.employeesCount ?? '',
        fullName: o.fullName ?? '',
        rating: o.rating ?? '',
        type: o.type ?? null,
        ...addr(o.officialAddress, 'off'),
        ...addr(o.postalAddress, 'post'),
    };
};

const fromPersonDomain = (p: any) => ({
    eyeColor: p.eyeColor ?? null,
    hairColor: p.hairColor ?? null,
    lx: p.location?.x ?? '',
    ly: p.location?.y ?? '',
    lz: p.location?.z ?? '',
    lname: p.location?.name ?? '',
    birthday: formatDateForInput(p.birthday),
    weight: p.weight ?? '',
    nationality: p.nationality ?? null,
});

export type FieldErrors = Record<string, string>;

const checkTownInto = (
    errors: FieldErrors,
    v: Record<string, any>,
    prefix: string,
    label: string,
) => {
    const tx = numOrNull(v[`${prefix}Tx`]);
    const ty = numOrNull(v[`${prefix}Ty`]);
    const tz = numOrNull(v[`${prefix}Tz`]);
    const tname = v[`${prefix}Tname`] ?? '';
    const anySet = tx != null || ty != null || tz != null || tname.length > 0;
    if (!anySet) return;

    if (tx == null) errors[`${prefix}Tx`] = `${label} town X is required`;
    if (ty == null) errors[`${prefix}Ty`] = `${label} town Y is required`;
    if (tz == null) errors[`${prefix}Tz`] = `${label} town Z is required`;
    if (!tname.trim()) errors[`${prefix}Tname`] = `${label} town name is required`;
};

export const validate = (addType: AddType, v: Record<string, any>): FieldErrors => {
    const errors: FieldErrors = {};

    if (addType === 'worker') {
        if (!v.name?.trim()) errors.name = 'Name is required';
        if (numOrNull(v.cx) == null) errors.cx = 'Coordinates X is required';
        if (numOrNull(v.cy) == null) errors.cy = 'Coordinates Y is required';
        if ((numOrNull(v.salary) ?? 0) <= 0) errors.salary = 'Salary must be a positive integer';
        const r = numOrNull(v.rating);
        if (v.rating !== '' && v.rating != null && (r == null || r <= 0))
            errors.rating = 'Rating must be positive';
        const pid = numOrNull(v.personId);
        if (pid == null || pid <= 0) errors.personId = 'Person is required';
    }

    if (addType === 'organization') {
        if ((numOrNull(v.annualTurnover) ?? 0) <= 0)
            errors.annualTurnover = 'Annual turnover must be a positive integer';
        if ((numOrNull(v.employeesCount) ?? 0) <= 0)
            errors.employeesCount = 'Employees count must be a positive integer';
        if (!v.type) errors.type = 'Type is required';
        if (v.fullName && !v.fullName.trim()) errors.fullName = 'Full name cannot be blank';
        const r = numOrNull(v.rating);
        if (v.rating !== '' && v.rating != null && (r == null || r <= 0))
            errors.rating = 'Rating must be positive';
        if ((v.offStreet?.length ?? 0) > 112)
            errors.offStreet = 'Official street must be at most 112 characters';
        if ((v.postStreet?.length ?? 0) > 112)
            errors.postStreet = 'Postal street must be at most 112 characters';
        checkTownInto(errors, v, 'off', 'Official');
        checkTownInto(errors, v, 'post', 'Postal');
    }

    if (addType === 'person') {
        if (!v.eyeColor) errors.eyeColor = 'Eye color is required';
        if (!v.hairColor) errors.hairColor = 'Hair color is required';
        if (numOrNull(v.lx) == null) errors.lx = 'Location X is required';
        if (numOrNull(v.ly) == null) errors.ly = 'Location Y is required';
        if (numOrNull(v.lz) == null) errors.lz = 'Location Z is required';
        if (!v.lname?.trim()) errors.lname = 'Location name is required';
        if ((numOrNull(v.weight) ?? 0) <= 0) errors.weight = 'Weight must be a positive integer';
    }

    return errors;
};

export const fromDomain: Record<AddType, (e: any) => Record<string, any>> = {
    worker: fromWorkerDomain,
    organization: fromOrganizationDomain,
    person: fromPersonDomain,
};
