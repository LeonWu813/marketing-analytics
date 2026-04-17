import { useState } from "react"
import styles from "../common/CreateCampaignPannel.module.css"
import type { CampaignChannel, CampaignResponse } from "../../types/campaign"
import { createCampaign, deleteCampaign, updateCampaign } from "../../api/campaignApi";
const CHANNEL_OPTIONS: { value: CampaignChannel; label: string }[] = [
    { value: 'ORGANIC', label: 'Organic' },
    { value: 'PAID', label: 'Paid' },
    { value: 'SOCIAL', label: 'Social' },
    { value: 'EMAIL', label: 'Email' },
    { value: 'EVENT', label: 'Event' },
    { value: 'DIRECT', label: 'Direct' },
    { value: 'OTHER', label: 'Other' },
]

interface Props {
    siteCode: string
    onClose: () => void
    onSubmit: () => void
    id: number | null
    campaign: CampaignResponse | null
}

export default function CreateCampaignPannel({ siteCode, onClose, onSubmit, id, campaign }: Props) {

    const [isEdit] = useState<boolean>(!!id)
    const [name, setName] = useState<string>(campaign?.campaignName ?? "")
    const [channel, setChannel] = useState<CampaignChannel | ''>(campaign?.channel ?? '')
    const [description, setDescription] = useState<string>(campaign?.campaignDescription ?? "")
    const [cost, setCost] = useState<number | ''>(campaign?.cost ?? '')
    const [startDate, setStartDate] = useState<string>(campaign?.startDate ?? '')
    const [endDate, setEndDate] = useState<string>(campaign?.endDate ?? '')
    const [metricName, setMetricName] = useState<string>(campaign?.metricName ?? '')
    const [metricValue, setMetricValue] = useState<string>(
        campaign?.metricValue ? String(campaign.metricValue) : ''
    )
    const [benchmarkValue, setBenchmarkValue] = useState<string>(
        campaign?.benchmarkMetricValue ? String(campaign.benchmarkMetricValue) : ''
    )

    function closePannel() {
        setName('')
        setChannel('')
        setDescription('')
        setCost('')
        setStartDate('')
        setEndDate('')
        setMetricName('')
        setMetricValue('')
        setBenchmarkValue('')
        onClose()
    }

    const delCampaign = async (e: React.SyntheticEvent) => {
        if (id) {
            await deleteCampaign(id, siteCode)
            onSubmit()
        }
        closePannel()
    }

    const handleSubmit = async (e: React.SyntheticEvent) => {
        e.preventDefault()
        try {
            if (!id) {
                await createCampaign(siteCode, {
                    campaignName: name,
                    campaignDescription: description || undefined,
                    cost: cost || undefined,
                    startDate: startDate || undefined,
                    endDate: endDate || undefined,
                    channel: channel as CampaignChannel,
                    status: id && campaign ? campaign.status : 'ACTIVE',
                    metricName: metricName || undefined,
                    metricValue: metricValue ? Number(metricValue) : undefined,
                    benchmarkMetricName: metricName || undefined,
                    benchmarkMetricValue: benchmarkValue ? Number(benchmarkValue) : undefined,
                })
                onSubmit()
                closePannel()
            } else {
                await updateCampaign(id, siteCode, {
                    campaignName: name,
                    campaignDescription: description || undefined,
                    cost: cost || undefined,
                    startDate: startDate || undefined,
                    endDate: endDate || undefined,
                    channel: channel as CampaignChannel,
                    status: 'ACTIVE',
                    metricName: metricName || undefined,
                    metricValue: metricValue ? Number(metricValue) : undefined,
                    benchmarkMetricName: metricName || undefined,
                    benchmarkMetricValue: benchmarkValue ? Number(benchmarkValue) : undefined,
                })
                onSubmit()
                closePannel()
            }
        } catch {
        } finally {
        }
    }

    return <section className={styles.newCampaignPannel}>
        {!isEdit &&
            (<p className={styles.newCampaignTitle}>
                New Campaign Details
            </p>)
        }
        <form className={styles.formContainer} onSubmit={handleSubmit}>
            <div className={`form-field-container ${styles.column2}`}>
                <label htmlFor="name" className="label">Campaign Name</label>
                <input required type="text" className="input" id="name" autoComplete='off' placeholder="e.g. Email Outreach 2025-03" onChange={(e) => setName(e.target.value)} value={name} />
            </div>
            <div className="form-field-container">
                <label htmlFor="channel" className="label">Channel</label>
                <select
                    id="channel"
                    className="input select"
                    value={channel}
                    onChange={(e) => setChannel(e.target.value as CampaignChannel)}
                    required
                >
                    <option value="" disabled>Select a channel</option>
                    {CHANNEL_OPTIONS.map(opt => (
                        <option key={opt.value} value={opt.value}>
                            {opt.label}
                        </option>
                    ))}
                </select>
            </div>
            <div className={`form-field-container ${styles.column3}`}>
                <label htmlFor="description" className="label">Description</label>
                <textarea className="input" rows={2} id="description" autoComplete='off' placeholder="Outline the primary objectives and target audience..." onChange={(e) => setDescription(e.target.value)} value={description}></textarea>
            </div>
            <div className="form-field-container">
                <label htmlFor="cost" className="label">Budget Cost</label>
                <input type="number" className="input" id="cost" autoComplete='off' placeholder="ex. 500.00" onChange={(e) => setCost(Number(e.target.value))} value={cost} />
            </div>
            <div className="form-field-container">
                <label htmlFor="startDate" className="label">Start Date</label>
                <input type="date" className="input" id="startDate" autoComplete='off' onChange={(e) => setStartDate(e.target.value)} value={startDate} />
            </div>
            <div className="form-field-container">
                <label htmlFor="endDate" className="label">End Date</label>
                <input type="date" className="input" id="endDate" autoComplete='off' onChange={(e) => setEndDate(e.target.value)} value={endDate} />
            </div>
            <div className="form-field-container">
                <label htmlFor="metricName" className="label">Metric</label>
                <input type="text" className="input" id="metricName" autoComplete='off' placeholder="ex. Form submissions" onChange={(e) => setMetricName(e.target.value)} value={metricName} />
            </div>
            <div className="form-field-container">
                <label htmlFor="endDate" className="label">Metric Value</label>
                <input type="number" className="input" id="endDate" autoComplete='off' placeholder="ex. 20" onChange={(e) => setMetricValue(e.target.value)} value={metricValue} />
            </div>
            <div className="form-field-container">
                <label htmlFor="endDate" className="label">Benchmark Value</label>
                <input type="number" className="input" id="endDate" autoComplete='off' placeholder="ex. 20" onChange={(e) => setBenchmarkValue(e.target.value)} value={benchmarkValue} />
            </div>
            <div className={`${styles.column3} ${styles.submitContainer}`}>
                <button type="button" onClick={() => closePannel()} className={styles.cancel}>Cancel</button>
                {
                    isEdit && <button type="button" onClick={delCampaign} className="button-secondary button-small">Delete</button>
                }
                <button type="submit" className="button-primary button-small">Save Campaign</button>
            </div>
        </form>
    </section>
}