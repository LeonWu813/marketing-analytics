import type { CampaignChannel } from './campaign'

export interface EventIngestRequest {
    eventType: string
    pageUrl: string
    siteCode: string
    utmSource?: string
    utmMedium?: string
    utmCampaign?: string
    campaignId?: number
    userIdentifier?: string
    metadata?: Record<string, unknown>
}

export interface EventResponse {
    id: number
    eventType: string
    pageUrl: string
    campaignId?: number
    utmSource?: string
    utmMedium?: string
    utmCampaign?: string
    channel?: CampaignChannel
    country?: string
    userIdentifier?: string
    metadata?: Record<string, unknown>
    createdAt: string
}

export interface EventTypeCount {
    eventType: string
    count: number
}