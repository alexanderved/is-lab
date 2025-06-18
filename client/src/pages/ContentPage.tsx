import {
    Container,
    Header,
    Content,
    Button,
    Table,
    VStack,
    HStack,
    Input
} from 'rsuite';
import NavHeader from '../components/NavHeader';
import { useContext, useEffect, useState } from 'react';
import * as http from '../utils/http';
import type { SortType } from 'rsuite/esm/Table';
import { getUserInfo, isLoggedOut } from '../utils/auth';
import AuthContext from '../AuthContext';

const { Column, HeaderCell, Cell } = Table;

export type Coordinates = {
    x: number,
    y: number
};

export type Location = {
    x: number,
    y: number,
    z: number
};

export type Address = {
    street: string,
    town: Location
};

export type Organization = {
    id: number,
    name: string,
    coordinates: Coordinates,
    creationDate: string,
    annualTurnover: number,
    fullName: string,
    employeesCount: number,
    type: string,
    officialAddres: Address,
    userID: number,
};

const ContentPage = () => {
    if (isLoggedOut()) {
        http.redirect("/signin");

        return <></>;
    }

    const TABLE_HEIGHT = 800;
    const ROW_HEIGHT = 46;
    const LOAD_MORE_DELTA = 100;
    const PER_PAGE_NUMBER = Math.ceil(TABLE_HEIGHT / ROW_HEIGHT);

    const authChecked = useContext(AuthContext);
    const [sortColumn, setSortColumn] = useState("id");
    const [sortOrder, setSortOrder] = useState("ASC");
    const [nameFilter, setNameFilter] = useState("");
    const [pageCount, setPageCount] = useState(3);
    const [organizations, setOrganizations] = useState([] as Organization[]);

    const loadData = () => {
        if (!authChecked) {
            setOrganizations([]);
            return;
        }

        http.requestAuthorized(`/organizations?\
order=${sortOrder}&\
column=${sortColumn}&\
per-page-number=${PER_PAGE_NUMBER}&\
page-count=${pageCount}&\
filter=${nameFilter}`, { method: "GET" })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }

                return response.json();
            })
            .then((data: Organization[]) => setOrganizations(data));
    };

    const handleSort = (column: string, order?: SortType) => {
        setSortOrder(order?.toString().toUpperCase() || "ASC");
        setSortColumn(column);
    };

    const handleScroll = (_x: number, y: number) => {
        const dataHeight = organizations.length * ROW_HEIGHT;
        const top = Math.abs(y);

        if (dataHeight - top - TABLE_HEIGHT < LOAD_MORE_DELTA && pageCount * PER_PAGE_NUMBER * ROW_HEIGHT <= dataHeight) {
            setPageCount(pageCount + 1);
        }
    };

    const handleFilter = (filter: string) => {
        setNameFilter(filter);
    };

    useEffect(() => setPageCount(3), []);
    useEffect(loadData, [authChecked, sortColumn, sortOrder, nameFilter, pageCount]);

    return <>
        <Container>
            <Header>
                <NavHeader />
            </Header>
            <Content style={{ padding: '2em' }}>
                <VStack alignItems="center" justifyContent="center" spacing={20}>
                    <Table
                        virtualized
                        height={TABLE_HEIGHT}
                        rowHeight={ROW_HEIGHT}
                        width={1370}
                        data={organizations}
                        sortColumn={sortColumn}
                        sortType={sortOrder.toLowerCase() as SortType}
                        onSortColumn={handleSort}
                        shouldUpdateScroll={false}
                        onScroll={handleScroll}
                        hover
                        bordered
                    >
                        <Column width={60} align="center" fixed fullText sortable>
                            <HeaderCell>Id</HeaderCell>
                            <Cell dataKey="id" />
                        </Column>

                        <Column width={150} fixed fullText sortable>
                            <HeaderCell>Name</HeaderCell>
                            <Cell dataKey="name" />
                        </Column>

                        <Column width={150} fixed fullText>
                            <HeaderCell>Coordinates</HeaderCell>
                            <Cell dataKey="coordinates">
                                {(rowData) => `(${rowData.coordinates.x}, ${rowData.coordinates.y})`}
                            </Cell>
                        </Column>

                        <Column width={160} fixed fullText sortable>
                            <HeaderCell>Creation Date</HeaderCell>
                            <Cell dataKey="creationDate">
                                {(rowData) => new Date(Date.parse(rowData.creationDate)).toLocaleString()}
                            </Cell>
                        </Column>

                        <Column width={150} fixed fullText sortable>
                            <HeaderCell>Annual Turnover</HeaderCell>
                            <Cell dataKey="annualTurnover" />
                        </Column>

                        <Column width={150} fixed fullText sortable>
                            <HeaderCell>Full Name</HeaderCell>
                            <Cell dataKey="fullName" />
                        </Column>

                        <Column width={150} fixed fullText sortable>
                            <HeaderCell>Employees Count</HeaderCell>
                            <Cell dataKey="employeesCount" />
                        </Column>

                        <Column width={150} fixed fullText sortable>
                            <HeaderCell>Type</HeaderCell>
                            <Cell dataKey="type" />
                        </Column>

                        <Column width={150} fixed fullText sortable>
                            <HeaderCell>Official Address</HeaderCell>
                            <Cell dataKey="officialAddress">
                                {(rowData) => `${rowData.officialAddress.street}, (${rowData.officialAddress.town.x},
                                    ${rowData.officialAddress.town.y}, ${rowData.officialAddress.town.z})`}
                            </Cell>
                        </Column>

                        <Column width={100} fixed align="center">
                            <HeaderCell> </HeaderCell>

                            <Cell style={{ padding: '6px' }}>
                                {(rowData) => {
                                    const userInfo = getUserInfo();

                                    if (rowData.userID == userInfo?.id
                                        || (userInfo?.role == "ROLE_ADMIN" && rowData.userID != 0)
                                        || userInfo?.role == "ROLE_SUPERADMIN"
                                    ) {
                                        return (
                                            <Button appearance="link" onClick={
                                                () => {
                                                    http.requestAuthorized(`/organizations/${rowData.id}`, {
                                                            method: "DELETE"
                                                        })
                                                        .then((response) => {
                                                            if (!response.ok) {
                                                                throw new Error(response.statusText);
                                                            }
                                                        })
                                                        .then(loadData);
                                                }
                                            }>
                                                Remove
                                            </Button>
                                        );
                                    } else {
                                        return (
                                            <Button appearance="link" disabled>
                                                Remove
                                            </Button>
                                        );
                                    }
                                }}
                            </Cell>
                        </Column>
                    </Table>
                    <HStack alignItems="center" justifyContent="center" spacing={15} style={{ width: '1370px' }}>
                        <Button href="/add">Add</Button>
                        <Input placeholder="Organization Name" onChange={handleFilter}/>
                    </HStack>
                </VStack>
            </Content>
        </Container>
    </>
};

export default ContentPage;