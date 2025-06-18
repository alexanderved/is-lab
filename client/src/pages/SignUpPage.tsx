import {
    Container,
    Header,
    Content,
    Stack,
    Panel,
    Form,
    VStack,
    Button,
    Uploader,
    Loader,
    Avatar
} from 'rsuite';
import NavHeader from '../components/NavHeader';
import { isLoggedOut, registration } from '../utils/auth';
import * as http from "../utils/http";
import { useState } from 'react';
import { upload, type UploadRequest } from '../utils/file';
import { errorStyles } from '../utils/error';

const SignUpPage = () => {
    if (!isLoggedOut()) {
        http.redirect("/");

        return <></>;
    }

    const [formValue, setFormValue] = useState({
        username: "",
        email: "",
        firstName: "",
        secondName: "",
        password: "",
    });
    const [uploading, setUploading] = useState(false);
    const [fileInfo, setFileInfo] = useState(null as string | null);
    const [errorVisible, setErrorVisible] = useState(false);

    const onFormChange = (value: Record<string, string>) => {
        setFormValue({
            username: value.username,
            email: value.email,
            firstName: value.firstName,
            secondName: value.secondName,
            password: value.password,
        });
    };

    const onFormSubmit = () => {
        registration(formValue)
            .then(() => {
                if (fileInfo != null) {
                    upload({ dataURL: fileInfo, filename: formValue.username } as UploadRequest);
                }
            })
            .then(() => http.redirect("/signin"))
            .catch(() => setErrorVisible(true));
    };

    const previewFile = (file: File | undefined, callback: (value: string | null) => void) => {
        const reader = new FileReader();
        reader.onloadend = () => {
            if (reader.result instanceof ArrayBuffer) {
                callback(Buffer.from(reader.result).toString());
            } else {
                callback(reader.result);
            }
        };

        if (file != undefined) {
            reader.readAsDataURL(file);
        }
    }

    return <>
        <Container>
            <Header>
                <NavHeader />
            </Header>
            <Content style={{ padding: '6em' }}>
                <Stack alignItems="center" justifyContent="center" style={{ height: '100%' }}>
                    <Panel header="Sign up" bordered style={{ width: 400 }}>
                        <Form fluid onChange={onFormChange}>
                            <Uploader
                                fileListVisible={false}
                                listType="picture"
                                action="//jsonplaceholder.typicode.com/posts/"
                                onUpload={file => {
                                    setUploading(true);
                                    previewFile(file.blobFile, value => {
                                        setFileInfo(value);
                                    });
                                }}
                                onSuccess={(_response, _file) => {
                                    setUploading(false);
                                }}
                                onError={() => {
                                    setFileInfo(null);
                                    setUploading(false);
                                }}
                            >
                                <button style={{ width: 150, height: 150 }}>
                                    {uploading && <Loader backdrop center />}
                                    {fileInfo ? (<img src={fileInfo} width="100%" height="100%" />)
                                              : (<Avatar style={{ fontSize: 80 }} />)}
                                </button>
                            </Uploader>

                            <Form.Group>
                                <Form.ControlLabel>Username</Form.ControlLabel>
                                <Form.Control name="username" />
                            </Form.Group>

                            <Form.Group>
                                <Form.ControlLabel>Email</Form.ControlLabel>
                                <Form.Control name="email" />
                            </Form.Group>

                            <Form.Group>
                                <Form.ControlLabel>First Name</Form.ControlLabel>
                                <Form.Control name="firstName" />
                            </Form.Group>

                            <Form.Group>
                                <Form.ControlLabel>Second Name</Form.ControlLabel>
                                <Form.Control name="secondName" />
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