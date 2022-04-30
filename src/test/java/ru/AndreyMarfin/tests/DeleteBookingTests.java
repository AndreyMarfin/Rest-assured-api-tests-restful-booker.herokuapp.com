package ru.AndreyMarfin.tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.AndreyMarfin.dao.CreateBookingdatesRequest;
import ru.AndreyMarfin.dao.CreateBookingRequest;
import ru.AndreyMarfin.dao.CreateTokenRequest;
import ru.AndreyMarfin.dao.CreateTokenResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeleteBookingTests {
    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    static Properties properties = new Properties();
    String id;
    private static CreateTokenRequest request;
    private static CreateTokenResponse response;
    private static CreateBookingdatesRequest bookingDates;
    private static CreateBookingRequest booking;

    static Faker faker = new Faker();

    @BeforeAll
    static void beforeAll() throws IOException {
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");
        request = CreateTokenRequest.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build();

        response = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .when()
                .post("auth")
                .prettyPeek()
                .then()
                .extract()
                .as(CreateTokenResponse.class);
        assertThat(response.getToken().length(), equalTo(15));

        bookingDates = CreateBookingdatesRequest.builder()
                .checkin(properties.getProperty("checkin"))
                .checkout(properties.getProperty("checkout"))
                .build();

        booking = CreateBookingRequest.builder()
                .firstname(faker.name().firstName())
                .lastname(faker.name().lastName())
                .totalprice(faker.hashCode())
                .depositpaid(faker.bool().bool())
                .bookingDates(bookingDates)
                .additionalneeds(faker.chuckNorris().fact())
                .build();

    }

    @BeforeEach
    void setUp() {
        id = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(booking)
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
    void deleteBookingCookiePositiveTest() {
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Cookie", "token=" + response.getToken())
                .when()
                .delete("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }

    @Test
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

