export interface CreateSiteRequest {
    siteName: string
    siteDomain: string
}

export interface SiteResponse {
    siteId: number
    siteCode: string
    siteName: string
    siteDomain: string
}