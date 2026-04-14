import type { CampaignChannel } from "../../types/campaign"
import type { EventResponse } from "../../types/event"
import styles from "./DashboardOverview.module.css"

interface Props {
    events: EventResponse[]
    title: string
    eventType: string
    dateRange: "1M" | "3M"
    channel: CampaignChannel | undefined
}

export default function DashboardOverview({ events, title, eventType, dateRange, channel }: Props) {

    const now = new Date()
    const startOfThisMonth = new Date(now.getFullYear(), now.getMonth(), 1)
    const startOfLastMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1)
    const startOfThis3M = new Date(now.getFullYear(), now.getMonth() - 2, 1)

    const channelFilter = channel ? events.filter(e => {
        return e.channel === channel
    }) : events

    const thisRangeEvents =
        channelFilter.filter(e => {
            const d = new Date(e.createdAt)
            return dateRange === '1M'
                ? d >= startOfThisMonth
                : d >= startOfThis3M
        })

    const lastRangeEvents = channelFilter.filter(e => {
        const d = new Date(e.createdAt)
        return dateRange === '1M'
            ? d < startOfThisMonth && d >= startOfLastMonth
            : d < startOfThis3M
    })

    const thisRangeCount = eventType === 'ALL'
        ? thisRangeEvents.length
        : thisRangeEvents.filter(e => e.eventType === eventType).length

    const lastRangeCount = eventType === 'ALL'
        ? lastRangeEvents.length
        : lastRangeEvents.filter(e => e.eventType === eventType).length

    const percentChange = lastRangeCount === 0
        ? 0
        : Math.trunc(((thisRangeCount - lastRangeCount) / lastRangeCount) * 100)


    return <div className={styles.overviewCard}>
        <p className={styles.cardTitle}>{title.split("_").join(" ")}</p>
        <p className={styles.cardValue}>{thisRangeCount}</p>
        <p className={styles.cardCompare}>
            <svg width="12" height="7" viewBox="0 0 12 7" fill="none" xmlns="http://www.w3.org/2000/svg" style={{ marginRight: "4px" }} className={thisRangeCount > lastRangeCount ? styles.greenSvg : styles.redSvg}>
                <path d="M0.816667 7L0 6.18333L4.31667 1.8375L6.65 4.17083L9.68333 1.16667H8.16667V0H11.6667V3.5H10.5V1.98333L6.65 5.83333L4.31667 3.5L0.816667 7Z" />
            </svg>
            <span className={thisRangeCount > lastRangeCount ? styles.green : styles.red}>{percentChange}%</span> vs last period</p>
    </div>
}