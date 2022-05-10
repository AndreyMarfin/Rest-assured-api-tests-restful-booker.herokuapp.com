package ru.AndreyMarfin.tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.AndreyMarfin.dao.CreateBookingRequest;
import ru.AndreyMarfin.dao.CreateBookingdatesRequest;
import ru.AndreyMarfin.dao.CreateTokenRequest;
import ru.AndreyMarfin.dao.CreateTokenResponse;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@Severity(SeverityLevel.BLOCKER)

@DisplayName("Delete a booking")
@Feature("Delete a booking")
public class DeleteBookingTests extends BaseTest{

    final static Logger log = LoggerFactory.getLogger(PartialUpdateBookingTests.class);

    @BeforeAll
    static void beforeAll() {
        log.info("Data preparation");
        log.info("Create a token");
        RestAssured.baseURI = properties.getProperty("base.url");
        requestToken = CreateTokenRequest.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build();
        log.info(requestToken.toString());

        responseToken = given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(requestToken)
                .expect()
                .statusCode(200)
                .when()
                .post("auth")
                .prettyPeek()
                .then()
                .extract()
                .as(CreateTokenResponse.class);
        assertThat(responseToken.getToken().length(), equalTo(15));
        log.info("The token is: " + responseToken.getToken());

        log.info("Data preparation");
        log.info("Create a booking dates");
        requestBookingDates = CreateBookingdatesRequest.builder()
                .checkin(properties.getProperty("checkin"))
                .checkout(properties.getProperty("checkout"))
                .build();
        log.info(requestBookingDates.toString());

        log.info("Data preparation");
        log.info("Create a booking");
        requestBooking = CreateBookingRequest.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.hashCode())
                .depositpaid(faker.bool().bool())
                .bookingDates(requestBookingDates)
                .additionalneeds(faker.chuckNorris().fact())
                .build();
        log.info(requestBooking.toString());
    }

    @BeforeEach
    void setUp() {
        id = given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(requestBooking)
                .expect()
                .statusCode(200)
                .when()
                .post("booking")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("bookingid")
                .toString();
        log.info("Booking id is: " + id);
    }

    @Test
    @DisplayName("(P)Deleting a booking with authorization via cookies")
    @Description("Positive test for deleting a booking with authorization via cookies")
    @Step("Deleting a booking with authorization via cookies")
    void deleteBookingCookiePositiveTest() {
        log.info("Start test: Deleting a booking with authorization via cookies");
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Cookie", "token=" + responseToken.getToken())
                .when()
                .delete("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);
        log.info("End test");
    }

    @Test
    @DisplayName("(N)Deleting a booking with authorization via hard token")
    @Description("Negative test for deleting a booking with authorization via hard token")
    @Step("Deleting a booking with authorization via hard token")
    void deleteBookingAuthorisationNegativeTest() {
        log.info("Start test: Deleting a booking with authorization via hard token");
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Authorisation", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
        log.info("End test");
    }

    @Test
    @DisplayName("(P)Deleting a booking with authorization via hard token")
    @Description("Positive test for deleting a booking with authorization via hard token")
    @Step("Deleting a booking with authorization via hard token")
    void deleteBookingAuthorizationPositiveTest() {
        log.info("Start test: Deleting a booking with authorization via hard token");
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);
        log.info("End test");
    }

    @Test
    @DisplayName("(N)Deleting a booking without authorization")
    @Description("Negative test for deleting a booking without authorization")
    @Step("Deleting a booking without authorization")
    void deleteBookingWithoutAuthorisationNegativeTest() {
        log.info("Start test: Deleting a booking without authorization");
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .when()
                .delete("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
        log.info("End test");
    }
}

