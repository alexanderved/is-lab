import {
    Container, Header, Content, Panel, Button, Input, SelectPicker,
    Form, VStack, Message, Loader, Grid, Row, Col, Divider,
} from 'rsuite';
import NavHeader from '../components/NavHeader';
import { isLoggedOut } from '../utils/auth';
import * as http from '../utils/http';
import { useEffect, useState } from 'react';
import { fetchAll } from '../utils/shared';

const OperationsPage = () => {
    const [sum, setSum] = useState<number | null>(null);
    const [average, setAverage] = useState<number | null>(null);
    const [uniquePersons, setUniquePersons] = useState<any[]>([]);
    const [workers, setWorkers] = useState<any[]>([]);
    const [organizations, setOrganizations] = useState<any[]>([]);

    const [loadingStats, setLoadingStats] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [hireWorkerId, setHireWorkerId] = useState<number | null>(null);
    const [hireOrgId, setHireOrgId] = useState<number | null>(null);
    const [hireBusy, setHireBusy] = useState(false);
    const [hireResult, setHireResult] = useState<string | null>(null);

    const [indexWorkerId, setIndexWorkerId] = useState<number | null>(null);
    const [coefficient, setCoefficient] = useState<string>('1.0');
    const [indexBusy, setIndexBusy] = useState(false);
    const [indexResult, setIndexResult] = useState<string | null>(null);

    const refresh = async () => {
        setLoadingStats(true);
        setError(null);
        try {
            const [sumRes, avgRes, personsRes, workersData, orgsData] = await Promise.all([
                http.requestAuthorized('/operations/rating/sum', { method: 'GET' }),
                http.requestAuthorized('/operations/rating/average', { method: 'GET' }),
                http.requestAuthorized('/operations/unique-persons', { method: 'GET' }),
                fetchAll('workers'),
                fetchAll('organizations'),
            ]);

            if (sumRes.ok) setSum((await sumRes.json()).value);
            if (avgRes.ok) setAverage((await avgRes.json()).value);
            if (personsRes.ok) setUniquePersons(await personsRes.json());
            setWorkers(workersData);
            setOrganizations(orgsData);
        } catch (e: any) {
            setError(e?.message ?? 'Failed to load data');
        } finally {
            setLoadingStats(false);
        }
    };

    useEffect(() => { refresh(); }, []);

    if (isLoggedOut()) {
        http.redirect('/signin');
        return <></>;
    }

    const onHire = async () => {
        if (hireWorkerId == null || hireOrgId == null) {
            setHireResult('Select both a worker and an organization');
            return;
        }
        setHireBusy(true);
        setHireResult(null);
        try {
            const res = await http.requestAuthorized(
                `/operations/hire/${hireWorkerId}/${hireOrgId}`,
                { method: 'POST' },
            );
            if (!res.ok) {
                const body = await res.json().catch(() => ({}));
                throw new Error(body.message ?? `Error ${res.status}`);
            }
            setHireResult('Hired successfully');
            await refresh();
        } catch (e: any) {
            setHireResult(e.message);
        } finally {
            setHireBusy(false);
        }
    };

    const onIndexSalary = async () => {
        const coef = Number(coefficient);
        if (indexWorkerId == null) {
            setIndexResult('Select a worker');
            return;
        }
        if (!Number.isFinite(coef) || coef <= 0) {
            setIndexResult('Coefficient must be a positive number');
            return;
        }
        setIndexBusy(true);
        setIndexResult(null);
        try {
            const res = await http.requestAuthorized(
                `/operations/index-salary/${indexWorkerId}?coefficient=${coef}`,
                { method: 'POST' },
            );
            if (!res.ok) {
                const body = await res.json().catch(() => ({}));
                throw new Error(body.message ?? `Error ${res.status}`);
            }
            const updated = await res.json();
            setIndexResult(`New salary: ${updated.salary}`);
            await refresh();
        } catch (e: any) {
            setIndexResult(e.message);
        } finally {
            setIndexBusy(false);
        }
    };

    const workerOptions = workers.map((w) => ({
        label: `#${w.id} — ${w.name} (Salary: ${w.salary})`,
        value: w.id,
    }));

    const orgOptions = organizations.map((o) => ({
        label: `#${o.id} (${o.fullName || o.type})`,
        value: o.id,
    }));

    return (
        <Container>
            <Header><NavHeader /></Header>
            <Content style={{ padding: '2em' }}>
                <VStack spacing={20}>
                    <h3 style={{ margin: 0 }}>Special Operations</h3>

                    {error && <Message showIcon type="error">{error}</Message>}

                    <Panel header="Statistics" bordered style={{ width: '100%' }}>
                        {loadingStats ? (
                            <Loader center content="Loading..." />
                        ) : (
                            <Grid fluid>
                                <Row gutter={20}>
                                    <Col xs={8}>
                                        <Panel bordered bodyFill>
                                            <div style={{ padding: 16 }}>
                                                <div style={{ fontSize: 12, color: '#888' }}>
                                                    Sum of ratings
                                                </div>
                                                <div style={{ fontSize: 32, fontWeight: 600 }}>
                                                    {sum ?? '—'}
                                                </div>
                                            </div>
                                        </Panel>
                                    </Col>
                                    <Col xs={8}>
                                        <Panel bordered bodyFill>
                                            <div style={{ padding: 16 }}>
                                                <div style={{ fontSize: 12, color: '#888' }}>
                                                    Average rating
                                                </div>
                                                <div style={{ fontSize: 32, fontWeight: 600 }}>
                                                    {average != null ? average.toFixed(2) : '—'}
                                                </div>
                                            </div>
                                        </Panel>
                                    </Col>
                                    <Col xs={8}>
                                        <Panel bordered bodyFill>
                                            <div style={{ padding: 16 }}>
                                                <div style={{ fontSize: 12, color: '#888' }}>
                                                    Unique persons
                                                </div>
                                                <div style={{ fontSize: 32, fontWeight: 600 }}>
                                                    {uniquePersons.length}
                                                </div>
                                            </div>
                                        </Panel>
                                    </Col>
                                </Row>
                            </Grid>
                        )}
                    </Panel>

                    {uniquePersons.length > 0 && (
                        <Panel header={`Unique persons (${uniquePersons.length})`} bordered style={{ width: '100%' }}>
                            <VStack spacing={6} alignItems="stretch">
                                {uniquePersons.map((p) => (
                                    <div key={p.id} style={{ padding: '6px 0', borderBottom: '1px solid #eee' }}>
                                        <b>#{p.id}</b> — eyes {p.eyeColor}, hair {p.hairColor},
                                        {' '}location <b>{p.location?.name}</b>
                                        {p.nationality && `, ${p.nationality}`}
                                    </div>
                                ))}
                            </VStack>
                        </Panel>
                    )}

                    <Grid fluid style={{ width: '100%' }}>
                        <Row gutter={20}>
                            <Col xs={12}>
                                <Panel header="Hire worker into organization" bordered>
                                    <Form fluid>
                                        <Form.Group>
                                            <Form.ControlLabel>Worker</Form.ControlLabel>
                                            <SelectPicker
                                                data={workerOptions}
                                                value={hireWorkerId}
                                                onChange={setHireWorkerId}
                                                style={{ width: '100%' }}
                                                placeholder="Select worker"
                                            />
                                        </Form.Group>
                                        <Form.Group>
                                            <Form.ControlLabel>Organization</Form.ControlLabel>
                                            <SelectPicker
                                                data={orgOptions}
                                                value={hireOrgId}
                                                onChange={setHireOrgId}
                                                style={{ width: '100%' }}
                                                placeholder="Select organization"
                                            />
                                        </Form.Group>
                                        <Divider />
                                        <Button
                                            appearance="primary"
                                            onClick={onHire}
                                            loading={hireBusy}
                                            disabled={hireWorkerId == null || hireOrgId == null}
                                        >
                                            Hire
                                        </Button>
                                        {hireResult && (
                                            <Message
                                                style={{ marginTop: 12 }}
                                                type={hireResult.startsWith('Hired') ? 'success' : 'error'}
                                                showIcon
                                            >
                                                {hireResult}
                                            </Message>
                                        )}
                                    </Form>
                                </Panel>
                            </Col>

                            <Col xs={12}>
                                <Panel header="Index salary" bordered>
                                    <Form fluid>
                                        <Form.Group>
                                            <Form.ControlLabel>Worker</Form.ControlLabel>
                                            <SelectPicker
                                                data={workerOptions}
                                                value={indexWorkerId}
                                                onChange={setIndexWorkerId}
                                                style={{ width: '100%' }}
                                                placeholder="Select worker"
                                            />
                                        </Form.Group>
                                        <Form.Group>
                                            <Form.ControlLabel>Coefficient</Form.ControlLabel>
                                            <Input
                                                type="number"
                                                step="0.01"
                                                value={coefficient}
                                                onChange={setCoefficient}
                                                placeholder="e.g. 1.15 for +15%"
                                            />
                                        </Form.Group>
                                        <Divider />
                                        <Button
                                            appearance="primary"
                                            onClick={onIndexSalary}
                                            loading={indexBusy}
                                            disabled={indexWorkerId == null}
                                        >
                                            Apply
                                        </Button>
                                        {indexResult && (
                                            <Message
                                                style={{ marginTop: 12 }}
                                                type={indexResult.startsWith('New salary') ? 'success' : 'error'}
                                                showIcon
                                            >
                                                {indexResult}
                                            </Message>
                                        )}
                                    </Form>
                                </Panel>
                            </Col>
                        </Row>
                    </Grid>
                </VStack>
            </Content>
        </Container>
    );
};

export default OperationsPage;