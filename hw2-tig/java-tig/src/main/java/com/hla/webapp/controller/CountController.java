package com.hla.webapp.controller;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/get-counter")
@RequiredArgsConstructor
public class CountController {

    private static final String METRIC_NAME = "call-counter";

    private final MeterRegistry meterRegistry;

    @GetMapping
    Double getCount() {
        final var counter = meterRegistry.counter(METRIC_NAME);
        log.info(counter.getId().toString());
        counter.increment();
        return counter.count();
    }
}
