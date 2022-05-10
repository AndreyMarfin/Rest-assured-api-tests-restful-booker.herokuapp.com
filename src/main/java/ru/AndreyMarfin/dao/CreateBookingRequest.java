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
        "firstname",
        "lastname",
        "totalprice",
        "depositpaid",
        "bookingdates",
        "additionalneeds"
})

public class CreateBookingRequest {

    @JsonProperty("firstname")
    private String firstname;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("totalprice")
    private Integer totalprice;
    @JsonProperty("depositpaid")
    private Boolean depositpaid;
    @JsonProperty("bookingdates")
    private CreateBookingdatesRequest bookingDates;
    @JsonProperty("additionalneeds")
    private String additionalneeds;

}