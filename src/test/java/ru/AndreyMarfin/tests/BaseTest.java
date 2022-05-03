package ru.AndreyMarfin.tests;

import com.github.javafaker.Faker;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import ru.AndreyMarfin.dao.CreateBookingRequest;
import ru.AndreyMarfin.dao.CreateBookingdatesRequest;
import ru.AndreyMarfin.dao.CreateTokenRequest;
import ru.AndreyMarfin.dao.CreateTokenResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class BaseTest {
    protected static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    protected static CreateTokenRequest requestToken;
    protected static CreateTokenResponse responseToken;
    protected static CreateBookingdatesRequest requestBookingDates;
    protected static CreateBookingRequest requestBooking;

    protected static Properties properties = new Properties();
    protected static Faker faker = new Faker();
    protected String id;

    @Story("Create a booking")
    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
    }
}
