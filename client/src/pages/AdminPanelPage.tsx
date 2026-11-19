import {
    Container,
    Header,
    Content,
    Button,
    Table,
    VStack,
    HStack,
    Input,
    Checkbox
} from 'rsuite';
import NavHeader from '../components/NavHeader';
import { useContext, useEffect, useState } from 'react';
import * as http from '../utils/http';
import { isAdmin } from '../utils/auth';
import type { UserInfo } from '../utils/user';
import AuthContext from '../AuthContext';
import type { ValueType } from 'rsuite/esm/Checkbox';

const { Column, HeaderCell, Cell } = Table;

const AdminPanelPage = () => {
    if (!isAdmin()) {
        http.redirect("/");

        return <></>;
    }
    
    const authChecked = useContext(AuthContext);
    const [users, setUsers] = useState([] as UserInfo[]);
    const [filterName, setFilterName] = useState("");
    const [onlyAdmins, setOnlyAdmins] = useState(false);

    const handleFilter = (filter: string) => {
        setFilterName(filter);
    };

    const onCheck = (_value: ValueType | undefined, checked: boolean) => {
        setOnlyAdmins(checked);
    }

    useEffect(() => {
        if (!authChecked) {
            setUsers([]);
            return;
        }

        http.requestAuthorized(`/users`, { method: "GET" })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }

                return response.json();
            })
            .then((data: UserInfo[]) =>
                data.filter((u) => !onlyAdmins || u.role == "ROLE_ADMIN" || u.role == "ROLE_SUPERADMIN"))
            .then((data: UserInfo[]) => data.filter((u) => u.username.includes(filterName)))
            .then((data: UserInfo[]) => setUsers(data));
    }, [authChecked, filterName, onlyAdmins]);

    return <>
        <Container>
            <Header>
                <NavHeader />
            </Header>
            <Content style={{ padding: '2em' }}>
                <VStack alignItems="center" justifyContent="center" spacing={20}>
                    <Table
                        height={800}
                        width={600}
                        data={users}
                        bordered
                    >
                        <Column width={600}>
                            <HeaderCell><strong>Users</strong></HeaderCell>

                            <Cell style={{ padding: '0px' }}>
                            {rowData => (
                                <Button block href={`/users/${rowData.username}`} style={{ height: '90%' }}>
                                    {rowData.firstName} {rowData.secondName} ({rowData.username})
                                </Button>
                            )}
                            </Cell>
                        </Column>
                    </Table>
                    <HStack alignItems="center" justifyContent="center" spacing={15} style={{ width: '600px' }}>
                        <Checkbox onChange={onCheck}>Only Admins</Checkbox>
                        <Input placeholder="User Name" onChange={handleFilter}/>
                    </HStack>
                </VStack>
            </Content>
        </Container>
    </>
};

export default AdminPanelPage;