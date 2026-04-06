import { Outlet } from "react-router-dom";
import NavBar from "./Navbar";
import Footer from "./Footer";

export default function AppShell() {
    return (
        <div>
            <NavBar />
            <main className="content">
                <Outlet />
            </main>
            <Footer />
        </div>
    )
}