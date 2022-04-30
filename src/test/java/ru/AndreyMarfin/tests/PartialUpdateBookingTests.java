package ru.AndreyMarfin.tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import ru.AndreyMarfin.dao.*;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PartialUpdateBookingTests {
    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    static Properties properties = new Properties();
    static String id;
    private static CreateTokenRequest requestToken;
    private static CreateTokenResponse responseToken;
    private static CreateBookingdatesRequest requestBookingDates;
    private static CreateBookingRequest requestBooking;

    static Faker faker = new Faker();

    @BeforeAll
    static void beforeAll() throws IOException {
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
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

    @AfterEach
    void tearDown() {
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
    void updateBookingCookiePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(requestBooking.withFirstname("Garry")
                        .withLastname("Potter")
                        .withTotalprice(123)
                        .withDepositpaid(false)
                        .withAdditionalneeds("Dinner")
                        .withBookingDates(requestBookingDates.withCheckin("2020-02-02").withCheckout("2020-03-03")))
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Garry"))
                .body("lastname", equalTo("Potter"))
                .body("totalprice", equalTo(Integer.valueOf("123")))
                .body("depositpaid", equalTo(Boolean.valueOf("false")))
                .body("bookingdates.checkin", is(CoreMatchers.equalTo("2020-02-02")))
                .body("bookingdates.checkout", is(CoreMatchers.equalTo("2020-03-03")))
                .body("additionalneeds", equalTo("Dinner"));
    }

    @Test
    void updateBookingAuthorisationPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .body(requestBooking.withFirstname("Garry")
                        .withLastname("Potter")
                        .withTotalprice(123)
                        .withDepositpaid(false)
                        .withAdditionalneeds("Dinner")
                        .withBookingDates(requestBookingDates.withCheckout("2020-03-03")))
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Garry"))
                .body("lastname", equalTo("Potter"))
                .body("totalprice", equalTo(Integer.valueOf("123")))
                .body("depositpaid", equalTo(Boolean.valueOf("false")))
                .body("bookingdates.checkout", is(CoreMatchers.equalTo("2020-03-03")))
                .body("additionalneeds", equalTo("Dinner"));
    }

    @Test
    void updateBookingFirstnameLastnamePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(requestBooking.withFirstname("Garry").withLastname("Potter"))
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Garry"))
                .body("lastname", equalTo("Potter"));
    }

    @Test
    void updateBookingFirstnamePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(requestBooking.withFirstname("James"))
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("James"));
    }

    @Test
    void updateBookingLastnamePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(requestBooking.withLastname("Green"))
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("Green"));
    }

    @Test
        // Баг
    void updateBookingCheckoutBeforeCheckinNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(requestBooking.withBookingDates(requestBookingDates.withCheckout("2020-02-02").withCheckin("2020-03-03")))
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200); //должен быть 400
    }


    @Test
    void updateBookingWithoutAuthorisationAndCookieNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestBooking.withFirstname("Garry")
                        .withLastname("Potter")
                        .withTotalprice(123)
                        .withDepositpaid(false)
                        .withAdditionalneeds("Dinner")
                        .withBookingDates(requestBookingDates.withCheckin("2020-02-02").withCheckout("2020-03-03")))
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }

    @Test
        // Баг
    void updateBookingCheckoutBeforeCheckinWithoutCheckoutFieldNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(requestBooking.withBookingDates(requestBookingDates.withCheckin("2020-03-03")))
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200); // должно быть 400
    }

    @Test
        // Баг
    void updateBookingCheckoutBeforeCheckinWithoutCheckinFieldNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .body(requestBooking.withBookingDates(requestBookingDates.withCheckout("2017-03-03")))
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200); // должно быть 400
    }
}
