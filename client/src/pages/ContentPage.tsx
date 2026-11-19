import {
    Container,
    Header,
    Content,
    Button,
    Table,
    VStack,
    HStack,
    Input,
    Nav,
    Pagination,
} from 'rsuite';
import NavHeader from '../components/NavHeader';
import { useContext, useEffect, useRef, useState } from 'react';
import * as http from '../utils/http';
import type { SortType } from 'rsuite/esm/Table';
import { getUserInfo, isLoggedOut } from '../utils/auth';
import AuthContext from '../AuthContext';
import { onUpdate, startUpdates, stopUpdates } from '../utils/updates-ws';

const { Column, HeaderCell, Cell } = Table;

export type Location = {
    x: number;
    y: number;
    z: number;
    name: string;
};

export type Address = {
    street: string | null;
    town: Location | null;
};

export type Coordinates = {
    x: number;
    y: number;
};

type TabKey = 'organization' | 'worker' | 'person';

type ColumnDef = {
    key: string;
    label: string;
    width: number;
    align?: 'left' | 'center' | 'right';
    render?: (row: any) => React.ReactNode;
};

type TabConfig = {
    key: TabKey;
    label: string;
    endpoint: string;
    columns: ColumnDef[];
};

const formatAddress = (addr: Address | null | undefined): string => {
    if (!addr) return '-';
    const town = addr.town;
    const townPart = town ? `, (${town.x}, ${town.y}, ${town.z})` : '';
    return `${addr.street ?? ''}${townPart}`.trim() || '-';
};

const TAB_CONFIGS: TabConfig[] = [
    {
        key: 'worker',
        label: 'Worker',
        endpoint: 'workers',
        columns: [
            { key: 'id', label: 'Id', width: 60, align: 'center' },
            { key: 'name', label: 'Name', width: 150 },
            { key: 'coordinates', label: 'Coordinates', width: 140,
                render: (r) => r.coordinates ? `(${r.coordinates.x}, ${r.coordinates.y})` : '-' },
            { key: 'creationDate', label: 'Creation Date', width: 170,
                render: (r) => r.creationDate ? new Date(r.creationDate).toLocaleString() : '-' },
            { key: 'organization', label: 'Organization', width: 130,
                render: (r) => r.organization ? `#${r.organization.id}` : '-' },
            { key: 'salary', label: 'Salary', width: 110 },
            { key: 'rating', label: 'Rating', width: 100 },
            { key: 'position', label: 'Position', width: 180 },
            { key: 'status', label: 'Status', width: 220 },
            { key: 'person', label: 'Person', width: 100,
                render: (r) => r.person ? `#${r.person.id}` : '-' },
            { key: 'userID', label: 'Owner', width: 80 },
        ],
    },
    {
        key: 'organization',
        label: 'Organization',
        endpoint: 'organizations',
        columns: [
            { key: 'id', label: 'Id', width: 60, align: 'center' },
            { key: 'annualTurnover', label: 'Annual Turnover', width: 150 },
            { key: 'employeesCount', label: 'Employees Count', width: 150 },
            { key: 'fullName', label: 'Full Name', width: 180 },
            { key: 'rating', label: 'Rating', width: 100 },
            { key: 'type', label: 'Type', width: 240 },
            { key: 'officialAddress', label: 'Official Address', width: 260,
                render: (r) => formatAddress(r.officialAddress) },
            { key: 'postalAddress', label: 'Postal Address', width: 260,
                render: (r) => formatAddress(r.postalAddress) },
            { key: 'userID', label: 'Owner', width: 80 },
        ],
    },
    {
        key: 'person',
        label: 'Person',
        endpoint: 'persons',
        columns: [
            { key: 'id', label: 'Id', width: 60, align: 'center' },
            { key: 'eyeColor', label: 'Eye Color', width: 130 },
            { key: 'hairColor', label: 'Hair Color', width: 130 },
            { key: 'location', label: 'Location', width: 220,
                render: (r) => r.location
                    ? `${r.location.name} (${r.location.x}, ${r.location.y}, ${r.location.z})`
                    : '-' },
            { key: 'birthday', label: 'Birthday', width: 130,
                render: (r) => r.birthday ? new Date(r.birthday).toLocaleDateString() : '-' },
            { key: 'weight', label: 'Weight', width: 100 },
            { key: 'nationality', label: 'Nationality', width: 150 },
            { key: 'userID', label: 'Owner', width: 80 },
        ],
    },
];

const ContentPage = () => {
    if (isLoggedOut()) {
        http.redirect('/signin');
        return <></>;
    }

    const ROW_HEIGHT = 46;
    const PAGE_SIZE = 15;
    const TABLE_HEIGHT = ROW_HEIGHT * (PAGE_SIZE + 1) - 6;

    const authChecked = useContext(AuthContext);
    const [activeTab, setActiveTab] = useState<TabKey>('worker');
    const [sortColumn, setSortColumn] = useState('id');
    const [sortOrder, setSortOrder] = useState('ASC');
    const [filter, setFilter] = useState('');
    const [page, setPage] = useState(1);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(false);
    const [rows, setRows] = useState<any[]>([]);
    const [selectedRow, setSelectedRow] = useState<any | null>(null);

    const currentTab = TAB_CONFIGS.find((t) => t.key === activeTab)!;
    const tableWidth = currentTab.columns.reduce((s, c) => s + c.width, 0);

    const loadData = () => {
        if (!authChecked) return;
        setLoading(true);

        const pageParams = new URLSearchParams({
            order: sortOrder,
            column: sortColumn,
            pageNumber: String(page),
            perPageNumber: String(PAGE_SIZE),
            pageCount: '1',
            filter,
        });

        const countParams = new URLSearchParams({
            column: sortColumn,
            filter,
        });

        Promise.all([
            http.requestAuthorized(
                `/${currentTab.endpoint}?${pageParams.toString()}`,
                { method: 'GET' },
            ).then((r) => (r.ok ? r.json() : Promise.reject(r))),
            http.requestAuthorized(
                `/${currentTab.endpoint}/count?${countParams.toString()}`,
                { method: 'GET' },
            ).then((r) => (r.ok ? r.json() : { count: 0 })),
        ])
            .then(([data, countData]: [any[], number]) => {
                setRows(data);
                setTotal(countData);
                console.log(countData)

                const totalPages = Math.max(1, Math.ceil(countData / PAGE_SIZE));
                if (page > totalPages) {
                    setPage(1);
                    return;
                }

                setSelectedRow((prev: any) =>
                    prev && data.some((r) => r?.id === prev.id) ? prev : null,
                );
            })
            .catch((e) => {
                console.warn('Failed to load page:', e);
            })
            .finally(() => setLoading(false));
    };

    const handleSort = (column: string, order?: SortType) => {
        setSortOrder(order?.toString().toUpperCase() || 'ASC');
        setSortColumn(column);
        setPage(1);
    };

    const handleFilterChange = (v: string) => {
        setFilter(v);
        setPage(1);
    };

    const handleTabChange = (key: string) => {
        const next = key as TabKey;
        if (next === activeTab) return;
        setActiveTab(next);
        setSelectedRow(null);
        setSortColumn('id');
        setSortOrder('ASC');
        setFilter('');
        setPage(1);
    };

    const canInteract = (row: any | null) => {
        if (!row) return false;
        const userInfo = getUserInfo();
        return row.userID === userInfo?.id
            || (userInfo?.role === 'ROLE_ADMIN' && row.userID !== 0)
            || userInfo?.role === 'ROLE_SUPERADMIN';
    };

    const handleUpdate = () => {
        if (!selectedRow || !canInteract(selectedRow)) return;
        http.redirect(`/update/${currentTab.key}/${selectedRow.id}`);
    };

    const handleRemove = () => {
        if (!selectedRow || !canInteract(selectedRow)) return;

        http.requestAuthorized(`/${currentTab.endpoint}/${selectedRow.id}`, {
            method: 'DELETE',
        })
            .then((response) => {
                if (!response.ok) throw new Error(response.statusText);
            })
            .then(() => {
                setSelectedRow(null);
                loadData();
            });
    };

    useEffect(loadData, [authChecked, activeTab, sortColumn, sortOrder, filter, page]);

    const loadDataRef = useRef(loadData);
    useEffect(() => { loadDataRef.current = loadData; }, [loadData]);

    useEffect(() => {
        startUpdates();
        const unsubscribe = onUpdate(() => loadDataRef.current());
        return () => {
            unsubscribe();
            stopUpdates();
        };
    }, []);

    return (
        <Container>
            <Header>
                <NavHeader />
            </Header>
            <Content style={{ padding: '2em' }}>
                <VStack alignItems="center" justifyContent="center" spacing={20}>
                    <Nav appearance="tabs" activeKey={activeTab} onSelect={handleTabChange}>
                        {TAB_CONFIGS.map((t) => (
                            <Nav.Item key={t.key} eventKey={t.key}>
                                {t.label}
                            </Nav.Item>
                        ))}
                    </Nav>

                    <Table
                        height={TABLE_HEIGHT}
                        rowHeight={ROW_HEIGHT}
                        width={tableWidth}
                        data={rows}
                        sortColumn={sortColumn}
                        sortType={sortOrder.toLowerCase() as SortType}
                        onSortColumn={handleSort}
                        rowClassName={(rowData) =>
                            rowData && rowData.id === selectedRow?.id ? 'selected-row' : ''
                        }
                        onRowClick={(rowData) => {
                            if (rowData) setSelectedRow(rowData);
                        }}
                        hover
                        bordered
                        loading={loading}
                    >
                        {currentTab.columns.map((col) => (
                            <Column
                                key={col.key}
                                width={col.width}
                                align={col.align ?? 'left'}
                                fullText
                                sortable
                            >
                                <HeaderCell>{col.label}</HeaderCell>
                                <Cell dataKey={col.key}>
                                    {col.render ? (row: any) => col.render!(row) : undefined}
                                </Cell>
                            </Column>
                        ))}
                    </Table>

                    <HStack alignItems="center" justifyContent="space-between" spacing={15} style={{ width: `${tableWidth}px` }}>
                        <HStack alignItems="center" justifyContent="center" spacing={15} style={{ width: `${tableWidth / 3 * 2}px` }}>
                            <Button href={`/add/${activeTab}`}>Add</Button>
                            <Button disabled={!canInteract(selectedRow)} onClick={handleUpdate}>
                                Edit
                            </Button>
                            <Button
                                color="red"
                                appearance="primary"
                                disabled={!canInteract(selectedRow)}
                                onClick={handleRemove}
                            >
                                Remove
                            </Button>
                            <Input placeholder="Filter" onChange={handleFilterChange} />
                        </HStack>

                        <Pagination
                            prev
                            next
                            first
                            last
                            ellipsis
                            boundaryLinks
                            maxButtons={3}
                            layout={['pager', 'skip']}
                            disabled={loading}
                            total={total}
                            limit={PAGE_SIZE}
                            activePage={page}
                            onChangePage={setPage}
                        />
                    </HStack>
                </VStack>
            </Content>
        </Container>
    );
};

export default ContentPage;