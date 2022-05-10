package ru.AndreyMarfin.tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.AndreyMarfin.dao.CreateBookingRequest;
import ru.AndreyMarfin.dao.CreateBookingdatesRequest;
import ru.AndreyMarfin.dao.CreateTokenRequest;
import ru.AndreyMarfin.dao.CreateTokenResponse;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Severity(SeverityLevel.NORMAL)

@DisplayName("Partial update")
@Feature("Partial update a booking")
public class PartialUpdateBookingTests extends BaseTest {

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
        assertThat(responseToken.getToken().length(), IsEqual.equalTo(15));
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

    @AfterEach
    void tearDown() {
        log.info("Delete test booking");
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
    @DisplayName("(P)Update a booking, authorization - cookie")
    @Description("Positive test for update a booking, authorization - cookie")
    @Step("Update a booking, authorization - cookie")
    void updateBookingCookiePositiveTest() {
        log.info("Start test: Update a booking, authorization - cookie");
        newRequest = requestBooking.withFirstname("Garry")
                .withLastname("Potter")
                .withTotalprice(123)
                .withDepositpaid(false)
                .withAdditionalneeds("Dinner")
                .withBookingDates(requestBookingDates.withCheckin("2020-02-02").withCheckout("2020-03-03"));
        log.info("Update booking: " + newRequest.toString());
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(newRequest)
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
        log.info("End test");
    }

    @Test
    @DisplayName("(P)Update a booking, authorization - hard token")
    @Description("Positive test for update a booking, authorization - hard token")
    @Step("Update a booking, authorization - hard token")
    void updateBookingAuthorisationPositiveTest() {
        log.info("Start test: Update a booking, authorization - hard token");
        newRequest = requestBooking.withFirstname("Garry")
                .withLastname("Potter")
                .withTotalprice(123)
                .withDepositpaid(false)
                .withAdditionalneeds("Dinner")
                .withBookingDates(requestBookingDates.withCheckout("2020-03-03"));
        log.info("Update booking: " + newRequest.toString());
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .body(newRequest)
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
        log.info("End test");
    }

    @Test
    @DisplayName("(P)Update a firstname and lastname")
    @Description("Positive test for partial update a firstname and lastname")
    @Step("Update a firstname and lastname")
    void updateBookingFirstnameLastnamePositiveTest() {
        log.info("Start test: Update a firstname and lastname");
        newRequest = requestBooking.withFirstname("Garry").withLastname("Potter");
        log.info("Update booking: " + newRequest.toString());
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(newRequest)
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Garry"))
                .body("lastname", equalTo("Potter"));
        log.info("End test");
    }

    @Test
    @DisplayName("(P)Update a firstname")
    @Description("Positive test for partial update a firstname")
    @Step("Update a firstname")
    void updateBookingFirstnamePositiveTest() {
        log.info("Start test: Update a firstname");
        newRequest = requestBooking.withFirstname("James");
        log.info("Update booking: " + newRequest.toString());
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(newRequest)
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("James"));
        log.info("End test");
    }

    @Test
    @DisplayName("(P)Update a lastname")
    @Description("Positive test for partial update a lastname")
    @Step("Update a lastname")
    void updateBookingLastnamePositiveTest() {
        log.info("Start test: Update a lastname");
        newRequest = requestBooking.withLastname("Green");
        log.info("Update booking: " + newRequest.toString());
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(newRequest)
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("Green"));
        log.info("End test");
    }

    @Test
    @DisplayName("(N)Update a checkin and checkout, checkout before checkin")
    @Description("Negative test for partial update a checkin and checkout, checkout before checkin")
    @Step("Update checkout before checkin")
    void updateBookingCheckoutBeforeCheckinNegativeTest() {
        log.info("Start test: Update checkout before checkin");
        newRequest = requestBooking.withBookingDates(requestBookingDates.withCheckout("2020-02-02").withCheckin("2020-03-03"));
        log.info("Update booking: " + newRequest.toString());
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(newRequest)
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);
        log.info("End test");
    }


    @Test
    @DisplayName("(N)Update without authorization")
    @Description("Negative test for partial update without authorization")
    @Step("Update without authorization")
    void updateBookingWithoutAuthorisationAndCookieNegativeTest() {
        log.info("Start test: Update without authorization");
        newRequest = requestBooking.withFirstname("Garry")
                .withLastname("Potter")
                .withTotalprice(123)
                .withDepositpaid(false)
                .withAdditionalneeds("Dinner")
                .withBookingDates(requestBookingDates.withCheckin("2020-02-02").withCheckout("2020-03-03"));
        log.info("Update booking: " + newRequest.toString());
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(newRequest)
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
        log.info("End test");
    }

    @Test
    @DisplayName("(N)Update checkout before checkin without checkout field")
    @Description("Negative test for partial update a checkin and checkout, checkout before checkin without checkout field")
    @Step("Update checkout before checkin without checkout field")
    void updateBookingCheckoutBeforeCheckinWithoutCheckoutFieldNegativeTest() {
        log.info("Start test: Update checkout before checkin without checkout field");
        newRequest = requestBooking.withBookingDates(requestBookingDates.withCheckin("2020-03-03"));
        log.info("Update booking: " + newRequest.toString());
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + responseToken.getToken())
                .body(newRequest)
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);
        log.info("End test");
    }

    @Test
    @DisplayName("(N)Update checkout before checkin without checkin field")
    @Description("Negative test for partial update a checkin and checkout, checkout before checkin without checkin field")
    @Step("Update, checkout before checkin without checkin field")
    void updateBookingCheckoutBeforeCheckinWithoutCheckinFieldNegativeTest() {
        log.info("Start test: Update, checkout before checkin without checkin field");
        newRequest = requestBooking.withBookingDates(requestBookingDates.withCheckout("2017-03-03"));
        log.info("Update booking: " + newRequest.toString());
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .body(newRequest)
                .when()
                .patch("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);
        log.info("End test");
    }
}
