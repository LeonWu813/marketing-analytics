import { useEffect, useState } from "react";
import { getSites } from "../api/siteApi";
import type { SiteResponse } from '../types/site'
import styles from '../pages/SitesPage.module.css'
import SiteCard from "../components/common/SiteCard";

export default function SitesPage() {
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


    if (loading) return <p>Loading...</p>
    if (error) return <p style={{ color: 'red' }}>{error}</p>

    return <>
        <section className={styles.headingSection}>
            <h1>Sites</h1>
            <p>Manage your digital properties and track real-time marketing performance across your ecosystem.</p>
        </section>
        <section className={styles.newSiteSection}>
            <p className="titles">Register New Site</p>
            <div className="form-field-container">
                <label htmlFor="domain" className="label">
                    Site Domain
                </label>
                <form onSubmit={() => setCreateSitePannel(true)} className={styles.newSiteForm}>
                    <input id="domain"
                        required
                        placeholder="https://example.domain.com"
                        type="url"
                        className="input"
                        onChange={(e) => setSiteDomain(e.target.value)} />
                    <button className={`button-primary button-small button-with-icon ${styles.createSiteBtn}`} type="submit">
                        Create Site
                    </button>
                </form>
            </div>
        </section>
        <section>
            <div className="grid-3">
                {sites.map(site =>
                    <SiteCard site={site} />)}
            </div>
        </section>
        <section className={styles.scaleProfile}>
            <div className={styles.scaleIcon}></div>
            <div className={`${styles.scaleContent} center`}>
                <p className="titles">Scale Your Portfolio</p>
                <p className={styles.scaleDescription}>
                    Connect additional marketing properties to unify your
                    cross-channel analytics. Our architectural engine
                    supports up to 9 concurrent site environments.</p>
            </div>
            <a
                href="https://github.com/LeonWu813/marketing-analytics"
                target="_blank"
                className={`linkWithIcon ${styles.learnMore}`}>
                <p>Learn about Site Architect</p>
            </a>
        </section>
    </>;
}