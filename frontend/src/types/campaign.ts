export type CampaignStatus = "ACTIVE" | "PAUSED" | "ARCHIVED"

export type CampaignChannel = "ORGANIC" | "PAID" | "SOCIAL" |
    "EMAIL" | "EVENT" | "DIRECT" | "OTHER"

export interface UpdateCampaignRequest {
    campaignName: string
    campaignDescription?: string
    cost?: number
    startDate?: string
    endDate?: string
    status: CampaignStatus
    channel: CampaignChannel
    metricName?: string
    metricValue?: number
    benchmarkMetricName?: string
    benchmarkMetricValue?: number
}

export interface CreateCampaignRequest {
    campaignName: string
    campaignDescription?: string
    cost?: number
    startDate?: string
    endDate?: string
    status: CampaignStatus
    channel: CampaignChannel
    metricName?: string
    metricValue?: number
    benchmarkMetricName?: string
    benchmarkMetricValue?: number
}

export interface CampaignResponse {
    id: number
    createdAt: string
    campaignName: string
    campaignDescription?: string
    cost?: number
    startDate?: string
    endDate?: string
    status: CampaignStatus
    channel: CampaignChannel
    metricName?: string
    metricValue?: number
    benchmarkMetricName?: string
    benchmarkMetricValue?: number
    isArchived: boolean
}