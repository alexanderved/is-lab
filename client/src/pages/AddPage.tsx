import { Container, Header, Content, Stack, Panel, Form, VStack, Button, Message } from 'rsuite';
import NavHeader from '../components/NavHeader';
import { isLoggedOut } from '../utils/auth';
import * as http from '../utils/http';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { FieldSet } from './SharedForm';
import {
    ADD_TITLES,
    ENDPOINTS,
    buildBody,
    validate,
    fetchAll,
    type AddType,
    type FieldErrors,
} from '../utils/shared';

const AddPage = () => {
    const params = useParams<{ type?: string; tab?: string }>();
    const raw = params.type ?? params.tab;
    const addType: AddType | null =
        raw === 'worker' || raw === 'organization' || raw === 'person' ? (raw as AddType) : null;

    const [formValue, setFormValue] = useState<Record<string, any>>({});
    const [errors, setErrors] = useState<FieldErrors>({});
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [persons, setPersons] = useState<any[]>([]);
    const [organizations, setOrganizations] = useState<any[]>([]);

    useEffect(() => {
        if (addType === 'worker') {
            fetchAll('persons').then(setPersons);
            fetchAll('organizations').then(setOrganizations);
        }
    }, [addType]);

    if (isLoggedOut()) {
        http.redirect('/signin');
        return <></>;
    }

    if (!addType) {
        return (
            <Container>
                <Header><NavHeader /></Header>
                <Content style={{ padding: '2em' }}>
                    <Message showIcon type="warning">Unknown type: {raw ?? '(none)'}</Message>
                </Content>
            </Container>
        );
    }

    const onFormChange = (value: Record<string, any>) => {
        setFormValue(value);
        if (Object.keys(errors).length > 0) setErrors({});
    };

    const onSubmit = () => {
        setSubmitError(null);
        const errs = validate(addType, formValue);
        if (Object.keys(errs).length > 0) {
            setErrors(errs);
            return;
        }
        setErrors({});

        http.requestAuthorized(`/${ENDPOINTS[addType]}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(buildBody[addType](formValue)),
        })
            .then(async (r) => {
                if (!r.ok) {
                    let msg = `Error ${r.status}`;
                    try {
                        const data = await r.json();
                        if (data?.message) msg = data.message;
                    } catch { /* */ }
                    throw new Error(msg);
                }
            })
            .then(() => http.redirect('/'))
            .catch((e) => setSubmitError(e.message || 'Failed to create'));
    };

    return (
        <Container>
            <Header><NavHeader /></Header>
            <Content style={{ padding: '2em' }}>
                <Stack alignItems="center" justifyContent="center" style={{ height: '100%' }}>
                    <Panel header={ADD_TITLES[addType]} bordered style={{ width: 620 }}>
                        <Form fluid onChange={onFormChange}>
                            <FieldSet
                                addType={addType}
                                persons={persons}
                                organizations={organizations}
                                errors={errors}
                            />
                            {submitError && (
                                <Form.Group>
                                    <Message showIcon type="error">{submitError}</Message>
                                </Form.Group>
                            )}
                            <VStack spacing={10}>
                                <Button appearance="primary" block onClick={onSubmit}>Add</Button>
                            </VStack>
                        </Form>
                    </Panel>
                </Stack>
            </Content>
        </Container>
    );
};

export default AddPage;