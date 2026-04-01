import type { CreateSiteRequest, SiteResponse } from "../types/site";
import axiosInstance from "./axiosInstance";

export function createSite(data: CreateSiteRequest): Promise<SiteResponse> {
    return axiosInstance.post<SiteResponse>('/api/sites', data)
        .then(res => res.data)
}

export function getSite(siteCode: string): Promise<SiteResponse> {
    return axiosInstance.get<SiteResponse>(`/api/sites/${siteCode}`).then(res => res.data)
}

export function getSites(): Promise<SiteResponse[]> {
    return axiosInstance.get<SiteResponse[]>('/api/sites').then(res => res.data)
}

export function deleteSite(siteCode: string): Promise<void> {
    return axiosInstance.delete(`/api/sites/${siteCode}`).then(() => { })
}