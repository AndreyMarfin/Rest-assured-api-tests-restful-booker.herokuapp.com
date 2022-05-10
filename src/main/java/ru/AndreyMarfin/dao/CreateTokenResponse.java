package ru.AndreyMarfin.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
@Getter
public class CreateTokenResponse {

    @JsonProperty("token")
    private String token;
    @JsonProperty("reason")
    private String reason;

}
