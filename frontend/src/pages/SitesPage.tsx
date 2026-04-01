import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getSites } from "../api/siteApi";
import type { SiteResponse } from '../types/site'

export default function SitesPage() {
    const navigate = useNavigate()

    const [createSitePannel, setCreateSitePannel] = useState<boolean>(false)
    const [sites, setSites] = useState<SiteResponse[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        getSites().then(data => setSites(data))
            .catch(() => setError('Failed to load sites'))
            .finally(() => setLoading(false))
    }, [])

    function selectSite(siteCode: string) {
        navigate(`/app/sites/${siteCode}`)
    }

    if (loading) return <p>Loading...</p>
    if (error) return <p style={{ color: 'red' }}>{error}</p>

    return <div>
        <h1>Sites</h1>
        <button onClick={() =>
            setCreateSitePannel(!createSitePannel)}>Create Site
        </button>
        <div className="grid">
            {sites.map(site => (
                <div className="siteCard" key={site.siteId}>
                    <p>{site.siteName}</p>
                    <button onClick={() => selectSite(site.siteCode)}>View
                    </button>
                </div>
            ))}
        </div>
    </div>
}