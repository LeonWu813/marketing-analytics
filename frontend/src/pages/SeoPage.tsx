import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import type { SeoReportResponse } from "../types/seo_report";
import { createReport, getReports } from "../api/reportApi";
import Breadcrumb from "../components/common/Breadcrumb";
import type { SiteResponse } from "../types/site";
import { getSite } from "../api/siteApi";
import styles from "./SeoPage.module.css"
import ReportPreview from "../components/common/ReportPreview";

export default function SeoPage() {
    const navigate = useNavigate()
    const location = useLocation()

    const { siteCode } = useParams<{ siteCode: string }>();
    const [site, setSite] = useState<SiteResponse | null>(null)
    const [reports, setReports] = useState<SeoReportResponse[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [createSeoPannel, setCreateSeoPannel] = useState<boolean>(
        location.state?.openCreate ?? false
    )
    const [targetUrl, setTargetUrl] = useState<string>("")
    const [targetKeyword, setTargetKeyword] = useState<string>("")

    useEffect(() => {
        if (!siteCode) {
            navigate('/app/404'); return
        }
        getSite(siteCode).then(data => setSite(data))
            .catch(() => setError('Failed to load site'))
            .finally(() => setLoading(false))
        getReports(siteCode).then(data => setReports(data.content)).catch(() => setError('Failed to load reports'))
            .finally(() => setLoading(false))
    }, [siteCode])

    if (loading) return <p>Loading...</p>
    if (error || !site || !siteCode) return <p style={{ color: 'red' }}>{error}</p>

    const newSeoAudit = async (e: React.SyntheticEvent) => {
        await createReport({
            analyzedUrl: targetUrl,
            keyword: targetKeyword,
            isFollowUp: false,
        }, siteCode)
    }

    return <>
        <section className={styles.heroSection}>
            <Breadcrumb
                first={site.siteName}
                second="Seo Reports"
            />
            <div className={styles.titleContainer}>
                <h1>Seo Report</h1>
                {!createSeoPannel && <button
                    className={`button-primary button-with-icon button-small ${styles.addButton}`}
                    onClick={() => setCreateSeoPannel(!createSeoPannel)}
                >
                    New Audit
                </button>}
            </div>
        </section>
        {
            createSeoPannel && <section className={styles.newSeoSection}>
                <p className="titles">Submit New Audit</p>
                <div className="form-field-container">
                    <form onSubmit={newSeoAudit} className={styles.newSeoForm}>
                        <div className="form-field-container">
                            <label htmlFor="url" className="label">
                                Target URL
                            </label>
                            <input id="url"
                                required
                                placeholder="https://example.domain.com"
                                type="url"
                                className="input"
                                autoComplete="on"
                                onChange={(e) => setTargetUrl(e.target.value)} />
                        </div>
                        <div className="form-field-container">
                            <label htmlFor="keyword" className="label">
                                Target Keyword
                            </label>
                            <input id="keyword"
                                placeholder="keyword"
                                type="text"
                                className="input"
                                onChange={(e) => setTargetKeyword(e.target.value)} />
                        </div>
                        <button className={`button-primary button-with-icon ${styles.createSeoBtn}`} type="submit">
                            Create Site
                        </button>
                    </form>
                </div>
            </section>
        }
        <section className={styles.seoReportsContainer}>{
            reports.map(report => (
                <ReportPreview key={report.id} report={report} />
            ))
        }</section >
    </>
}