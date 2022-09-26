package com.hla.ga.currency.rate;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateUpdater {

    private final RateTracker rateTracker;
    private final RateLoader rateLoader;

    @PostConstruct
    void update() {
        log.info("Updating rates");

        rateLoader.load().stream()
            .forEach(rateTracker::track);

        log.info("Rates have been updated");
    }
}
