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

export function formatDate(date: string) {
    const dateSplit: string[] = date.split("T")
    const time: string[] = dateSplit[1].split(":")
    const hour: number = Number(time[0])
    return dateSplit[0] + " • " +
        (hour > 12 ? hour - 12 : hour) + ":" +
        time[1] + (hour > 12 ? " PM" : " AM")
}