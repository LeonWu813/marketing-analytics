package com.leon.marketing_analytics.service;

import com.leon.marketing_analytics.dto.EventIngestRequest;
import com.leon.marketing_analytics.dto.EventResponse;
import com.leon.marketing_analytics.entity.*;
import com.leon.marketing_analytics.exception.ForbiddenException;
import com.leon.marketing_analytics.exception.ResourceNotFoundException;
import com.leon.marketing_analytics.repository.CampaignRepository;
import com.leon.marketing_analytics.repository.EventRepository;
import com.leon.marketing_analytics.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SiteRepository siteRepository;
    private final CampaignRepository campaignRepository;

    private CampaignChannel categorizeChannel(String utmMedium){
        if (utmMedium == null) return CampaignChannel.OTHER;
        return switch (utmMedium.toLowerCase()) {
            case "organic" -> CampaignChannel.ORGANIC;
            case "cpc" -> CampaignChannel.PAID;
            case "social" -> CampaignChannel.SOCIAL;
            case "email" -> CampaignChannel.EMAIL;
            case "event" -> CampaignChannel.EVENT;
            default -> CampaignChannel.DIRECT;
        };
    }

    private EventResponse toResponse(Event event){
        return new EventResponse(
                event.getId(),
                event.getEventType(),
                event.getPageUrl(),
                event.getCampaign() != null ? event.getCampaign().getId() : null,
                event.getUtmSource(),
                event.getUtmMedium(),
                event.getUtmCampaign(),
                event.getChannel(),
                event.getCountry(),
                event.getUserIdentifier(),
                event.getMetadata(),
                event.getCreatedAt()
        );
    }

    private Site getSite(String siteCode){
        return siteRepository.findBySiteCode(siteCode).orElseThrow(()->
                new ResourceNotFoundException("Site not found."));
    }

    @Transactional
    public EventResponse createEvent(EventIngestRequest request, String ipAddress){
        Site site = getSite(request.siteCode());

        Campaign campaign = (request.campaignId() != null)
                ? campaignRepository.findById(request.campaignId()).orElse(null)
                : null;


        Event event = Event.builder()
                .site(site)
                .campaign(campaign)
                .eventType(request.eventType())
                .pageUrl(request.pageUrl())
                .utmSource(request.utmSource())
                .utmMedium(request.utmMedium())
                .utmCampaign(request.utmCampaign())
                .metadata(request.metadata())
                .userIdentifier(request.userIdentifier())
                .channel(categorizeChannel(request.utmMedium()))
                .country("Unknown")
                .build();

        eventRepository.save(event);

        return toResponse(event);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getEvents(String siteCode,
                                         Long campaignId,
                                         String eventType,
                                         CampaignChannel channel,
                                         LocalDate startDate,
                                         LocalDate endDate,
                                         Pageable pageable,
                                         User currentUser){
        Site site = getSite(siteCode);
        if (!site.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Access forbidden");
        }

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end   = endDate   != null ? endDate.atTime(LocalTime.MAX) : null;


        Page<Event> events = eventRepository.findBySiteWithFilters(site.getId(),
                campaignId, eventType, channel, start, end, pageable);

        return events.map(this::toResponse);
    }
}
