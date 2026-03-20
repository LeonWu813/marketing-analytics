package com.leon.marketing_analytics.service;

import com.leon.marketing_analytics.dto.CampaignResponse;
import com.leon.marketing_analytics.dto.CreateCampaignRequest;
import com.leon.marketing_analytics.dto.UpdateCampaignRequest;
import com.leon.marketing_analytics.entity.Campaign;
import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.CampaignStatus;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.exception.ForbiddenException;
import com.leon.marketing_analytics.exception.ResourceNotFoundException;
import com.leon.marketing_analytics.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignService campaignService;

    private User testUser;
    private User otherUser;
    private Campaign testCampaign;
    private String testCampaignName;
    private CampaignChannel testCampaignChannel;
    private CampaignStatus testCampaignStatus;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@email.com")
                .passwordHash("pwd")
                .build();

        otherUser = User.builder()
                .id(2L)
                .email("other@email.com")
                .passwordHash("pwd")
                .build();

        testCampaignName = "Testing Campaign";
        testCampaignChannel = CampaignChannel.ORGANIC;
        testCampaignStatus = CampaignStatus.ACTIVE;
        testPageable = PageRequest.of(0, 10);

        testCampaign = Campaign.builder()
                .id(1L)
                .user(testUser)
                .campaignName(testCampaignName)
                .channel(testCampaignChannel)
                .status(testCampaignStatus)
                .build();
    }

    @Test
    void createCampaign_validRequest_savesAndReturnsResponse() {
        CreateCampaignRequest requestTest = new CreateCampaignRequest(
                "Different Name", null, null, null, null,
                CampaignStatus.ACTIVE, CampaignChannel.EMAIL,
                null, null, null, null
        );
        when(campaignRepository.save(any(Campaign.class))).thenReturn(testCampaign);

        CampaignResponse testResponse = campaignService.createCampaign(requestTest, testUser);

        verify(campaignRepository, times(1)).save(any(Campaign.class));
        assertEquals(testCampaignName, testResponse.campaignName());
        assertEquals(testCampaignChannel, testResponse.channel());
    }

    @Test
    void getCampaignById_validIdAndOwner_returnsResponse() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));

        CampaignResponse response = campaignService.getCampaignById(1L, testUser);

        assertEquals(testCampaignName, response.campaignName());
        assertEquals(testCampaignChannel, response.channel());
    }

    @Test
    void getCampaignById_invalidId_throwsResourceNotFoundException() {
        when(campaignRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> campaignService.getCampaignById(99L, testUser));

        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void getCampaignById_wrongOwner_throwsForbiddenException() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));

        assertThrows(ForbiddenException.class,
                () -> campaignService.getCampaignById(1L, otherUser));
    }

    @Test
    void getCampaigns_withStatusAndChannel_callsCorrectRepositoryMethod() {
        Page<Campaign> resultPage = new PageImpl<>(List.of(testCampaign), testPageable, 1);
        when(campaignRepository.findAllByUserAndStatusAndChannelAndIsArchivedFalse(
                testUser, testCampaignStatus, testCampaignChannel, testPageable))
                .thenReturn(resultPage);

        Page<CampaignResponse> result = campaignService
                .getCampaigns(testUser, testCampaignStatus, testCampaignChannel, testPageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getCampaigns_withChannelOnly_callsCorrectRepositoryMethod() {
        Page<Campaign> resultPage = new PageImpl<>(List.of(testCampaign), testPageable, 1);
        when(campaignRepository.findAllByUserAndChannelAndIsArchivedFalse(
                testUser, testCampaignChannel, testPageable))
                .thenReturn(resultPage);

        Page<CampaignResponse> result = campaignService
                .getCampaigns(testUser, null, testCampaignChannel, testPageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getCampaigns_withStatusOnly_callsCorrectRepositoryMethod() {
        Page<Campaign> resultPage = new PageImpl<>(List.of(testCampaign), testPageable, 1);
        when(campaignRepository.findAllByUserAndStatusAndIsArchivedFalse(
                testUser, testCampaignStatus, testPageable))
                .thenReturn(resultPage);

        Page<CampaignResponse> result = campaignService
                .getCampaigns(testUser, testCampaignStatus, null, testPageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getCampaigns_withNoFilters_callsCorrectRepositoryMethod() {
        Page<Campaign> resultPage = new PageImpl<>(List.of(testCampaign), testPageable, 1);
        when(campaignRepository.findAllByUserAndIsArchivedFalse(testUser, testPageable))
                .thenReturn(resultPage);

        Page<CampaignResponse> result = campaignService
                .getCampaigns(testUser, null, null, testPageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getCampaigns_userWithNoCampaigns_returnsEmptyPage() {
        Page<Campaign> emptyPage = new PageImpl<>(Collections.emptyList(), testPageable, 0);
        when(campaignRepository.findAllByUserAndIsArchivedFalse(otherUser, testPageable))
                .thenReturn(emptyPage);

        Page<CampaignResponse> result = campaignService
                .getCampaigns(otherUser, null, null, testPageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateCampaign_validIdAndOwner_updatesAndReturnsResponse() {
        String updatedName = "Updated Campaign";
        UpdateCampaignRequest updateRequest = new UpdateCampaignRequest(
                updatedName, null, null, null, null,
                CampaignStatus.ACTIVE, CampaignChannel.ORGANIC,
                null, null, null, null
        );
        Campaign updatedCampaign = Campaign.builder()
                .id(1L).user(testUser).campaignName(updatedName)
                .channel(testCampaignChannel).status(CampaignStatus.ACTIVE).build();

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(updatedCampaign);

        CampaignResponse response = campaignService.updateCampaign(1L, updateRequest, testUser);

        verify(campaignRepository, times(1)).save(any(Campaign.class));
        assertEquals(updatedName, response.campaignName());
        assertEquals(testCampaignChannel, response.channel());
    }

    @Test
    void updateCampaign_invalidId_throwsResourceNotFoundException() {
        when(campaignRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> campaignService.updateCampaign(99L, any(), testUser));

        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void updateCampaign_wrongOwner_throwsForbiddenException() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));

        assertThrows(ForbiddenException.class,
                () -> campaignService.updateCampaign(1L, any(), otherUser));

        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void deleteCampaign_validIdAndOwner_setsArchivedAndStatus() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));

        campaignService.deleteCampaign(1L, testUser);

        verify(campaignRepository, times(1)).save(any(Campaign.class));
        assertEquals(CampaignStatus.END, testCampaign.getStatus());
        assertTrue(testCampaign.isArchived());
    }

    @Test
    void deleteCampaign_invalidId_throwsResourceNotFoundException() {
        when(campaignRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> campaignService.deleteCampaign(99L, testUser));

        verify(campaignRepository, never()).save(any(Campaign.class));
    }

    @Test
    void deleteCampaign_wrongOwner_throwsForbiddenException() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));

        assertThrows(ForbiddenException.class,
                () -> campaignService.deleteCampaign(1L, otherUser));

        verify(campaignRepository, never()).save(any(Campaign.class));
    }
}