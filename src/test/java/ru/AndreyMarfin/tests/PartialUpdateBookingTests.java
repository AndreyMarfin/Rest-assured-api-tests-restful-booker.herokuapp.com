package ru.AndreyMarfin.tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.*;
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
        assertThat(responseToken.getToken().length(), IsEqual.equalTo(15));

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
    @DisplayName("(P)Update a booking, authorization - cookie")
    @Description("Positive test for update a booking, authorization - cookie")
    @Step("Update a booking, authorization - cookie")
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
    @DisplayName("(P)Update a booking, authorization - hard token")
    @Description("Positive test for update a booking, authorization - hard token")
    @Step("Update a booking, authorization - hard token")
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
    @DisplayName("(P)Update a firstname and lastname")
    @Description("Positive test for partial update a firstname and lastname")
    @Step("Update a firstname and lastname")
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
    @DisplayName("(P)Update a firstname")
    @Description("Positive test for partial update a firstname")
    @Step("Update a firstname")
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
    @DisplayName("(P)Update a lastname")
    @Description("Positive test for partial update a lastname")
    @Step("Update a lastname")
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
    @DisplayName("(N)Update a checkin and checkout, checkout before checkin")
    @Description("Negative test for partial update a checkin and checkout, checkout before checkin")
    @Step("Update checkout before checkin")
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
                .statusCode(200);
    }


    @Test
    @DisplayName("(N)Update without authorization")
    @Description("Negative test for partial update without authorization")
    @Step("Update without authorization")
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
    @DisplayName("(N)Update checkout before checkin without checkout field")
    @Description("Negative test for partial update a checkin and checkout, checkout before checkin without checkout field")
    @Step("Update checkout before checkin without checkout field")
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
                .statusCode(200);
    }

    @Test
    @DisplayName("(N)Update checkout before checkin without checkin field")
    @Description("Negative test for partial update a checkin and checkout, checkout before checkin without checkin field")
    @Step("Update, checkout before checkin without checkin field")
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
                .statusCode(200);
    }
}
