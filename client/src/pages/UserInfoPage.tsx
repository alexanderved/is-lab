import {
    Container,
    Header,
    Content,
    VStack,
    Panel,
    Form,
    HStack,
    Button
} from 'rsuite';
import NavHeader from '../components/NavHeader';
import { useContext, useEffect, useState } from 'react';
import { useParams } from "react-router-dom";
import { findByName, type UserInfo } from '../utils/user';
import { getUserInfo } from '../utils/auth';
import AuthContext from '../AuthContext';
import * as http from '../utils/http';

const UserInfoPage = () => {
    const authChecked = useContext(AuthContext);
    const { username } = useParams()
    const [formValue, setFormValue] = useState({
        id: 0,
        username: "",
        role: "",
    } as UserInfo);

    const blockUser = () => {
        if (!authChecked) {
            return;
        }
        
        http.requestAuthorized(`/admin/block/${formValue.id}`, { method: "POST" })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }

                return response.json();
            })
            .then(async () => {
                await findByName(formValue.username)
                    .then((userInfo) => {
                        if (userInfo != null) {
                            setFormValue(userInfo);
                        }
                    });
            });
    };

    const assignUser = () => {
        if (!authChecked) {
            return;
        }
        
        http.requestAuthorized(`/admin/appoint/${formValue.id}`, { method: "POST" })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }

                return response.json();
            })
            .then(() => {
                findByName(formValue.username)
                    .then((userInfo) => {
                        if (userInfo != null) {
                            setFormValue(userInfo);
                        }
                    });
            });
    };

    const AdminButtons = () => {
        if (!authChecked) {
            return <></>;
        }

        const selfInfo = getUserInfo();
        if (selfInfo?.role == "ROLE_BLOCKED" || selfInfo?.role == "ROLE_USER") {
            return <></>;
        }

        let disabledButtons = selfInfo?.id == formValue.id;
        if (selfInfo?.role == "ROLE_ADMIN") {
            disabledButtons = disabledButtons || formValue.role == "ROLE_SUPERADMIN";
            return (<>
                <Button onClick={blockUser} disabled={disabledButtons}>
                    {formValue.role == "ROLE_BLOCKED" ? "Unblock" : "Block"}
                </Button>
            </>);
        }

        if (selfInfo?.role == "ROLE_SUPERADMIN") {
            if (formValue.role == "ROLE_BLOCKED") {
                return (<>
                    <Button onClick={blockUser} disabled={disabledButtons}>
                        Unblock
                    </Button>
                </>);
            }

            return (<>
                <Button onClick={blockUser} disabled={disabledButtons}>
                    Block
                </Button>
                <Button onClick={assignUser} disabled={disabledButtons}>
                    {formValue.role == "ROLE_ADMIN" ? "Unassign" : "Assign"}
                </Button>
            </>);
        }
    };

    useEffect(() => {
        if (username == undefined) {
            http.redirect("/");
            return;
        }

        findByName(username)
            .then((userInfo) => {
                if (userInfo != null) {
                    setFormValue(userInfo);
                }
            });
    }, [authChecked]);

    return <>
        <Container>
            <Header>
                <NavHeader />
            </Header>
            <Content style={{ padding: '18em' }}>
                <VStack alignItems="center" justifyContent="center" style={{ height: '100%' }} spacing={20}>
                    <Panel bordered style={{ width: 400 }}>
                        <Form fluid formValue={formValue} readOnly>
                            <Form.Group>
                                <Form.ControlLabel>Username</Form.ControlLabel>
                                <Form.Control name="username" />
                            </Form.Group>

                            <Form.Group>
                                <Form.ControlLabel>Role</Form.ControlLabel>
                                <Form.Control name="role" />
                            </Form.Group>
                        </Form>
                    </Panel>
                    <HStack alignItems="center" justifyContent="center">
                        <AdminButtons />
                    </HStack>
                </VStack>
            </Content>
        </Container>
    </>
};

export default UserInfoPage;