package com.hla.ga.currency.rate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
class RateEvent {

    @JsonProperty("client_id") String clientId;
    @JsonProperty("timestamp_micros") String timestamp;
    @JsonProperty("non_personalized_ads") boolean nonPersonalized = true;


}
