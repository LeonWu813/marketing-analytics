import type { PageResponse } from "../types/common";
import type { SeoReportRequest, SeoReportResponse } from "../types/seo_report";
import axiosInstance from "./axiosInstance";


export function createReport(data: SeoReportRequest, siteCode: string): Promise<SeoReportResponse> {
    return axiosInstance.post<SeoReportResponse>(`/api/${siteCode}/seo/analyze`, data).then(res => res.data)
}

export function getReports(
    siteCode: string,
    analyzedUrl?: string,
    page: number = 0
): Promise<PageResponse<SeoReportResponse>> {
    return axiosInstance.get<PageResponse<SeoReportResponse>>(`/api/${siteCode}/seo/reports`, {
        params: {
            analyzedUrl: analyzedUrl ?? undefined,
            page: page,
            size: 20
        }
    }).then(res => res.data)
}