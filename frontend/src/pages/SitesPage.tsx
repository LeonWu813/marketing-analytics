import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getSites } from "../api/siteApi";
import type { SiteResponse } from '../types/site'
import styles from '../pages/SitesPage.module.css'

export default function SitesPage() {
    const navigate = useNavigate()

    const [createSitePannel, setCreateSitePannel] = useState<boolean>(false)
    const [siteDomain, setSiteDomain] = useState<string>("")
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

    return <>
        <section className={styles.headingSection}>
            <h1>Sites</h1>
            <p>Manage your digital properties and track real-time marketing performance across your ecosystem.</p>
        </section>
        <section className={styles.newSiteSection}>
            <p className="titles">Register New Site</p>
            <div className={styles.newSiteForm}>
                <label htmlFor="domain" className="label">
                    Site Domain
                </label>
                <form onSubmit={() => setCreateSitePannel(true)}>
                    <input id="domain"
                        required
                        placeholder="https://example.domain.com"
                        type="url"
                        className="input"
                        onChange={(e) => setSiteDomain(e.target.value)} />
                    <button className={`button-primary ${styles.createSiteBtn}`} type="submit">
                        Create Site
                    </button>
                </form>
            </div>
        </section>


        <div>
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
    </>;
}