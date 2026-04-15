import { useEffect, useState } from "react";
import { createSite, getSites } from "../api/siteApi";
import type { SiteResponse } from '../types/site'
import styles from '../pages/SitesPage.module.css'
import SiteCard from "../components/common/SiteCard";
import SiteCreatedModal from "../components/common/SiteCreatedModal";

export default function SitesPage() {
    const [siteDomain, setSiteDomain] = useState<string>("")
    const [siteName, setSiteName] = useState<string>("")
    const [sites, setSites] = useState<SiteResponse[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [submitting, setSubmitting] = useState(false)
    const [createdSite, setCreatedSite] = useState<SiteResponse | null>(null)

    useEffect(() => {
        getSites().then(data => setSites(data))
            .catch(() => setError('Failed to load sites'))
            .finally(() => setLoading(false))
    }, [])

    async function handleCreateSite(e: React.SyntheticEvent) {
        e.preventDefault()
        setSubmitting(true)
        try {
            const site = await createSite({ siteName, siteDomain })
            setCreatedSite(site)
            const updated = await getSites()
            setSites(updated)
        } catch {
            setError('Failed to create site')
        } finally {
            setSubmitting(false)
        }
    }

    function handleModalDone() {
        setCreatedSite(null)
        getSites().then(setSites)
    }

    if (loading) return <p>Loading...</p>
    if (error) return <p style={{ color: 'red' }}>{error}</p>

    return <>{createdSite &&
        <SiteCreatedModal siteCode={createdSite.siteCode} siteName={createdSite.siteName} onDone={handleModalDone} />}
        <section className={styles.headingSection}>
            <h1>Sites</h1>
            <p>Manage your digital properties and track real-time marketing performance across your ecosystem.</p>
        </section>
        <section className={styles.newSiteSection}>
            <p className="titles">Register New Site</p>
            <div>
                <form onSubmit={(e) => handleCreateSite(e)} className={styles.newSiteForm}>
                    <div className="form-field-container">
                        <label htmlFor="domain" className="label">
                            Site Domain
                        </label>
                        <input id="domain"
                            required
                            placeholder="https://example.domain.com"
                            type="url"
                            className="input"
                            onChange={(e) => setSiteDomain(e.target.value)} />
                    </div>
                    <div className="form-field-container">
                        <label htmlFor="name" className="label">
                            Site Name
                        </label>
                        <input id="name"
                            required
                            placeholder="Site Name"
                            type="text"
                            className="input"
                            onChange={(e) => setSiteName(e.target.value)} />
                    </div>
                    <button className={`button-primary button-with-icon ${styles.createSiteBtn}`} disabled={submitting} type="submit">
                        {submitting ? "Creating..." : "Create Site"}
                    </button>
                </form>
            </div>
        </section>
        <section>
            <div className="grid-3">
                {sites.map(site =>
                    <SiteCard site={site} key={site.siteId} />)}
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