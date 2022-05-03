package ru.AndreyMarfin.tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = properties.getProperty("base.url");
        requestToken = CreateTokenRequest.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build();

        responseToken = given()
                .log()
                .all()
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

        requestBookingDates = CreateBookingdatesRequest.builder()
                .checkin(properties.getProperty("checkin"))
                .checkout(properties.getProperty("checkout"))
                .build();

        requestBooking = CreateBookingRequest.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.hashCode())
                .depositpaid(faker.bool().bool())
                .bookingDates(requestBookingDates)
                .additionalneeds(faker.chuckNorris().fact())
                .build();
    }

    @BeforeEach
    void setUp() {
        id = given()
                .log()
                .all()
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
    }

    @Test
    @DisplayName("(P)Deleting a booking with authorization via cookies")
    @Description("Positive test for deleting a booking with authorization via cookies")
    @Step("Deleting a booking with authorization via cookies")
    void deleteBookingCookiePositiveTest() {
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
    }

    @Test
    @DisplayName("(N)Deleting a booking with authorization via hard token")
    @Description("Negative test for deleting a booking with authorization via hard token")
    @Step("Deleting a booking with authorization via hard token")
    void deleteBookingAuthorisationNegativeTest() {
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
    }

    @Test
    @DisplayName("(P)Deleting a booking with authorization via hard token")
    @Description("Positive test for deleting a booking with authorization via hard token")
    @Step("Deleting a booking with authorization via hard token")
    void deleteBookingAuthorizationPositiveTest() {
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
    }

    @Test
    @DisplayName("(N)Deleting a booking without authorization")
    @Description("Negative test for deleting a booking without authorization")
    @Step("Deleting a booking without authorization")
    void deleteBookingWithoutAuthorisationNegativeTest() {
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
    }
}

