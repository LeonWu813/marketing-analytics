import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import type { SiteResponse } from "../types/site";
import { getSite } from "../api/siteApi";
import Breadcrumb from "../components/common/Breadcrumb";
import styles from "../pages/CampaignsPage.module.css"
import CreateCampaignPannel from "../components/common/CreateCampaignPannel";
import type { CampaignChannel, CampaignResponse, CampaignStatus } from "../types/campaign";
import { getCampaigns } from "../api/campaignApi";
import CampaignTable from "../components/common/CampaignTable";
import Filter from "../components/common/Filter";

const STATUS_FILTERS: { label: string; value: CampaignStatus | undefined }[] = [
    { label: 'All', value: undefined },
    { label: 'Active', value: 'ACTIVE' },
    { label: 'Paused', value: 'PAUSED' },
    { label: 'Archived', value: 'ARCHIVED' },
]

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

export default function CampaignsPage() {
    const navigate = useNavigate()
    const location = useLocation()

    const { siteCode } = useParams<{ siteCode: string }>();
    const [site, setSite] = useState<SiteResponse | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [createSitePannel, setCreateSitePannel] = useState<boolean>(
        location.state?.openCreate ?? false
    )
    const [campaigns, setCampaigns] = useState<CampaignResponse[]>([])
    const [haveCampaigns, setHaveCampaigns] = useState<boolean>(true)
    const [selectedStatus, setSelectedStatus] = useState<CampaignStatus | undefined>(undefined)
    const [selectedChannel, setSelectedChannel] = useState<CampaignChannel | undefined>(undefined)

    async function refreshCampaigns(status?: CampaignStatus, channel?: CampaignChannel) {
        if (!siteCode) return
        try {
            const data = await getCampaigns(siteCode, status, channel)
            setCampaigns(data.content)
        } catch {
            setHaveCampaigns(false)
        }
    }

    useEffect(() => {
        if (!siteCode) {
            navigate('/app/404')
            return
        }
        getSite(siteCode).then(data => setSite(data))
            .catch(() => setError('Failed to load site'))
            .finally(() => setLoading(false))

        refreshCampaigns(selectedStatus, selectedChannel)
    }, [siteCode])

    useEffect(() => {
        if (campaigns.length == 0) {
            setHaveCampaigns(false)
        } else {
            setHaveCampaigns(true)
        }
    }, [campaigns])

    if (loading) return <p>Loading...</p>
    if (error || !siteCode) return <p style={{ color: 'red' }}>{error}</p>
    if (!site) return null

    function handleStatusFilter(status: CampaignStatus | undefined) {
        setSelectedStatus(status)
        refreshCampaigns(status, selectedChannel)
    }

    function handelChannelFilter(channel: CampaignChannel | undefined) {
        setSelectedChannel(channel)
        refreshCampaigns(selectedStatus, channel)
    }

    return <>
        <section className={styles.heroSection}>
            <Breadcrumb
                first={site.siteName}
                second="Campaigns"
            />
            <div className={styles.titleContainer}>
                <h1>Campaigns</h1>
                {!createSitePannel && <button
                    className={`button-primary button-with-icon button-small ${styles.addButton}`}
                    onClick={() => setCreateSitePannel(!createSitePannel)}
                >
                    Create Campaign
                </button>}
            </div>
        </section>
        {createSitePannel && < CreateCampaignPannel siteCode={siteCode} onClose={() => setCreateSitePannel(false)} onSubmit={() => refreshCampaigns(selectedStatus, selectedChannel)} id={null} campaign={null} />}
        <section className={styles.filterSection}>
            <div className={styles.statusFilter}>
                {STATUS_FILTERS.map(filter => (
                    <button
                        key={filter.label}
                        onClick={() => handleStatusFilter(filter.value)}
                        className={
                            selectedStatus === filter.value
                                ? `${styles.filterBtn} ${styles.filterBtnActive}`
                                : styles.filterBtn
                        }
                    >
                        {filter.label}
                    </button>
                ))}
            </div>
            <Filter
                label="Channel"
                options={STATUS_CHANNEL}
                onSelect={(value) => handelChannelFilter(value)}
                large={true}
            />
        </section>
        {haveCampaigns ?
            <CampaignTable campaigns={campaigns} siteCode={siteCode} refresh={() => refreshCampaigns()} /> :
            <div className={styles.noCampaignContainer}><p>No Campaign</p></div>}
    </>
}