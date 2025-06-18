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
import { isLoggedOut } from '../utils/auth';
import * as http from "../utils/http";
import { useState } from 'react';
import type { Address, Coordinates } from './ContentPage';
import { errorStyles } from '../utils/error';

type OrganizationTemplate = {
    name: string,
    coordinates: Coordinates,
    annualTurnover: number,
    fullName: string,
    employeesCount: number,
    type: string,
    officialAddress: Address,
};

const AddPage = () => {
    if (isLoggedOut()) {
        http.redirect("/signin");

        return <></>;
    }

    const [formValue, setFormValue] = useState(null as OrganizationTemplate | null);
    const [errorVisible, setErrorVisible] = useState(false);

    const onFormChange = (value: Record<string, any>) => {
        setFormValue({
            name: value.name,
            coordinates: {
                x: value.cx,
                y: value.cy,
            },
            annualTurnover: value.annualTurnover,
            fullName: value.fullName,
            employeesCount: value.employeesCount,
            type: value.type,
            officialAddress: {
                street: value.street,
                town: {
                    x: value.tx,
                    y: value.ty,
                    z: value.tz,
                }
            },
        });
    };

    const onFormSubmit = () => {
        if (formValue == null) {
            setErrorVisible(true);

            return;
        }

        http.requestAuthorized(`/organizations`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formValue),
            })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }
            })
            .then(() => {
                http.redirect("/");
            })
            .catch(() => setErrorVisible(true));
    };

    return <>
        <Container>
            <Header>
                <NavHeader />
            </Header>
            <Content style={{ padding: '2em' }}>
                <Stack alignItems="center" justifyContent="center" style={{ height: '100%' }}>
                    <Panel header="Add" bordered style={{ width: 500 }}>
                        <Form fluid onChange={onFormChange}>
                            <Form.Group>
                                <Form.ControlLabel>Name</Form.ControlLabel>
                                <Form.Control name="name" />
                            </Form.Group>

                            <Form.Group>
                                <VStack spacing={10}>
                                    <Form.ControlLabel>Coordinates</Form.ControlLabel>
                                    <Form.Control name="cx" />
                                    <Form.Control name="cy" />
                                </VStack>
                            </Form.Group>

                            <Form.Group>
                                <Form.ControlLabel>Annual Turnover</Form.ControlLabel>
                                <Form.Control name="annualTurnover" />
                            </Form.Group>

                            <Form.Group>
                                <Form.ControlLabel>Full Name</Form.ControlLabel>
                                <Form.Control name="fullName" />
                            </Form.Group>

                            <Form.Group>
                                <Form.ControlLabel>Employees Count</Form.ControlLabel>
                                <Form.Control name="employeesCount" />
                            </Form.Group>

                            <Form.Group>
                                <Form.ControlLabel>Type</Form.ControlLabel>
                                <Form.Control name="type" />
                            </Form.Group>

                            <Form.Group>
                                <Form.ControlLabel>Street</Form.ControlLabel>
                                <Form.Control name="street" />
                            </Form.Group>

                            <Form.Group>
                                <VStack spacing={10}>
                                    <Form.ControlLabel>Town</Form.ControlLabel>
                                    <Form.Control name="tx" />
                                    <Form.Control name="ty" />
                                    <Form.Control name="tz" />
                                </VStack>
                            </Form.Group>

                            <Form.Group>
                                <div style={errorStyles(errorVisible)}>The form is invalid</div>
                            </Form.Group>

                            <VStack spacing={10}>
                                <Button appearance="primary" block onClick={onFormSubmit}>
                                    Add
                                </Button>
                            </VStack>
                        </Form>
                    </Panel>
                </Stack>
            </Content>
        </Container>
    </>
};

export default AddPage;