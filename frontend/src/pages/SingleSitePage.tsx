import { useEffect, useState } from "react"
import { useNavigate, useParams } from "react-router-dom"
import type { SiteResponse } from "../types/site"
import { getSite } from "../api/siteApi"

export default function SingleSitePage() {
    const navigate = useNavigate()
    const { siteCode } = useParams<{ siteCode: string }>();
    const [site, setSite] = useState<SiteResponse | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!siteCode) {
            navigate('/app/404')
            return
        }
        getSite(siteCode).then(data => setSite(data))
            .catch(() => setError('Failed to load site'))
            .finally(() => setLoading(false))
    }, [siteCode])

    if (loading) return <p>Loading...</p>
    if (error) return <p style={{ color: 'red' }}>{error}</p>
    if (!site) return null

    return <div>
        <h1>{site.siteName}</h1>
        <p>{site.siteCode}</p>
        <button onClick={() => navigate(`/app/${siteCode}/campaigns`)}>
            Campaigns
        </button>
        <button onClick={() => navigate(`/app/${siteCode}/seo/reports`)}>
            Seo Reports
        </button>
    </div>
}