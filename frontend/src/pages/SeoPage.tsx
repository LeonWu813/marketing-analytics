import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import type { SeoReportResponse } from "../types/seo_report";
import { getReports } from "../api/reportApi";

export default function SeoPage() {
    const navigate = useNavigate()
    const { siteCode } = useParams<{ siteCode: string }>();
    const [reports, setReports] = useState<SeoReportResponse[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)


    useEffect(() => {
        if (!siteCode) {
            navigate('/app/404'); return
        }
        getReports(siteCode).then(data => setReports(data.content)).catch(() => setError('Failed to load reports'))
            .finally(() => setLoading(false))
    }, [siteCode])

    if (loading) return <p>Loading...</p>
    if (error) return <p style={{ color: 'red' }}>{error}</p>

    return <div><h1>Seo Reports</h1>{
        reports.map(report => (
            <div key={report.id}>
                <p>Report Time: {report.analyzedAt}</p>
                <button onClick={() => navigate(`/app/${siteCode}/seo/analyze/${report.id}`)}>Report</button>
            </div>
        ))
    }</div>
}