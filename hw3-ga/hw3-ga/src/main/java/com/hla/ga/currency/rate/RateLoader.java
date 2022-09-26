package com.hla.ga.currency.rate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static java.util.Arrays.asList;

@Slf4j
@Component
@RequiredArgsConstructor
class RateLoader {

    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final LocalDate FROM_DATE = LocalDate.of(2022, 1, 1);
    private static final LocalDate TO_DATE = LocalDate.now();

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();
    @Value("${currency-rate.rates-api-url}") private final String apiUrl;

    Collection<Rate> load() {
        try {
            return loadRatesForPeriod(FROM_DATE, TO_DATE);
        } catch (final RuntimeException e) {
            log.error(String.format("Loading rates failed for period from %s to %s", FROM_DATE, TO_DATE), e);
            return List.of();
        }
    }

    private Collection<Rate> loadRatesForPeriod(final LocalDate fromDate, final LocalDate toDate) {
        log.info("Loading rates for period from {} to {}", fromDate, toDate);

        final var fromDateFormatted = fromDate.format(API_DATE_FORMATTER);
        final var toDateFormatted = toDate.format(API_DATE_FORMATTER);
        final var rates = restTemplate.getForObject(apiUrl, Rate[].class, fromDateFormatted, toDateFormatted);

        log.info("Rates {} have been loaded for date period from {} to {}", rates, fromDate, toDate);

        return asList(rates);
    }
}
