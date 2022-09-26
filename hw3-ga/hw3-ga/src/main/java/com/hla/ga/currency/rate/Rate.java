package com.hla.ga.currency.rate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Value;

@Value
class Rate {

    @JsonProperty("exchangedate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    LocalDate date;

    Double rate;
    String cc;

}
