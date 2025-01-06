package tests;

import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.ApiCoreRequests;
import lib.DataGenerator;

import java.util.HashMap;
import java.util.Map;

import  io.qameta.allure.Description;
import  io.qameta.allure.Epic;
import  io.qameta.allure.Feature;
import  org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test tries to register a user with email that already exists")
    @DisplayName("Test create already existing user negative")
    public void testCreateUserWithExistingEmailNegative() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser, "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("This test successfully register a user with valid data")
    @DisplayName("Test create user positive")
    public void testCreateUserSuccessfully() {

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 200);
        Assertions.assertJsonHasField(responseCreateUser, "id");
    }

    @Test
    @Description("This test tries to register a user with invalid email")
    @DisplayName("Test create user with invalid email negative")
    public void testCreateUserWithInvalidEmailNegative() {
        String invalidEmail = "borisbritva1example.com"; // отсутствует символ @

        Map<String, String> userData = new HashMap<>();
        userData.put("email", invalidEmail);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser, "Invalid email format");
    }

    @Description("This test tries to register a user without one of the required fields")
    @DisplayName("Test create user without required fields negative")
    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName", "email", "password"})
    public void testCreateUserMissedRequiredFieldsNegative(String field) {
        Map<String, String> userData = new HashMap<>();
        userData.put(field, null);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser, "The following required params are missed: " + field);
    }

    @Description("This test tries to register a user with too short name")
    @DisplayName("Test create user with too short name")
    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName"})
    public void testCreateUserWithTooShortNameNegative(String field) {
        Map<String, String> userData = new HashMap<>();
        userData.put(field, "A");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser, "The value of '" + field + "' field is too short");
    }

    @Description("This test tries to register a user with too long name")
    @DisplayName("Test create user with too long name")
    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName"})
    public void testCreateUserWithTooLongNameNegative(String field) {
        Map<String, String> userData = new HashMap<>();
        userData.put(field, "XYoi06sBu4qUkNYTugISI7Xi1YiMx0NlwzCGtpuMDRlbZhsk8wbUoNwfC6ogJKuJY2uD3YclsnZrBFXaf7kCoWb6qGRwjO90rw6quvrqJPTId6lMmr9X6DvqmYI9T0muRrnjPl6RNYMdzAjKljwpXsGmJUSTFkiUpTH0ZIjT312afK9ypkbkQNoihUGaYMNTDI0XxwodGXwLXgl8sILmsnjlWdj3jXLYRz9fGPrY8dy5hiR1CpARN4D6kaT2TDJ");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser, "The value of '" + field + "' field is too long");
    }
}
