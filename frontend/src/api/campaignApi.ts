import type { CampaignChannel, CampaignResponse, CampaignStatus, CreateCampaignRequest, UpdateCampaignRequest } from "../types/campaign";
import axiosInstance from "./axiosInstance";
import type { PageResponse } from '../types/common'

export function createCampaign(siteCode: string,
    data: CreateCampaignRequest): Promise<CampaignResponse> {
    return axiosInstance.post<CampaignResponse>(`/api/${siteCode}/campaigns`, data)
        .then(res => res.data)
}

export function getCampaign(
    siteCode: string,
    id: number,
): Promise<CampaignResponse> {
    return axiosInstance.get(`/api/${siteCode}/campaigns/${id}`).then(res => res.data)
}

export function getCampaigns(
    siteCode: string,
    status?: CampaignStatus,
    channel?: CampaignChannel,
    page: number = 0
): Promise<PageResponse<CampaignResponse>> {
    return axiosInstance.get<PageResponse<CampaignResponse>>(`/api/${siteCode}/campaigns`, {
        params: {
            status: status ?? undefined,
            channel: channel ?? undefined,
            page: page,
            size: 20
        }
    }).then(res => res.data)
}

export function updateCampaign(
    id: number,
    siteCode: string,
    update: UpdateCampaignRequest
): Promise<CampaignResponse> {
    return axiosInstance.put<CampaignResponse>(`/api/${siteCode}/campaigns/${id}`, update).then(res => res.data)
}

export function deleteCampaign(
    id: number,
    siteCode: string
): Promise<void> {
    return axiosInstance.delete(`/api/${siteCode}/campaigns/${id}`).then(() => { })
}