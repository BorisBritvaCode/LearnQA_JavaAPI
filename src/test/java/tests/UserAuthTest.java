package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import lib.BaseTestCase;
import lib.Assertions;
import lib.ApiCoreRequests;

import java.util.HashMap;
import java.util.Map;

@Feature("Authorization")
@DisplayName("Authorization tests")
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(ENVIRONMENT + "user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    @Description("This test successfully authorize user by email and password")
    @DisplayName("Test auth user positive")
    @Story("Positive tests")
    @Tags({@Tag("api"),@Tag("smoke"),@Tag("user")})
    @Owner("Ivan Pechenkin")
    public void testAuthUser() {

        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(ENVIRONMENT + "user/auth",
                        this.header,
                        this.cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @ParameterizedTest (name = "Test auth user negative. Auth with invalid {0}")
    @ValueSource(strings = {"cookie", "header"})
    @Description("This test checks authorization status by sending invalid auth cookie or token ")
    @DisplayName("Test auth user with invalid auth data")
    @Story("Negative tests")
    @Tags({@Tag("api"),@Tag("smoke"),@Tag("user")})
    @Owner("Ivan Pechenkin")
    public void testAuthUserWithInvalidAuthData(String condition){

        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests
                    .makeGetRequest(ENVIRONMENT + "user/auth",
                            this.header,
                            "123");
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);

        } else if (condition.equals("header")) {
            Response responseForCheck = apiCoreRequests
                    .makeGetRequest(ENVIRONMENT + "user/auth",
                            "123",
                            this.cookie);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition value is unknown " + condition);
        }
    }

    @ParameterizedTest (name = "Test auth user negative. Auth with {0} only")
    @ValueSource(strings = {"cookie", "header"})
    @Description("This test checks authorization status without sending auth cookie or token ")
    @DisplayName("Test auth user without auth data")
    @Story("Negative tests")
    @Tags({@Tag("api"),@Tag("smoke"),@Tag("user")})
    @Owner("Ivan Pechenkin")
    public void testAuthUserMissedAuthData(String condition){

        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
                    ENVIRONMENT + "user/auth",
                    this.cookie
            );
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("header")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
                    ENVIRONMENT + "user/auth",
                    this.header
            );
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition value is unknown " + condition);
        }
    }
}
