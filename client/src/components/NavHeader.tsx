import { Navbar, Nav } from 'rsuite';
import { getUserInfo, isAdmin, isLoggedOut, logout } from '../utils/auth';

const HomeButton = ({ isLoggedOut }: { isLoggedOut: boolean }) => {
    if (!isLoggedOut) {
        return <Nav.Item href="/">Home</Nav.Item>;
    }

    return (
        <></>
    );
};

const OperationsButton = ({ isLoggedOut }: { isLoggedOut: boolean }) => {
    if (!isLoggedOut) {
        return <Nav.Item href="/operations">Operations</Nav.Item>;
    }

    return (
        <></>
    );
};

const AdminPanelButton = ({ isAdmin }: { isAdmin: boolean }) => {
    if (!isAdmin) {
        return null;
    }

    return <Nav.Item href="/admin">Admin Panel</Nav.Item>;
};

const UserButton = ({ isLoggedOut }: { isLoggedOut: boolean }) => {
    if (isLoggedOut) {
        return (
            <>
                <Nav.Item href="/signin">Sign in</Nav.Item>
                <Nav.Item href="/signup">Sign up</Nav.Item>
            </>
        );
    }

    return (
        <>
            <Nav.Item href={`/users/${getUserInfo()?.username}`}>User Info</Nav.Item>
            <Nav.Item onClick={() => logout()}>Log Out</Nav.Item>
        </>
    );
};

const NavHeader = () => {
    return (
        <Navbar>
            <Nav>
                <HomeButton isLoggedOut={isLoggedOut()} />
                <OperationsButton isLoggedOut={isLoggedOut()} />
            </Nav>
            <Nav pullRight>
                <AdminPanelButton isAdmin={isAdmin()} />
                <UserButton isLoggedOut={isLoggedOut()} />
            </Nav>
        </Navbar>
    );
};

export default NavHeader;