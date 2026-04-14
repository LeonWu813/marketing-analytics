import { useState } from "react"
import { formatDate, type SeoReportResponse } from "../../types/seo_report"
import styles from "../common/ReportPreview.module.css"
import { useNavigate } from "react-router-dom"

interface Props {
    report: SeoReportResponse
}

export default function ReportPreview({ report }: Props) {
    const navigate = useNavigate()

    const [seoScore] = useState<number>(Number(report?.seoScore) ?? -1)
    const [performanceScroe] = useState<number>(Number(report?.performanceScore) ?? -1)
    const [pass] = useState<number>(report.checks.filter(c => c.checkStatus == "PASS").length)
    const [warn] = useState<number>(report.checks.filter(c => c.checkStatus == "WARN").length)
    const [fail] = useState<number>(report.checks.filter(c => c.checkStatus == "FAIL").length)

    function examine() {
        return warn > 3 || seoScore < 70 || performanceScroe < 70
    }

    return <div className={styles.report}>
        <div className={styles.mainInfo}>
            <div className={styles.tagAndTime}>
                {examine() ?
                    <p className={`${styles.tag} ${styles.inactiveTag}`}>Action Required</p> :
                    <p className={`${styles.tag} ${styles.activeTag}`}>Healthy</p>}
                <p className={styles.date}>{formatDate(report.analyzedAt)}</p>
            </div>
            <p className="titles">{report.analyzedUrl}</p>
            {report.keyword && <p className={styles.keyword}>Keyword: {report.keyword}</p>}
        </div>
        <div className={`${styles.scores} center`}>
            <div>
                <p className={styles.scoreName}>SEO</p>
                <div className={styles.score}>
                    <p className={`${styles.scoreName} ${seoScore == -1 ?
                        styles.grayCircle : seoScore > 69 ?
                            styles.greenCircle : styles.redCircle}`}>
                        {seoScore}
                    </p>
                    <div className={`${styles.centerCircle} ${seoScore == -1 ?
                        styles.grayCircle : seoScore > 69 ?
                            styles.greenCircle : styles.redCircle}`}></div>
                </div>
            </div>
            <div>
                <p className={styles.scoreName}>PERF</p>
                <div className={styles.score}>
                    <p className={`${styles.scoreName} ${performanceScroe == -1 ?
                        styles.grayCircle : performanceScroe > 69 ?
                            styles.greenCircle : styles.redCircle}`}>
                        {performanceScroe}
                    </p>
                    <div className={`${styles.centerCircle} ${performanceScroe == -1 ?
                        styles.grayCircle : performanceScroe > 69 ?
                            styles.greenCircle : styles.redCircle}`}></div>
                </div>
            </div>
        </div>
        <div className={styles.checks}>
            <div className="center">
                <p className={styles.scoreName}>Pass</p>
                <p className={`${styles.checkText} ${styles.greenCircle}`}>{pass}</p>
            </div>
            <div className="center">
                <p className={styles.scoreName}>Warn</p>
                <p className={`${styles.checkText} ${styles.redCircle}`}>{warn}</p>
            </div>
            <div className="center">
                <p className={styles.scoreName}>Fail</p>
                <p className={`${styles.checkText} ${styles.grayCircle}`}>{fail}</p>
            </div>
        </div>
        <button className="button-secondary button-small" onClick={() =>
            navigate(`/app/${report.siteCode}/seo/analyze/${report.id}`)
        }><span className={styles.view}>View Rreport</span></button>
    </div>
}