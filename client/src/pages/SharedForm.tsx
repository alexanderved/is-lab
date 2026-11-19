import { Form, SelectPicker, VStack } from 'rsuite';
import type { ReactNode } from 'react';
import * as shared from '../utils/shared';

type Errors = shared.FieldErrors;
type Ctx = { errors: Errors };

const L = ({ children, required }: { children: ReactNode; required?: boolean }) => (
    <Form.ControlLabel>
        {children}
        {required && <span style={{ color: '#d32f2f', marginLeft: 4 }}>*</span>}
    </Form.ControlLabel>
);

const WorkerFields = ({
                          persons,
                          organizations,
                          errors,
                      }: { persons: any[]; organizations: any[] } & Ctx) => (
    <>
        <Form.Group>
            <L required>Name</L>
            <Form.Control name="name" errorMessage={errors.name} />
        </Form.Group>

        <Form.Group>
            <L required>Coordinates</L>
            <VStack spacing={8}>
                <Form.Control name="cx" type="number" step="1" placeholder="X (integer)"
                              errorMessage={errors.cx} />
                <Form.Control name="cy" type="number" step="any" placeholder="Y (float)"
                              errorMessage={errors.cy} />
            </VStack>
        </Form.Group>

        <Form.Group>
            <L>Organization</L>
            <Form.Control
                name="organizationId"
                accepter={SelectPicker}
                data={organizations.map((o) => ({
                    label: `#${o.id} (${o.fullName || o.type})`,
                    value: o.id,
                }))}
                style={{ width: '100%' }}
                cleanable
                errorMessage={errors.organizationId}
            />
        </Form.Group>

        <Form.Group>
            <L required>Salary</L>
            <Form.Control name="salary" type="number" step="1"
                          placeholder="Positive integer"
                          errorMessage={errors.salary} />
        </Form.Group>

        <Form.Group>
            <L>Rating</L>
            <Form.Control name="rating" type="number" step="1"
                          placeholder="Positive integer"
                          errorMessage={errors.rating} />
        </Form.Group>

        <Form.Group>
            <L>Position</L>
            <Form.Control name="position" accepter={SelectPicker}
                          data={shared.enumData(shared.POSITIONS)}
                          style={{ width: '100%' }} cleanable
                          errorMessage={errors.position} />
        </Form.Group>

        <Form.Group>
            <L>Status</L>
            <Form.Control name="status" accepter={SelectPicker}
                          data={shared.enumData(shared.STATUSES)}
                          style={{ width: '100%' }} cleanable
                          errorMessage={errors.status} />
        </Form.Group>

        <Form.Group>
            <L required>Person</L>
            <Form.Control
                name="personId"
                accepter={SelectPicker}
                data={persons.map((p) => ({
                    label: `#${p.id} (${p.location?.name ?? '-'})`,
                    value: p.id,
                }))}
                style={{ width: '100%' }}
                errorMessage={errors.personId}
            />
        </Form.Group>
    </>
);

const OrganizationFields = ({ errors }: Ctx) => (
    <>
        <Form.Group>
            <L required>Annual Turnover</L>
            <Form.Control name="annualTurnover" type="number" step="1"
                          placeholder="Positive integer"
                          errorMessage={errors.annualTurnover} />
        </Form.Group>

        <Form.Group>
            <L required>Employees Count</L>
            <Form.Control name="employeesCount" type="number" step="1"
                          placeholder="Positive integer"
                          errorMessage={errors.employeesCount} />
        </Form.Group>

        <Form.Group>
            <L>Full Name</L>
            <Form.Control name="fullName" errorMessage={errors.fullName} />
        </Form.Group>

        <Form.Group>
            <L>Rating</L>
            <Form.Control name="rating" type="number" step="any"
                          placeholder="Positive number"
                          errorMessage={errors.rating} />
        </Form.Group>

        <Form.Group>
            <L required>Type</L>
            <Form.Control name="type" accepter={SelectPicker}
                          data={shared.enumData(shared.ORGANIZATION_TYPES)}
                          style={{ width: '100%' }}
                          errorMessage={errors.type} />
        </Form.Group>

        <h5 style={{ marginTop: 20 }}>Official Address</h5>
        <Form.Group>
            <L>Street</L>
            <Form.Control name="offStreet" placeholder="Max 112 characters"
                          errorMessage={errors.offStreet} />
        </Form.Group>
        <Form.Group>
            <L>Town</L>
            <VStack spacing={8}>
                <Form.Control name="offTx" type="number" step="1" placeholder="X (integer)"
                              errorMessage={errors.offTx} />
                <Form.Control name="offTy" type="number" step="1" placeholder="Y (integer)"
                              errorMessage={errors.offTy} />
                <Form.Control name="offTz" type="number" step="any" placeholder="Z (double)"
                              errorMessage={errors.offTz} />
                <Form.Control name="offTname" placeholder="Name"
                              errorMessage={errors.offTname} />
            </VStack>
        </Form.Group>

        <h5 style={{ marginTop: 20 }}>Postal Address</h5>
        <Form.Group>
            <L>Street</L>
            <Form.Control name="postStreet" placeholder="Max 112 characters"
                          errorMessage={errors.postStreet} />
        </Form.Group>
        <Form.Group>
            <L>Town</L>
            <VStack spacing={8}>
                <Form.Control name="postTx" type="number" step="1" placeholder="X (integer)"
                              errorMessage={errors.postTx} />
                <Form.Control name="postTy" type="number" step="1" placeholder="Y (integer)"
                              errorMessage={errors.postTy} />
                <Form.Control name="postTz" type="number" step="any" placeholder="Z (double)"
                              errorMessage={errors.postTz} />
                <Form.Control name="postTname" placeholder="Name"
                              errorMessage={errors.postTname} />
            </VStack>
        </Form.Group>
    </>
);

const PersonFields = ({ errors }: Ctx) => (
    <>
        <Form.Group>
            <L required>Eye Color</L>
            <Form.Control name="eyeColor" accepter={SelectPicker}
                          data={shared.enumData(shared.COLORS)}
                          style={{ width: '100%' }}
                          errorMessage={errors.eyeColor} />
        </Form.Group>

        <Form.Group>
            <L required>Hair Color</L>
            <Form.Control name="hairColor" accepter={SelectPicker}
                          data={shared.enumData(shared.COLORS)}
                          style={{ width: '100%' }}
                          errorMessage={errors.hairColor} />
        </Form.Group>

        <Form.Group>
            <L required>Location</L>
            <VStack spacing={8}>
                <Form.Control name="lx" type="number" step="1" placeholder="X (integer)"
                              errorMessage={errors.lx} />
                <Form.Control name="ly" type="number" step="1" placeholder="Y (integer)"
                              errorMessage={errors.ly} />
                <Form.Control name="lz" type="number" step="any" placeholder="Z (double)"
                              errorMessage={errors.lz} />
                <Form.Control name="lname" placeholder="Name"
                              errorMessage={errors.lname} />
            </VStack>
        </Form.Group>

        <Form.Group>
            <L>Birthday</L>
            <Form.Control name="birthday" type="date" errorMessage={errors.birthday} />
        </Form.Group>

        <Form.Group>
            <L required>Weight</L>
            <Form.Control name="weight" type="number" step="1"
                          placeholder="Positive integer"
                          errorMessage={errors.weight} />
        </Form.Group>

        <Form.Group>
            <L>Nationality</L>
            <Form.Control name="nationality" accepter={SelectPicker}
                          data={shared.enumData(shared.COUNTRIES)}
                          style={{ width: '100%' }} cleanable
                          errorMessage={errors.nationality} />
        </Form.Group>
    </>
);

export const FieldSet = ({
                             addType,
                             persons,
                             organizations,
                             errors,
                         }: {
    addType: shared.AddType;
    persons: any[];
    organizations: any[];
    errors: shared.FieldErrors;
}) => {
    if (addType === 'worker') return <WorkerFields persons={persons} organizations={organizations} errors={errors} />;
    if (addType === 'organization') return <OrganizationFields errors={errors} />;
    return <PersonFields errors={errors} />;
};