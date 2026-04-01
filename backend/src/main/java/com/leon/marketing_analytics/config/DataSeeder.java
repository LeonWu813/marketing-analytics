package com.leon.marketing_analytics.config;

import com.leon.marketing_analytics.entity.CampaignChannel;
import com.leon.marketing_analytics.entity.Event;
import com.leon.marketing_analytics.entity.Site;
import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.repository.EventRepository;
import com.leon.marketing_analytics.repository.SiteRepository;
import com.leon.marketing_analytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SiteRepository siteRepository;
    private final EventRepository eventRepository;
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        // Already create this user in database
        User user = userRepository.getByEmail("test@example.co")
                .orElseThrow(()-> new AccessDeniedException("Please create a user: test@example.co"));

        Site site = siteRepository.getBySiteName("Test Site")
                .orElseGet(() -> siteRepository.save(Site.builder()
                        .siteName("Test Site").siteCode(UUID.randomUUID().toString()).user(user).build()));

        if (eventRepository.findBySite(site, PageRequest.of(0, 10)).getTotalElements() > 50L) {
            System.out.println("DataSeeder: data already present, skipping.");
            return;
        }

        String[] eventTypes = {"Visit", "Button Click", "Form Submission"};
        String[] pageUrls = {"/", "/blog", "/product"};
        CampaignChannel[] channels = {CampaignChannel.ORGANIC, CampaignChannel.EMAIL, CampaignChannel.DIRECT};
        List<Event> batch = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            Event event = Event.builder()
                    .eventType(eventTypes[random.nextInt(eventTypes.length)])
                    .site(site)
                    .pageUrl(pageUrls[random.nextInt(pageUrls.length)])
                    .channel(channels[random.nextInt(channels.length)])
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(90)))
                    .build();
            batch.add(event);

            if (batch.size() == 500) {
                eventRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            eventRepository.saveAll(batch);
        }

        System.out.println("DataSeeder: inserted 500 events.");
    }
}
