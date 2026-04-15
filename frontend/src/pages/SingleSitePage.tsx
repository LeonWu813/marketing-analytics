import { useEffect, useState } from "react"
import { Link, useNavigate, useParams } from "react-router-dom"
import type { SiteResponse } from "../types/site"
import { getSite } from "../api/siteApi"
import Breadcrumb from "../components/common/Breadcrumb"
import styles from "./SingleSitePage.module.css"
import { getEvents } from "../api/eventApi"
import type { EventResponse } from "../types/event"
import DashboardOverview from "../components/common/DashboardOverview"
import Chart from "../components/common/Chart"
import DashboardCard from "../components/common/DashboardCard"
import type { CampaignChannel, CampaignResponse } from "../types/campaign"
import Filter from "../components/common/Filter"
import { getReports } from "../api/reportApi"
import type { SeoReportResponse } from "../types/seo_report"
import { getCampaigns } from "../api/campaignApi"
import SiteCreatedModal from "../components/common/SiteCreatedModal"

const STATUS_CHANNEL: { label: string; value: CampaignChannel | undefined }[] = [
    { label: 'All', value: undefined },
    { label: 'Organic', value: 'ORGANIC' },
    { label: 'Paid', value: 'PAID' },
    { label: 'Social', value: 'SOCIAL' },
    { label: 'Email', value: 'EMAIL' },
    { label: 'Event', value: 'EVENT' },
    { label: 'Direct', value: 'DIRECT' },
    { label: 'OTHER', value: 'OTHER' },
]

const EVENT_TYPE: { label: string; value: string }[] = [
    { label: 'page view', value: 'PAGE_VIEW' },
    { label: 'click', value: 'CLICK' },
    { label: 'form submit', value: 'FORM_SUBMIT' }
]

export default function SingleSitePage() {
    const navigate = useNavigate()
    const { siteCode } = useParams<{ siteCode: string }>();
    const [site, setSite] = useState<SiteResponse | null>(null)
    const [events, setEvents] = useState<EventResponse[]>([])
    const [dateRange, setDateRange] = useState<"3M" | "1M">("1M")
    const [channel, setChannel] = useState<CampaignChannel | undefined>(undefined)
    const [eventType, setEventType] = useState<string>("PAGE_VIEW")
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [report, setReport] = useState<SeoReportResponse | null>(null)
    const [campaigns, setCampaigns] = useState<CampaignResponse[]>([])
    const [reconnect, setReconnect] = useState(false)

    useEffect(() => {
        if (!siteCode) { navigate('/app/404'); return }

        // compute 6 months ago as a date string "YYYY-MM-DD"
        const sixMonthsAgo = new Date()
        sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6)
        sixMonthsAgo.setDate(1)
        const startDate = sixMonthsAgo.toISOString().split('T')[0]

        Promise.all([
            getSite(siteCode),
            getEvents(siteCode, undefined, undefined, undefined, undefined, undefined, startDate),
            getReports(siteCode),
            getCampaigns(siteCode)
        ])
            .then(([siteData, eventData, reportData, campaignData]) => {
                setSite(siteData)
                setEvents(eventData)
                setReport(reportData.content[0])
                setCampaigns(campaignData.content.slice(0, 3))
            })
            .catch(() => setError('Failed to load data'))
            .finally(() => setLoading(false))
    }, [siteCode])

    if (loading) return <p>Loading...</p>
    if (error) return <p style={{ color: 'red' }}>{error}</p>
    if (!site) return null

    function handelChannelFilter(channel: CampaignChannel | undefined) {
        setChannel(channel)
    }

    function handelEventTypeFilter(eventType: string) {
        setEventType(eventType)
    }

    return <>{reconnect &&
        <SiteCreatedModal siteCode={site.siteCode} siteName={site.siteName} onDone={() => setReconnect(false)} />}
        <section className={styles.heroSection}>
            <Breadcrumb
                first="Site"
                second={site.siteName}
            />
            <div className={styles.titleContainer}>
                <div>
                    <h1>{site.siteName}</h1>
                    <div className={styles.codeContainer}>
                        <p className={styles.siteCode}>SITE_CODE: {siteCode}</p>
                        <button onClick={() => setReconnect(true)}>Reconnect Site</button>
                    </div>
                </div>
                <div className={styles.heroButtons}>
                    <button
                        className={`button-secondary button-with-icon ${styles.campaignBtn}`} onClick={() => navigate(`/app/${siteCode}/campaigns`)}>
                        Campaigns
                    </button>
                    <button
                        className={`button-primary button-with-icon ${styles.reportBtn}`} onClick={() => navigate(`/app/${siteCode}/seo/reports`)}>
                        Seo Reports
                    </button>
                </div>
            </div>
        </section>
        <section className={styles.dashboardSection}>
            <div className={styles.filters}>
                <Filter
                    label="Channel"
                    options={STATUS_CHANNEL}
                    onSelect={(value) => handelChannelFilter(value)}
                    large={true}
                />
                <Filter
                    label="Event Type"
                    options={EVENT_TYPE}
                    onSelect={(value) => handelEventTypeFilter(value ? value : "PAGE_VIEW")}
                    large={true}
                />
            </div>
            <div className={styles.topRow}>
                <div className={styles.overviewCardContainer}>
                    <DashboardOverview events={events} title="All Event" eventType="ALL" dateRange={dateRange} channel={channel} />
                    <DashboardOverview events={events} title={eventType} eventType={eventType} dateRange={dateRange} channel={channel} />
                </div>
                <div className={styles.chatContainer}>
                    <div className={styles.chatHeader}>
                        <p className={styles.dashboardCardTitle}>Events Over Time</p>
                        <div className={styles.charOptionContainer}>
                            <button
                                className={`${styles.charOption} ${dateRange === "1M" && styles.charOptionActive}`}
                                onClick={() => setDateRange("1M")}>
                                30 Days
                            </button>
                            <button
                                className={`${styles.charOption} ${dateRange === "3M" && styles.charOptionActive}`}
                                onClick={() => setDateRange("3M")}>
                                3 Months

                            </button>
                        </div>
                    </div>
                    <Chart events={events} eventType={eventType} dateRange={dateRange} channel={channel} />
                </div>
            </div>
            <div className="grid-3">
                <DashboardCard events={events} attribute="pageUrl" eventType={eventType} dateRange={dateRange} channel={channel} />
                <DashboardCard events={events} attribute="channel" eventType={eventType} dateRange={dateRange} channel={channel} />
                <DashboardCard events={events} attribute="country" eventType={eventType} dateRange={dateRange} channel={channel} />
            </div>
        </section >
        <section className={styles.previewSection}>
            <div className={styles.previewContainer}>
                <div className={styles.previewHeader}>
                    <div>
                        <div className={`${styles.icon} ${styles.campaignIcon}`}></div>
                        <p className="titles">Recent Campaigns</p>
                    </div>
                    <div >
                        <Link to={`/app/${siteCode}/campaigns`} className={styles.viewAll}>View all</Link>
                        <button className={`button-primary button-small button-with-icon ${styles.newCampaign}`} onClick={() => navigate(`/app/${siteCode}/campaigns`, { state: { openCreate: true } })}>Create</button>
                    </div>
                </div>
                <div className={styles.campaignContainer}>
                    {campaigns.map(c => (
                        <div key={c.id} className={styles.campaignPreview}>
                            <p>{c.campaignName}</p>
                            <p>{c.channel}</p>
                        </div>
                    ))}
                </div>
            </div>
            <div className={styles.previewContainer}>
                <div className={styles.previewHeader}>
                    <div>
                        <div className={`${styles.icon} ${styles.reportIcon}`}></div>
                        <p className="titles">SEO Performance</p>
                    </div>
                    <div>
                        <Link to={`/app/${siteCode}/seo/reports`} className={styles.viewAll}>All reports</Link>
                        <button className={`button-primary button-small button-with-icon ${styles.newReport}`} onClick={() => navigate(`/app/${siteCode}/seo/reports`, { state: { openCreate: true } })}>New Audit</button>
                    </div>
                </div>
                {report ?
                    <div className={styles.reportContainer}>
                        <div className={styles.reportDetail}>
                            <p className={styles.reportLabel}>LATEST AUDIT</p>
                            <p className={styles.reportUrl}>{report.analyzedUrl}</p>
                            <p className={styles.reportAt}>{report.analyzedAt}</p>
                        </div>
                        <div className={styles.scoreContainer}>
                            <div>
                                <p className={`${styles.score} ${styles.blue}`}>{report.seoScore}</p>
                                <p className={styles.scoreType}>Seo Score</p>
                            </div>
                            <div>
                                <p className={`${styles.score} ${styles.green}`}>{report.performanceScore}</p>
                                <p className={styles.scoreType}>Performance</p>
                            </div>
                        </div>
                    </div> :
                    <div><p>No recent report</p></div>
                }
            </div>
        </section>
    </>
}