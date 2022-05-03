package ru.AndreyMarfin.tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.AndreyMarfin.dao.CreateTokenRequest;
import ru.AndreyMarfin.dao.CreateTokenResponse;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Severity(SeverityLevel.BLOCKER)

@DisplayName("Create token")
@Feature("Create an user token")
public class CreateTokenTests extends BaseTest {

    @BeforeAll
    static void beforeSuite() {
        RestAssured.baseURI = properties.getProperty("base.url");
        requestToken = CreateTokenRequest.builder()
                .username(properties.getProperty("username"))
                .password(properties.getProperty("password"))
                .build();
    }

    @Test
    @DisplayName("(P)Creation token")
    @Description("Positive test for creation token")
    @Step("Creation token")
     void createTokenPositiveTest() {
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
        assertThat(responseToken, is(notNullValue()));
        assertThat(responseToken.getToken().length(), equalTo(15));
    }

    @Test
    @DisplayName("(N)Creation token with wrong password")
    @Description("Negative test for creation token with wrong password")
    @Step("Creation token with wrong password")
    void createTokenWithAWrongPasswordNegativeTest() {
        responseToken = given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(requestToken.withPassword("password"))
                .expect()
                .statusCode(200)
                .when()
                .post("auth")
                .prettyPeek()
                .then()
                .extract()
                .as(CreateTokenResponse.class);
        assertThat(responseToken.getReason(), is(notNullValue()));
        assertThat(responseToken.getReason(), equalTo("Bad credentials"));
    }

    @Test
    @DisplayName("(N)Creation token with wrong username")
    @Description("Negative test for creation token with wrong username")
    @Step("Creation token with wrong username")
    void createTokenWithAWrongUsernameNegativeTest() {
        responseToken = given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(requestToken.withUsername("admin123"))
                .expect()
                .statusCode(200)
                .when()
                .post("auth")
                .prettyPeek()
                .then()
                .extract()
                .as(CreateTokenResponse.class);
        assertThat(responseToken, is(notNullValue()));
        assertThat(responseToken.getReason(), equalTo("Bad credentials"));
    }
}
