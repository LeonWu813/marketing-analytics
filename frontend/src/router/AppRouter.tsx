import { createBrowserRouter, RouterProvider, Navigate, Outlet } from 'react-router-dom'
import { useSelector } from 'react-redux'
import type { RootState } from '../store/index'
import LoginPage from '../pages/LoginPage'
import RegisterPage from '../pages/RegisterPage'
import SitesPage from '../pages/SitesPage'
import SingleSitePage from '../pages/SingleSitePage'
import CampaignsPage from '../pages/CampaignsPage'
import SeoPage from '../pages/SeoPage'
import SingleSeoPage from '../pages/SingleSeoPage'

function ProtectedRoute() {
    const isAuthenticated = useSelector(
        (state: RootState) => state.auth.isAuthenticated
    )
    return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />
}

const router = createBrowserRouter([
    { path: '/login', element: <LoginPage /> },
    { path: '/register', element: <RegisterPage /> },
    {
        element: <ProtectedRoute />,
        children: [
            { path: '/app/sites', element: <SitesPage /> },
            { path: '/app/sites/:siteCode', element: <SingleSitePage /> },
            { path: '/app/:siteCode/campaigns', element: <CampaignsPage /> },
            { path: '/app/:siteCode/seo/reports', element: <SeoPage /> },
            { path: '/app/:siteCode/seo/analyze/:id', element: <SingleSeoPage /> },
        ]
    },
    { path: '/', element: <Navigate to="/login" /> }
])

export default function AppRouter() {
    return <RouterProvider router={router} />
}