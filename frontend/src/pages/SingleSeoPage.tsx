import { useEffect, useState } from "react"
import { formatDate, type SeoReportResponse } from "../types/seo_report"
import { getSingleReport, sentReport } from "../api/reportApi"
import { useNavigate, useParams } from "react-router-dom";
import styles from "./SingleSeoPage.module.css"
import Breadcrumb from "../components/common/Breadcrumb";
import type { SiteResponse } from "../types/site";
import { getSite } from "../api/siteApi";


export default function SingleSeoPage() {
    const navigate = useNavigate()

    const { siteCode } = useParams<{ siteCode: string }>();
    const [site, setSite] = useState<SiteResponse | null>(null)
    const [email, setEmail] = useState<string>("")
    const { id } = useParams<{ id: string }>();

    const [report, setReport] = useState<SeoReportResponse | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        if (!siteCode) {
            navigate('/app/404')
            return
        }
        getSite(siteCode).then(data => setSite(data))
            .catch(() => setError('Failed to load site'))
            .finally(() => setLoading(false))
        try {
            getSingleReport(siteCode, Number(id)).then(data => setReport(data))
        } catch {
            setError("Report not found")
        }
    }, [siteCode, id])

    if (loading) return <p>Loading...</p>
    if (error || !siteCode || !site || !report) {
        return <div>{error}</div>
    }

    return <>
        <section className={styles.heroSection}>
            <a href={`/app/${siteCode}/seo/reports`} className={`link linkWithIcon ${styles.back}`}>Back to reports</a>
            <div className={styles.titleContainer}>
                <div className={styles.titles}>
                    <Breadcrumb
                        first={site.siteName}
                        second="Seo Reports"
                        third="Seo Audit"
                    />
                    <h1>{report.analyzedUrl}</h1>
                    <p className={styles.date}>Report generated on {formatDate(report.analyzedAt)}</p>
                </div>
                <form onSubmit={() => sentReport(siteCode, Number(id), { sentTo: email })} className={styles.form}>
                    <input type="email"
                        required
                        name="email"
                        autoComplete="on"
                        className="input"
                        onChange={e => setEmail(e.target.value)}
                        placeholder="Enter received email" />
                    <button className="button-primary button-small">Share Report</button>
                </form>
            </div>
        </section>
        <section className="grid-3">
            <div className={styles.scoreOverview}>
                <div className={`${styles.score} ${styles.seo}`}>
                    <p className={styles.number}>{report.seoScore}</p>
                    <p className={styles.scoreTitle}>Performance</p>
                </div>
                <div className={styles.metric}>
                    <div className={styles.otherMetric}>
                        <div>
                            <p>LCP</p>
                            <p>{report.lcpSeconds}</p>
                        </div>
                    </div>
                    <div>this</div>
                </div>
            </div>
            <div className={styles.scoreOverview}>
                <div className={`${styles.score} ${styles.performance}`}>
                    <p className={styles.number}>{report.performanceScore}</p>
                    <p className={styles.scoreTitle}>Seo Score</p>
                </div>
            </div>
            <div></div>
        </section>
    </>
}