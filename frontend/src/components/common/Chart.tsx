import type { CampaignChannel } from "../../types/campaign"
import type { EventResponse } from "../../types/event"
import { Bar, BarChart, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts"

interface Props {
    events: EventResponse[]
    eventType: string
    dateRange: "1M" | "3M"
    channel: CampaignChannel | undefined
}

export default function Chart({ events, eventType, dateRange, channel }: Props) {

    const now = new Date()

    // ── Date boundaries ───────────────────────────────────────────────
    const startOfThisMonth = new Date(now.getFullYear(), now.getMonth(), 1)
    const startOfLastMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1)
    const startOfThis3M = new Date(now.getFullYear(), now.getMonth() - 2, 1)
    const startOfLast3M = new Date(now.getFullYear(), now.getMonth() - 5, 1)

    const channelFilter = channel ? events.filter(e => {
        return e.channel === channel
    }) : events

    // ── Filter by eventType helper ────────────────────────────────────
    const filterType = (arr: EventResponse[]) =>
        eventType === 'ALL' ? arr : arr.filter(e => e.eventType === eventType)

    // ── 1M: breakdown by day ──────────────────────────────────────────
    const build1MData = () => {
        const thisMEvents = filterType(
            channelFilter.filter(e => new Date(e.createdAt) >= startOfThisMonth)
        )
        const lastMEvents = filterType(
            channelFilter.filter(e => {
                const d = new Date(e.createdAt)
                return d >= startOfLastMonth && d < startOfThisMonth
            })
        )

        const daysInLastMonth = new Date(
            now.getFullYear(), now.getMonth(), 0
        ).getDate()

        return Array.from({ length: daysInLastMonth }, (_, i) => {
            const day = i + 1
            return {
                day,
                thisMonth: thisMEvents.filter(e =>
                    new Date(e.createdAt).getDate() === day
                ).length,
                lastMonth: lastMEvents.filter(e =>
                    new Date(e.createdAt).getDate() === day
                ).length,
            }
        })
    }

    // ── 3M: breakdown by month ────────────────────────────────────────
    const build3MData = () => {
        const this3MEvents = filterType(
            channelFilter.filter(e => {
                const d = new Date(e.createdAt)
                return d >= startOfThis3M && d < startOfThisMonth
            })
        )
        const last3MEvents = filterType(
            channelFilter.filter(e => {
                const d = new Date(e.createdAt)
                return d >= startOfLast3M && d < startOfThis3M
            })
        )

        // build 3 month slots
        return Array.from({ length: 3 }, (_, i) => {
            const monthDate = new Date(now.getFullYear(), now.getMonth() - 2 + i, 1)
            const prevMonthDate = new Date(now.getFullYear(), now.getMonth() - 5 + i, 1)

            const label = monthDate.toLocaleDateString('en-US', { month: 'short' })

            const thisCount = this3MEvents.filter(e => {
                const d = new Date(e.createdAt)
                return d.getMonth() === monthDate.getMonth() &&
                    d.getFullYear() === monthDate.getFullYear()
            }).length

            const lastCount = last3MEvents.filter(e => {
                const d = new Date(e.createdAt)
                return d.getMonth() === prevMonthDate.getMonth() &&
                    d.getFullYear() === prevMonthDate.getFullYear()
            }).length

            return { label, thisMonth: thisCount, lastMonth: lastCount }
        })
    }

    // ── Render ────────────────────────────────────────────────────────
    if (dateRange === '1M') {
        const data = build1MData()
        return (
            <ResponsiveContainer width="100%" height="100%">
                <BarChart data={data} barGap={2}>
                    <XAxis
                        dataKey="day"
                        tick={{ fontSize: 11 }}
                        tickFormatter={(day) => day % 5 === 0 ? String(day) : ''}
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
                        formatter={(value) =>
                            value === 'thisMonth' ? 'This Month' : 'Last Month'
                        }
                    />
                    <Bar dataKey="lastMonth" fill="#D5E3FC" radius={[2, 2, 0, 0]} />
                    <Bar dataKey="thisMonth" fill="#0C52D2" radius={[2, 2, 0, 0]} />
                </BarChart>
            </ResponsiveContainer>
        )
    }

    // 3M
    const data = build3MData()
    return (
        <ResponsiveContainer width="100%" height="100%">
            <BarChart data={data} barGap={4}>
                <XAxis dataKey="label" tick={{ fontSize: 12 }} />
                <YAxis hide />
                <Tooltip
                    formatter={(value, name) => [
                        value,
                        name === 'thisMonth' ? 'This Period' : 'Previous Period'
                    ]}
                />
                <Legend
                    formatter={(value) =>
                        value === 'thisMonth' ? 'This Period (last 3M)' : 'Previous Period (3-6M ago)'
                    }
                />
                <Bar dataKey="lastMonth" fill="#D5E3FC" radius={[4, 4, 0, 0]} />
                <Bar
                    dataKey="thisMonth"
                    fill="#0C52D2"
                    radius={[4, 4, 0, 0]}
                    shape={(props: any) => {
                        const { x, y, width, height, index } = props
                        const opacity = 0.5 + (index / data.length) * 0.5
                        return (
                            <rect
                                x={x}
                                y={y}
                                width={width}
                                height={height}
                                fill={`rgba(12, 82, 210, ${opacity})`}
                                rx={4}
                                ry={4}
                            />
                        )
                    }}
                />
            </BarChart>
        </ResponsiveContainer>
    )
}