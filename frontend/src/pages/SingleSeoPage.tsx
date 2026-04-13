import { useEffect, useState } from "react"
import { formatDate, type SeoReportResponse } from "../types/seo_report"
import { getSingleReport, sentReport } from "../api/reportApi"
import { useNavigate, useParams } from "react-router-dom";
import styles from "./SingleSeoPage.module.css"
import Breadcrumb from "../components/common/Breadcrumb";
import type { SiteResponse } from "../types/site";
import { getSite } from "../api/siteApi";
import warning from "../assets/warning.svg"
import opportunity from "../assets/opportunities.svg"

const TITLE_MAX = 60
const META_MAX = 155


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

    function metaCheck(meta: string) {
        const len = meta.length
        if (meta == "<empty>" || len == 0) {
            return "missing"
        } else if (len <= META_MAX && len >= 120) {
            return "optimal"
        } else if (len < 215 || len > 60) {
            return "good"
        } else {
            return "need improve"
        }
    }

    function titleCheck(title: string) {
        const len = title.length
        if (len == 0) {
            return "missing"
        } else if (len <= TITLE_MAX && len >= 50) {
            return "optimal"
        } else if (len < 80 || len > 30) {
            return "good"
        } else {
            return "bad"
        }
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
                <div className={`${styles.score} ${styles.performance}`}>
                    <p className={styles.number}>{report.performanceScore}</p>
                    <p className={styles.scoreTitle}>Seo Score</p>
                </div>

                <div className={styles.metric}>
                    <div className={styles.otherMetric}>
                        <div className="center">
                            <p>LCP</p>
                            <p>{report.lcpSeconds}</p>
                        </div>
                    </div>
                    <div className={styles.otherMetric}>
                        <div className="center">
                            <p>FCP</p>
                            <p>{report.fcpSeconds}</p>
                        </div>
                    </div>
                    <div className={styles.otherMetric}>
                        <div className="center">
                            <p>TBT</p>
                            <p>{report.tbtMilliseconds}</p>
                        </div>
                    </div>
                </div>
            </div>
            <div className={styles.scoreOverview}>
                <div className={`${styles.score} ${styles.seo}`}>
                    <p className={styles.number}>{report.seoScore}</p>
                    <p className={styles.scoreTitle}>Performance</p>
                </div>
                <div className={styles.seoMetric}>
                    <div>
                        <p className="label">Indexable</p>
                        <p>{report.loadingExperience && "Yes"}</p>
                    </div>
                    <div>
                        <p className="label">Mobile</p>
                        <p>{report.loadingExperience}</p>
                    </div>
                </div>
            </div>
            <div className={styles.serp}>
                <p className={styles.serpTitle}>Serp preview</p>
                <div className={styles.serpPreview}>
                    <p className="titles">
                        {report.title.length > TITLE_MAX ? report.title.substring(0, TITLE_MAX) + "..." : report.title}
                    </p>
                    <p>
                        {report.metaDescription.length > META_MAX ? report.metaDescription.substring(0, META_MAX) + "..." : report.metaDescription}
                    </p>
                </div>
                <div className={styles.serpSummary}>
                    <div>
                        <p>Title Length</p>
                        <p
                            className={titleCheck(report.title) == "good" ? styles.good : titleCheck(report.title) == "optimal" ? styles.optimal : styles.bad}
                        >{`${titleCheck(report.title)}${report.title == "<empty>" ? "" : ` (${report.title.length} chars)`}`}</p>
                    </div>
                    <div>
                        <p>Description Length</p>
                        <p
                            className={metaCheck(report.title) == "good" ? styles.good : metaCheck(report.title) == "optimal" ? styles.optimal : styles.bad}>
                            {`${metaCheck(report.metaDescription)}${report.metaDescription == "<empty>" ? "" : ` (${report.metaDescription.length} chars)`}`}</p>
                    </div>
                </div>
            </div>
        </section>
        <section className={styles.actions}>
            <p className="titles">Optimization Analysis</p>
            <div className={`${styles.actionsContainer} ${styles.criticalFixed}`}>
                <p className={styles.actionsTitle}>Critical Fixes</p>
                {
                    report.opportunities.map(opp =>
                        opp.type == "opportunity" &&
                        <div className={styles.actionItem}>
                            <img src={warning} alt="" width={24} height={24} style={{ marginTop: '4px' }} />
                            <div>
                                <div className={styles.actionTitleContainer}>
                                    <p className={styles.actionTitle}>{opp.title}</p>
                                    <p className={styles.actionTitle}>Saving {opp.savingsBytes} bytes</p>
                                </div>
                                <p className={styles.actionDescription}>{opp.description}</p>
                            </div>
                        </div>

                    )
                }
                {
                    report.seoAudits.map(audit =>
                        audit.score == 0 &&
                        <div className={styles.actionItem}>
                            <img src={warning} alt="" width={24} height={24} />
                            <div>
                                <div className={styles.actionTitleContainer}>
                                    <p className={styles.actionTitle}>{audit.title}</p>
                                </div>
                            </div>
                        </div>

                    )
                }
                {
                    report.checks.map(check =>
                        check.checkStatus == "FAIL" &&
                        <div className={styles.actionItem}>
                            <img src={warning} alt="" width={24} height={24} />
                            <div>
                                <div className={styles.actionTitleContainer}>
                                    <p className={styles.actionTitle}>{check.checkName}</p>
                                </div>
                                <p className={styles.actionDescription}>{check.details}</p>
                            </div>
                        </div>

                    )
                }
            </div>
            <div className={`${styles.actionsContainer} ${styles.opportunities}`}>
                <p className={styles.actionsTitle}>Opportunities</p>
                {
                    report.checks.map(check =>
                        check.checkStatus == "WARN" &&
                        <div className={styles.actionItem}>
                            <img src={opportunity} alt="" width={24} height={24} />
                            <div>
                                <div className={styles.actionTitleContainer}>
                                    <p className={styles.actionTitle}>{check.checkName}</p>
                                </div>
                                <p className={styles.actionDescription}>{check.details}</p>
                            </div>
                        </div>

                    )
                }
                {
                    report.opportunities.map(opp =>
                        opp.type == "diagnostic" &&
                        <div className={styles.actionItem}>
                            <img src={opportunity} alt="" width={24} height={24} style={{ marginTop: '4px' }} />
                            <div>
                                <div className={styles.actionTitleContainer}>
                                    <p className={styles.actionTitle}>{opp.title}</p>
                                </div>
                                <p className={styles.actionDescription}>{opp.description}</p>
                            </div>
                        </div>

                    )
                }
            </div>
        </section>
    </>
}