package ru.AndreyMarfin.tests;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

public class PartialUpdateBookingTests {

    static String token;
    static String id;

    @BeforeAll
    static void beforeAll() {
        token = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"username\" : \"admin\",\n" +
                        "    \"password\" : \"password123\"\n" +
                        "}")
                .expect()
                .statusCode(200)
                .body("token", is(CoreMatchers.notNullValue()))
                .when()
                .post("https://restful-booker.herokuapp.com/auth")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("token")
                .toString();
    }

    @BeforeEach
    void setUp() {
        id = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "    \"firstname\" : \"Jim\",\n" +
                        "    \"lastname\" : \"Brown\",\n" +
                        "    \"totalprice\" : 111,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2018-01-01\",\n" +
                        "        \"checkout\" : \"2019-01-01\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast\"\n" +
                        "}")
                .expect()
                .statusCode(200)
                .when()
                .post("https://restful-booker.herokuapp.com/booking")
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
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("https://restful-booker.herokuapp.com/booking/" + id)
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
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"firstname\" : \"Garry\",\n" +
                        "    \"lastname\" : \"Potter\",\n" +
                        "    \"totalprice\" : 123,\n" +
                        "    \"depositpaid\" : false,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2020-02-02\",\n" +
                        "        \"checkout\" : \"2020-03-03\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Dinner\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
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
                .body("{\n" +
                        "    \"firstname\" : \"Garry\",\n" +
                        "    \"lastname\" : \"Potter\",\n" +
                        "    \"totalprice\" : 123,\n" +
                        "    \"depositpaid\" : false,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkout\" : \"2020-03-03\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Dinner\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
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
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"firstname\" : \"Garry\",\n" +
                        "    \"lastname\" : \"Potter\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Garry"))
                .body("lastname", equalTo("Potter"))
                .body("totalprice", equalTo(Integer.valueOf("111")))
                .body("depositpaid", equalTo(Boolean.valueOf("true")))
                .body("bookingdates.checkin", is(CoreMatchers.equalTo("2018-01-01")))
                .body("bookingdates.checkout", is(CoreMatchers.equalTo("2019-01-01")))
                .body("additionalneeds", equalTo("Breakfast"));
    }

    @Test
    void updateBookingFirstnamePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"firstname\" : \"James\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
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
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"lastname\" : \"Green\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("Green"));
    }

    @Test
    void updateBookingCheckoutBeforeCheckinNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2020-03-03\",\n" +
                        "        \"checkout\" : \"2020-02-02\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(400);
    }


    @Test
    void updateBookingWithoutAuthorisationAndCookieNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body("{\n" +
                        "    \"firstname\" : \"Garry\",\n" +
                        "    \"lastname\" : \"Potter\",\n" +
                        "    \"totalprice\" : 123,\n" +
                        "    \"depositpaid\" : false,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2020-02-02\",\n" +
                        "        \"checkout\" : \"2020-03-03\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Dinner\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }

    @Test
    void updateBookingCheckoutBeforeCheckinWithoutCheckoutFieldNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2020-03-03\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(400);
    }

    @Test
    void updateBookingCheckoutBeforeCheckinWithoutCheckinFieldNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .body("{\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkout\" : \"2017-03-03\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(400);
    }
}
