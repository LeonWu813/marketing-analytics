import type { CampaignChannel } from "../../types/campaign"
import type { EventResponse } from "../../types/event"
import styles from "./DashboardCard.module.css"

interface Props {
    events: EventResponse[]
    attribute: string
    eventType: string
    dateRange: "1M" | "3M"
    channel: CampaignChannel | undefined
}

export default function DashboardCard({ events,
    attribute, eventType, dateRange, channel }: Props) {
    const title = attribute === "pageUrl" ? "page" : attribute

    const now = new Date()
    const startOfThisMonth = new Date(now.getFullYear(), now.getMonth(), 1)
    const startOfThis3M = new Date(now.getFullYear(), now.getMonth() - 2, 1)

    const channelFilter = channel ? events.filter(e => {
        return e.channel === channel
    }) : events

    const rangeFilter = channelFilter.filter((e) => {
        const d = new Date(e.createdAt)
        return dateRange === '1M'
            ? d >= startOfThisMonth
            : d >= startOfThis3M
    })

    const typeFilter = rangeFilter.filter((e) => {
        return e.eventType === eventType
    })

    const attributeBreakdown: { label: string; value: number }[] = []
    typeFilter.forEach(e => {
        const label = String(e[attribute as keyof EventResponse] ?? 'Unknow')
        const existing = attributeBreakdown.find(a => a.label === label)
        if (existing) {
            existing.value++
        } else {
            attributeBreakdown.push({ label, value: 1 })
        }
    })

    const top5 = attributeBreakdown
        .sort((a, b) => b.value - a.value)
        .slice(0, 5)

    return <div className={styles.dashboardCard}>
        <p className={styles.dashboardCardTitle}>Top {title}</p>
        <div>
            <div className={styles.rows}>
                <p className={styles.header}>{title}</p>
                <p className={styles.header}>Counts</p>
            </div>
            {top5.map((item, index) => (
                <div className={styles.rows} key={index}>
                    <p>{item.label}</p>
                    <p>{item.value}</p>
                </div>
            ))}
        </div>
    </div>
}