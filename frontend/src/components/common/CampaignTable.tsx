import type { CampaignChannel, CampaignResponse, CampaignStatus } from "../../types/campaign"
import styles from "../common/CampaignTable.module.css"
import organicIcon from '../../assets/organic.svg'
import paidIcon from '../../assets/paid.svg'
import socialIcon from '../../assets/social.svg'
import emailIcon from '../../assets/email.svg'
import eventIcon from '../../assets/event.svg'
import scaleIcon from '../../assets/scale.svg'
import CreateCampaignPannel from "./CreateCampaignPannel"
import { useState } from "react"


const CHANNEL_MAP: Record<CampaignChannel, { label: string; icon: string }> = {
    ORGANIC: { label: 'Organic', icon: organicIcon },
    PAID: { label: 'Paid', icon: paidIcon },
    SOCIAL: { label: 'Social', icon: socialIcon },
    EMAIL: { label: 'Email', icon: emailIcon },
    EVENT: { label: 'Event', icon: eventIcon },
    DIRECT: { label: 'Direct', icon: scaleIcon },
    OTHER: { label: 'Other', icon: scaleIcon },
}

interface Props {
    campaigns: CampaignResponse[],
    siteCode: string
    refresh: () => void
}

export default function CampaignTable({ campaigns, siteCode, refresh }: Props) {
    const [selectedCampaignId, setSelectedCampaignId] = useState<number | null>(null)

    function getStatusBadge(status: CampaignStatus) {
        switch (status) {
            case 'ACTIVE':
                return <p className={`${styles.statusBadge} ${styles.active}`}>Active</p>
            case 'PAUSED':
                return <p className={`${styles.statusBadge} ${styles.pause}`}>Paused</p>
            case 'ARCHIVED':
                return <p className={`${styles.statusBadge} ${styles.archive}`}>Archived</p>
        }
    }

    function getChannelBadge(channel: CampaignChannel) {
        const { label, icon } = CHANNEL_MAP[channel]
        return (
            <div className={styles.channelContainer}>
                <img src={icon} alt={label} width={14} height={14} />
                <p>{label}</p>
            </div>
        )
    }

    function getProgressBar(value: number, target: number, name: string | undefined) {
        const percent = (value / target) * 100
        const progressNum = percent > 100 ? 100 : percent
        const color = progressNum == 100 ? "var(--color-good)" : "var(--color-warning)"
        const progress: React.CSSProperties = {
            width: 64 * progressNum / 100,
            background: color
        };

        return <div>
            <p className={styles.metrics}>{value} {name}</p>
            <div className={styles.progressBar}>
                <div className={styles.bar}>
                    <div className={styles.progress} style={progress}></div>
                </div>
                <p className={progressNum == 100 ? styles.green : styles.red}>{percent}%</p>
            </div>
        </div>
    }

    function openEditPannel(id: number) {
        setSelectedCampaignId(prev => prev === id ? null : id)
    }

    return <section className={styles.tableSection}>
        <div className={styles.table}>
            <div className={`${styles.tableRow} ${styles.header}`}>
                <p>Campaign Name</p>
                <p>Status</p>
                <p>Channel</p>
                <p>Budget</p>
                <p>Performance</p>
                <p>Date Range</p>
            </div>
            {campaigns.map(c => <div key={c.id}>
                <div className={styles.campaignRow}>
                    <div className={styles.tableRow}>
                        <p className={styles.nameColumn}>{c.campaignName}</p>
                        <div>{getStatusBadge(c.status)}</div>
                        {getChannelBadge(c.channel)}
                        <p>{c.cost ? `$ ${c.cost}` : "N/A"}</p>
                        <div>
                            {c.metricValue && c.benchmarkMetricValue ?
                                getProgressBar(c.metricValue, c.benchmarkMetricValue, c.metricName) :
                                "N/A"}
                        </div>
                        <p className={styles.date}>{c.startDate ? c.startDate : "N/A"} <br />~ {c.endDate ? c.endDate : "N/A"}</p>
                    </div>
                    <button className={styles.edit} onClick={() => openEditPannel(c.id)}>
                    </button>
                </div>
                {selectedCampaignId === c.id && (
                    <CreateCampaignPannel
                        siteCode={siteCode}
                        id={c.id}
                        onClose={() => {
                            setSelectedCampaignId(null)
                        }}
                        onSubmit={() => {
                            setSelectedCampaignId(null)
                            refresh()
                        }}
                        campaign={c}
                    />
                )}
            </div>
            )}
        </div>
    </section>
}