export type CheckStatus = "PASS" | "WARN" | "FAIL"

export interface SeoCheckResult {
    checkName: string
    checkStatus: CheckStatus
    details: string
}

export interface SeoReportRequest {
    analyzedUrl: string
    keyword?: string
    isFollowUp: boolean
}

export interface SeoReportResponse {
    id: number
    siteCode: string
    analyzedUrl: string
    keyword?: string
    analyzedAt: string
    checks: SeoCheckResult[]
    performanceScore: number
    seoScore: number
    lcpSeconds: string
    fcpSeconds: string
    tbtMilliseconds: string
    runWarnings: string[]
}