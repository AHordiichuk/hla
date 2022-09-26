package com.hla.ga.currency.rate;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateTracker {

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();
    @Value("classpath:gaRequestTemplate.json") private final Resource requestTemplate;
    @Value("${currency-rate.tracker.ga.client-id}") private final String clientId;
    @Value("${currency-rate.tracker.ga.api-url}") private final String apiUrl;

    @SneakyThrows
    void track(final Rate rate) {
        log.info("Track rate {}", rate);

        final var requestPayload = Files.readString(requestTemplate.getFile().toPath())
            .replace("${clientId}", clientId)
            .replace("${eventTs}", getEventTs(rate.getDate()))
            .replace("${rate}", rate.getRate().toString())
            .replace("${currency}", rate.getCc());

        restTemplate.postForEntity(apiUrl, requestPayload, Object.class);

        log.info("Rate {} has been tracked", rate);
    }

    private static String getEventTs(final LocalDate date) {
        return String.valueOf(
            date.atTime(LocalTime.MIDNIGHT)
                .atZone(ZoneId.of("GMT"))
                .toInstant()
                .toEpochMilli() * 1000
        );
    }
}
