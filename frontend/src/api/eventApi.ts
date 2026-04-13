import type { CampaignChannel } from "../types/campaign";
import type { EventResponse } from "../types/event";
import axiosInstance from "./axiosInstance";

export function getEvents(
    siteCode: string,
    eventType?: string,
    channel?: CampaignChannel,
    utmSource?: string,
    utmMedium?: string,
    country?: string,
    startDate?: string,
    endDate?: string
): Promise<EventResponse[]> {
    return axiosInstance.get<EventResponse[]>(`/api/events`, {
        params: {
            siteCode,
            eventType,
            channel,
            utmSource,
            utmMedium,
            country,
            startDate,
            endDate,
        }
    }).then(res => res.data)
}