import { Container, Header, Content, Stack, Panel, Form, VStack, Button, Message, Loader } from 'rsuite';
import NavHeader from '../components/NavHeader';
import { isLoggedOut } from '../utils/auth';
import * as http from '../utils/http';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { FieldSet } from './SharedForm';
import {
    UPDATE_TITLES,
    ENDPOINTS,
    buildBody,
    validate,
    fetchAll,
    fetchById,
    fromDomain,
    type AddType,
    type FieldErrors,
} from '../utils/shared';

const UpdatePage = () => {
    const params = useParams<{ type?: string; id?: string }>();
    const raw = params.type;
    const addType: AddType | null =
        raw === 'worker' || raw === 'organization' || raw === 'person' ? (raw as AddType) : null;
    const id = params.id != null ? Number(params.id) : null;

    const [formValue, setFormValue] = useState<Record<string, any>>({});
    const [errors, setErrors] = useState<FieldErrors>({});
    const [submitError, setSubmitError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [persons, setPersons] = useState<any[]>([]);
    const [organizations, setOrganizations] = useState<any[]>([]);

    useEffect(() => {
        if (!addType || id == null) {
            setLoading(false);
            return;
        }
        setLoading(true);
        setSubmitError(null);

        const loadEntity = fetchById(ENDPOINTS[addType], id).then((entity) => {
            if (!entity) setSubmitError(`Entity #${id} not found`);
            else setFormValue(fromDomain[addType](entity));
        });

        const loadDeps =
            addType === 'worker'
                ? Promise.all([fetchAll('persons'), fetchAll('organizations')]).then(([p, o]) => {
                    setPersons(p);
                    setOrganizations(o);
                })
                : Promise.resolve();

        Promise.all([loadEntity, loadDeps])
            .catch(() => setSubmitError('Failed to load'))
            .finally(() => setLoading(false));
    }, [addType, id]);

    if (isLoggedOut()) {
        http.redirect('/signin');
        return <></>;
    }

    if (!addType || id == null) {
        return (
            <Container>
                <Header><NavHeader /></Header>
                <Content style={{ padding: '2em' }}>
                    <Message showIcon type="warning">Invalid update URL</Message>
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

        http.requestAuthorized(`/${ENDPOINTS[addType]}/${id}`, {
            method: 'PUT',
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
            .catch((e) => setSubmitError(e.message || 'Failed to update'));
    };

    return (
        <Container>
            <Header><NavHeader /></Header>
            <Content style={{ padding: '2em' }}>
                <Stack alignItems="center" justifyContent="center" style={{ height: '100%' }}>
                    <Panel header={UPDATE_TITLES[addType]} bordered style={{ width: 620 }}>
                        {loading ? (
                            <Loader center content="Loading..." />
                        ) : (
                            <Form fluid formValue={formValue} onChange={onFormChange}>
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
                                    <Button appearance="primary" block onClick={onSubmit}>Save</Button>
                                </VStack>
                            </Form>
                        )}
                    </Panel>
                </Stack>
            </Content>
        </Container>
    );
};

export default UpdatePage;