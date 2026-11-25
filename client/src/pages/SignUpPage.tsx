import {
    Container,
    Header,
    Content,
    Stack,
    Panel,
    Form,
    VStack,
    Button
} from 'rsuite';
import NavHeader from '../components/NavHeader';
import { isLoggedOut, registration } from '../utils/auth';
import * as http from "../utils/http";
import { useState } from 'react';
import { errorStyles } from '../utils/error';

const SignUpPage = () => {
    if (!isLoggedOut()) {
        http.redirect("/");

        return <></>;
    }

    const [formValue, setFormValue] = useState({
        username: "",
        password: "",
    });
    const [errorVisible, setErrorVisible] = useState(false);

    const onFormChange = (value: Record<string, string>) => {
        setFormValue({
            username: value.username,
            password: value.password,
        });
    };

    const onFormSubmit = () => {
        registration(formValue)
            .then(() => http.redirect("/signin"))
            .catch(() => setErrorVisible(true));
    };

    return <>
        <Container>
            <Header>
                <NavHeader />
            </Header>
            <Content style={{ padding: '15em' }}>
                <Stack alignItems="center" justifyContent="center" style={{ height: '100%' }}>
                    <Panel header="Sign up" bordered style={{ width: 400 }}>
                        <Form fluid onChange={onFormChange}>
                            <Form.Group>
                                <Form.ControlLabel>Username</Form.ControlLabel>
                                <Form.Control name="username" />
                            </Form.Group>

                            <Form.Group>
                                <Form.ControlLabel>Password</Form.ControlLabel>
                                <Form.Control name="password" autoComplete="off" />
                            </Form.Group>

                            <Form.Group>
                                <div style={errorStyles(errorVisible)}>The form is invalid</div>
                            </Form.Group>

                            <VStack spacing={10}>
                                <Button appearance="primary" block onClick={onFormSubmit}>
                                    Sign up
                                </Button>
                            </VStack>
                        </Form>
                    </Panel>
                </Stack>
            </Content>
        </Container>
    </>
};

export default SignUpPage;