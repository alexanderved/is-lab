import {
    Container, Header, Content, Panel, VStack, HStack,
    Button, Message, Table, Loader, Tag, Divider, Uploader,
} from 'rsuite';
import NavHeader from '../components/NavHeader';
import { isLoggedOut, getUserInfo } from '../utils/auth';
import * as http from '../utils/http';
import { useCallback, useEffect, useState } from 'react';
import {
    uploadImport, fetchImportHistory, previewImportFile,
    type ImportOperation, type ImportStatus, type FilePreview,
} from '../utils/imports';
import { onUpdate, startUpdates, stopUpdates } from '../utils/updates-ws';

const { Column, HeaderCell, Cell } = Table;

const statusTag = (status: ImportStatus) => {
    switch (status) {
        case 'SUCCESS': return <Tag color="green">SUCCESS</Tag>;
        case 'FAILED':  return <Tag color="red">FAILED</Tag>;
    }
};

const formatDate = (iso: string) =>
    new Date(iso).toLocaleString(undefined, {
        year: 'numeric', month: '2-digit', day: '2-digit',
        hour: '2-digit', minute: '2-digit', second: '2-digit',
    });

const ImportPage = () => {
    const [fileList, setFileList] = useState<any[]>([]);
    const [preview, setPreview] = useState<FilePreview | null>(null);
    const [previewError, setPreviewError] = useState<string | null>(null);

    const [uploading, setUploading] = useState(false);
    const [uploadResult, setUploadResult] = useState<
        { type: 'success' | 'error'; message: string } | null
    >(null);

    const [history, setHistory] = useState<ImportOperation[]>([]);
    const [historyLoading, setHistoryLoading] = useState(true);
    const [historyError, setHistoryError] = useState<string | null>(null);

    const isAdmin = (() => {
        const role = getUserInfo()?.role;
        return role === 'ROLE_ADMIN' || role === 'ROLE_SUPERADMIN';
    })();

    const loadHistory = useCallback((silent = false) => {
        if (!silent) setHistoryLoading(true);
        setHistoryError(null);
        fetchImportHistory()
            .then(setHistory)
            .catch((e) => setHistoryError(e.message))
            .finally(() => { if (!silent) setHistoryLoading(false); });
    }, []);

    useEffect(() => {
        startUpdates();
        const unsub = onUpdate(() => loadHistory(true));   // silent = без спиннера
        return () => {
            unsub();
            stopUpdates();
        };
    }, [loadHistory]);

    useEffect(() => { loadHistory(); }, [loadHistory]);

    const handleFileChange = async (list: any[]) => {
        setFileList(list);
        setPreview(null);
        setPreviewError(null);
        setUploadResult(null);

        if (list.length === 0 || !list[0].blobFile) return;

        try {
            const p = await previewImportFile(list[0].blobFile);
            setPreview(p);
        } catch (e: any) {
            setPreviewError(e.message);
        }
    };

    const handleSubmit = async () => {
        setUploadResult(null);
        if (!preview) {
            setUploadResult({ type: 'error', message: 'Выберите корректный файл' });
            return;
        }

        setUploading(true);
        try {
            const result = await uploadImport(fileList[0].blobFile);
            setUploadResult({
                type: 'success',
                message: `Импорт успешен: добавлено ${result.objectsCount} ${
                    result.objectsCount === 1 ? 'объект' : 'объектов'
                } (операция #${result.id})`,
            });
            setFileList([]);
            setPreview(null);
            loadHistory();   // на случай, если WS не успеет
        } catch (e: any) {
            setUploadResult({ type: 'error', message: e.message });
            loadHistory(true);   // FAILED-запись уже создана на сервере
        } finally {
            setUploading(false);
        }
    };

    const handleClear = () => {
        setFileList([]);
        setPreview(null);
        setPreviewError(null);
        setUploadResult(null);
    };

    if (isLoggedOut()) {
        http.redirect('/signin');
        return <></>;
    }

    return (
        <Container>
            <Header><NavHeader /></Header>
            <Content style={{ padding: '2em' }}>
                <VStack alignItems="center" spacing={20}>
                    <h3 style={{ margin: 0 }}>Object import</h3>

                    <Panel bordered style={{ width: 700 }}>
                        <VStack spacing={15} alignItems="stretch">
                            <div>
                                <div style={{ marginBottom: 8, fontWeight: 500 }}>
                                    JSON file
                                </div>
                                <Uploader
                                    fileList={fileList}
                                    onChange={handleFileChange}
                                    autoUpload={false}
                                    draggable
                                    multiple={false}
                                    accept=".json,application/json"
                                    disabled={uploading}
                                    action={""}
                                >
                                    <div style={{ padding: 40, textAlign: 'center', color: '#888' }}>
                                        Drag and drop the file or click to choose
                                    </div>
                                </Uploader>
                            </div>

                            {previewError && (
                                <Message type="error" showIcon closable
                                         onClose={() => setPreviewError(null)}>
                                    {previewError}
                                </Message>
                            )}

                            <HStack spacing={10}>
                                <Button
                                    appearance="primary"
                                    onClick={handleSubmit}
                                    loading={uploading}
                                    disabled={!preview || uploading}
                                >
                                    Import
                                </Button>
                                <Button appearance="default" onClick={handleClear}
                                        disabled={uploading}>
                                    Clear
                                </Button>
                            </HStack>

                            {uploadResult && (
                                <Message
                                    type={uploadResult.type === 'success' ? 'success' : 'error'}
                                    showIcon
                                    closable
                                    onClose={() => setUploadResult(null)}
                                >
                                    {uploadResult.message}
                                </Message>
                            )}
                        </VStack>
                    </Panel>

                    <Divider>
                        Import history {isAdmin ? '(all users)' : ''}
                    </Divider>

                    <VStack spacing={12} style={{ width: 500 }}>
                        {historyError && (
                            <Message type="error" showIcon>{historyError}</Message>
                        )}

                        {historyLoading ? (
                            <Loader center content="Loading history..." />
                        ) : (
                            <Table
                                data={history}
                                height={500}
                                width={500}
                                hover
                                bordered
                                rowKey="id"
                                locale={{ emptyMessage: 'No operations were performed yet' }}
                            >
                                <Column width={80} align="center">
                                    <HeaderCell>ID</HeaderCell>
                                    <Cell dataKey="id" />
                                </Column>

                                <Column width={140}>
                                    <HeaderCell>Status</HeaderCell>
                                    <Cell>
                                        {(row: ImportOperation) => statusTag(row.status)}
                                    </Cell>
                                </Column>

                                <Column width={160} align="center">
                                    <HeaderCell>Objects added</HeaderCell>
                                    <Cell>
                                        {(row: ImportOperation) =>
                                            row.status === 'SUCCESS' && row.objectsCount != null
                                                ? row.objectsCount
                                                : '-'
                                        }
                                    </Cell>
                                </Column>

                                <Column width={120} align="center">
                                    <HeaderCell>User</HeaderCell>
                                    <Cell>
                                        {(row: ImportOperation) => row.userId || row.userId === 0 ? `#${row.userId}` : '-'}
                                    </Cell>
                                </Column>
                            </Table>
                        )}
                    </VStack>
                </VStack>
            </Content>
        </Container>
    );
};

export default ImportPage;