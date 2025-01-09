package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import  org.junit.jupiter.api.DisplayName;

@Feature("Get user details")
@DisplayName("Get user tests")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test checks that not authorized user can get only a username from user details")
    @DisplayName("Test get not authorized user details positive")
    @Story("Positive tests")
    @Tags({@Tag("api"),@Tag("smoke"),@Tag("user")})
    @Owner("Ivan Pechenkin")
    public void testGetUserDataNotAuth() {
        Response responseUserData = apiCoreRequests.makeGetRequestWithNoAuth(
                ENVIRONMENT + "user/2");

        String[] unexpectedFields = {"firstName", "lastName", "email"};

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
    }

    @Test
    @Description("This test checks that authorized user can successfully get full details about himself")
    @DisplayName("Test get authorized user details positive")
    @Story("Positive tests")
    @Tags({@Tag("api"),@Tag("smoke"),@Tag("user")})
    @Owner("Ivan Pechenkin")
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // логинимся за пользователя с ID=2
        Response responseGetAuth = apiCoreRequests
                .makePostRequest(ENVIRONMENT + "user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // запрашиваем данные пользователя с ID=2
        Response responseUserData = apiCoreRequests
                .makeGetRequest(ENVIRONMENT + "user/2",
                        header,
                        cookie);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);

    }

    @Test
    @Description("This test checks that authorized user can get only a username from another user details")
    @DisplayName("Test authorized user get another user details negative")
    @Story("Negative tests")
    @Tags({@Tag("api"),@Tag("user")})
    @Owner("Ivan Pechenkin")
    public void testGetUserDetailsAuthAsAnotherUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // логинимся за пользователя с ID=2
        Response responseGetAuth = apiCoreRequests
                .makePostRequest(ENVIRONMENT + "user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        // запрашиваем данные пользователя с ID=1
        Response responseUserData = apiCoreRequests
                .makeGetRequest(ENVIRONMENT + "user/1",
                        header,
                        cookie);

        String[] unexpectedFields = {"firstName", "lastName", "email"};

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
    }
}
