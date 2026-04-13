import { useEffect, useState } from "react"
import { useNavigate, useParams } from "react-router-dom"
import type { SiteResponse } from "../types/site"
import { getSite } from "../api/siteApi"
import Breadcrumb from "../components/common/Breadcrumb"
import styles from "./SingleSitePage.module.css"
import { getEvents } from "../api/eventApi"
import type { EventResponse } from "../types/event"
import { Bar, BarChart, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts"

export default function SingleSitePage() {
    const navigate = useNavigate()
    const { siteCode } = useParams<{ siteCode: string }>();
    const [site, setSite] = useState<SiteResponse | null>(null)
    const [events, setEvents] = useState<EventResponse[]>([])
    const [chartDisplay, setChartDisplay] = useState<"month" | "day">("day")
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!siteCode) { navigate('/app/404'); return }

        // compute 6 months ago as a date string "YYYY-MM-DD"
        const sixMonthsAgo = new Date()
        sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6)
        sixMonthsAgo.setDate(1)
        const startDate = sixMonthsAgo.toISOString().split('T')[0]

        Promise.all([
            getSite(siteCode),
            getEvents(siteCode, undefined, undefined, undefined, undefined, undefined, startDate)
        ])
            .then(([siteData, eventData]) => {
                setSite(siteData)
                setEvents(eventData)
            })
            .catch(() => setError('Failed to load data'))
            .finally(() => setLoading(false))
    }, [siteCode])

    if (loading) return <p>Loading...</p>
    if (error) return <p style={{ color: 'red' }}>{error}</p>
    if (!site) return null

    const chartDataSixMonthByMonth = events.reduce((acc, event) => {
        const date = event.createdAt.substring(0, 7)
        const existing = acc.find(d => d.date.substring(0, 7) === date)
        if (existing) {
            existing.count++
        } else {
            acc.push({ date, count: 1 })
        }
        return acc
    }, [] as { date: string; count: number }[])
        .sort((a, b) => a.date.localeCompare(b.date))

    const now = new Date()
    const thisMonthNum = now.getMonth()
    const lastMonthNum = now.getMonth() - 1
    const thisYear = now.getFullYear()
    const lastMonthYear = lastMonthNum < 0 ? thisYear - 1 : thisYear
    const lastMonthAdj = lastMonthNum < 0 ? 11 : lastMonthNum

    // get events for this month
    const thisMonthEvents = events.filter(e => {
        const d = new Date(e.createdAt)
        return d.getMonth() === thisMonthNum && d.getFullYear() === thisYear
    })

    const thisMonthView = thisMonthEvents.filter(e => e.eventType === 'PAGE_VIEW').length

    // get events for last month
    const lastMonthEvents = events.filter(e => {
        const d = new Date(e.createdAt)
        return d.getMonth() === lastMonthAdj && d.getFullYear() === lastMonthYear
    })

    const lastMonthView = lastMonthEvents.filter(e => e.eventType === 'PAGE_VIEW').length

    // get how many days last month had
    const daysInLastMonth = new Date(lastMonthYear, lastMonthAdj + 1, 0).getDate()

    // build one entry per day
    const compareByDay = Array.from({ length: daysInLastMonth }, (_, i) => {
        const day = i + 1
        return {
            day,
            thisMonth: thisMonthEvents.filter(e => new Date(e.createdAt).getDate() === day && e.eventType === 'PAGE_VIEW').length,
            lastMonth: lastMonthEvents.filter(e => new Date(e.createdAt).getDate() === day && e.eventType === 'PAGE_VIEW').length,
        }
    })

    return <>
        <section className={styles.heroSection}>
            <Breadcrumb
                first={site.siteName}
                second="Seo Reports"
            />
            <div className={styles.titleContainer}>
                <div>
                    <h1>{site.siteName}</h1>
                    <p className={styles.siteCode}>SITE_CODE: {siteCode}</p>
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
            <div className={styles.topRow}>
                <div className={styles.overviewCardContainer}>
                    <div className={styles.overviewCard}>
                        <p className={styles.cardTitle}>Monthly Page Views</p>
                        <p className={styles.cardValue}>{thisMonthView}</p>
                        <p className={styles.cardCompare}>
                            <svg width="12" height="7" viewBox="0 0 12 7" fill="none" xmlns="http://www.w3.org/2000/svg" style={{ marginRight: "4px" }} className={thisMonthView > lastMonthView ? styles.greenSvg : styles.redSvg}>
                                <path d="M0.816667 7L0 6.18333L4.31667 1.8375L6.65 4.17083L9.68333 1.16667H8.16667V0H11.6667V3.5H10.5V1.98333L6.65 5.83333L4.31667 3.5L0.816667 7Z" />
                            </svg>
                            <span className={thisMonthView > lastMonthView ? styles.green : styles.red}>{Math.trunc(((thisMonthView - lastMonthView) / lastMonthView) * 100)}%</span> vs last month</p>
                    </div>
                    <div className={styles.overviewCard}>
                        <p className={styles.cardTitle}>Monthly Page Events</p>
                        <p className={styles.cardValue}>{thisMonthEvents.length}</p>
                        <p className={styles.cardCompare}>
                            <svg width="12" height="7" viewBox="0 0 12 7" fill="none" xmlns="http://www.w3.org/2000/svg" style={{ marginRight: "4px" }} className={thisMonthEvents.length > lastMonthEvents.length ? styles.greenSvg : styles.redSvg}>
                                <path d="M0.816667 7L0 6.18333L4.31667 1.8375L6.65 4.17083L9.68333 1.16667H8.16667V0H11.6667V3.5H10.5V1.98333L6.65 5.83333L4.31667 3.5L0.816667 7Z" />
                            </svg>
                            <span className={thisMonthEvents.length > lastMonthEvents.length ? styles.green : styles.red}>{Math.trunc(((thisMonthEvents.length - lastMonthEvents.length) / lastMonthEvents.length) * 100)}%</span> vs last month</p>
                    </div>
                </div>
                <div className={styles.chatContainer}>
                    <div className={styles.chatHeader}>
                        <p className={styles.dashboardCardTitle}>Events Over Time</p>
                        <div className={styles.charOptionContainer}>
                            <button
                                className={`${styles.charOption} ${chartDisplay === "day" && styles.charOptionActive}`}
                                onClick={() => setChartDisplay("day")}>
                                30 Days
                            </button>
                            <button
                                className={`${styles.charOption} ${chartDisplay === "month" && styles.charOptionActive}`}
                                onClick={() => setChartDisplay("month")}>
                                6 Months

                            </button>
                        </div>
                    </div>
                    {chartDisplay === "month" ?
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={chartDataSixMonthByMonth}>
                                <XAxis
                                    dataKey="date"
                                    tick={{ fontSize: 11 }}
                                    tickFormatter={(date) => {
                                        return new Date(date + '-01').toLocaleDateString('en-US', { month: 'short' })
                                    }}
                                />
                                <YAxis hide />
                                <Tooltip
                                    formatter={(value) => [value, 'Events']}
                                    labelFormatter={(date) => new Date(date).toLocaleDateString()}
                                />
                                <Bar
                                    dataKey="count"
                                    fill="#0C52D2"
                                    radius={[4, 4, 0, 0]}
                                />
                            </BarChart>
                        </ResponsiveContainer> :
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={compareByDay} barGap={2}>
                                <XAxis
                                    dataKey="day"
                                    tick={{ fontSize: 11 }}
                                    tickFormatter={(day) => day % 5 === 0 ? day : ''}
                                />
                                <YAxis hide />
                                <Tooltip
                                    formatter={(value, name) => [
                                        value,
                                        name === 'thisMonth' ? 'This Month' : 'Last Month'
                                    ]}
                                    labelFormatter={(day) => `Day ${day}`}
                                />
                                <Legend
                                    formatter={(value) => value === 'thisMonth' ? 'This Month' : 'Last Month'}
                                />
                                <Bar dataKey="lastMonth" fill="#D5E3FC" radius={[2, 2, 0, 0]} />
                                <Bar dataKey="thisMonth" fill="#0C52D2" radius={[2, 2, 0, 0]} />
                            </BarChart>
                        </ResponsiveContainer>}
                </div>
            </div>
            <div className="grid-3">
                <div className={styles.dashboardCard}>
                    <p className={styles.dashboardCardTitle}>Event Type Breakdown</p>
                </div>
            </div>
        </section>
    </>
}