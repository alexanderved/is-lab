import AdminPanelPage from './pages/AdminPanelPage.tsx';
import { Routes, Route } from 'react-router-dom';
import './App.css'
import ContentPage from './pages/ContentPage.tsx';
import SignInPage from './pages/SignInPage.tsx';
import SignUpPage from './pages/SignUpPage.tsx';
import { useEffect, useState } from 'react';
import { updateUserInfo } from './utils/auth.ts';
import AuthContext from './AuthContext.tsx';
import UserInfoPage from './pages/UserInfoPage.tsx';
import AddPage from './pages/AddPage.tsx';
import UpdatePage from './pages/UpdatePage.tsx';
import OperationsPage from "./pages/OperationsPage.tsx";

const App = () => {
    const [authChecked, setAuthChecked] = useState(false);

    useEffect(() => {
        setAuthChecked(false);
        updateUserInfo()
            .then(() => {
                setAuthChecked(() => true);
            });
    }, []);

    return (
        <>
            <AuthContext.Provider value={authChecked}>
                <Routes>
                    <Route path='/' Component={ContentPage}></Route>
                    <Route path='/admin' Component={AdminPanelPage}></Route>
                    <Route path='/signin' Component={SignInPage}></Route>
                    <Route path='/signup' Component={SignUpPage}></Route>
                    <Route path='/users/:username' Component={UserInfoPage}></Route>
                    <Route path='/add/:type' Component={AddPage}></Route>
                    <Route path='/update/:type/:id' Component={UpdatePage}></Route>
                    <Route path="/operations" Component={OperationsPage}></Route>
                </Routes>
            </AuthContext.Provider>
        </>
    );
}

export default App
