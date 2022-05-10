package ru.AndreyMarfin.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.ToString;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@With
@ToString
@JsonPropertyOrder({
        "checkin",
        "checkout"
})
public class CreateBookingdatesRequest {

    @JsonProperty("checkin")
    private String checkin;
    @JsonProperty("checkout")
    private String checkout;

}