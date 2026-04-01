export interface ReportSendResponse {
    id: number
    sentTo: string
    sentAt: string
    reportId: number
}

export interface ReportSendRequest {
    sentTo: string
}