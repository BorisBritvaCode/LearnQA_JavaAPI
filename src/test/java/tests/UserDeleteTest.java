package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import  org.junit.jupiter.api.DisplayName;

@Feature("Delete user")
@DisplayName("Delete user tests")
public class UserDeleteTest extends BaseTestCase {

    String header;
    String cookie;
    String userId;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test checks that deletion of a test user is forbidden")
    @DisplayName("Test delete test user negative")
    @Story("Negative tests")
    @Tags({@Tag("api"),@Tag("user")})
    @Owner("Ivan Pechenkin")
    public void testDeleteTestUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // логинимся за пользователя с ID=2
        Response responseGetAuth = apiCoreRequests
                .makePostRequest(ENVIRONMENT + "user/login", authData);

        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");

        // пробуем удалить пользователя с ID=2
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(ENVIRONMENT + "user/2",
                        this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertJsonByName(responseDeleteUser, "error", "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        // проверяем, что пользователь с ID=2 не удалился
        Response responseUserData = apiCoreRequests
                .makeGetRequest(ENVIRONMENT + "user/2",
                        this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseUserData, 200);
        Assertions.assertJsonByName(responseUserData, "username", "Vitaliy");
    }

    @Test
    @Description("This test registers a user, then authorizes this user, deletes him and checks the user is deleted")
    @DisplayName("Test delete just created user positive")
    @Story("Positive tests")
    @Tags({@Tag("api"),@Tag("smoke"),@Tag("user")})
    @Owner("Ivan Pechenkin")
    public void testDeleteJustCreatedUser() {
        // создаем нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                ENVIRONMENT + "user/", userData);

        this.userId = getStringFromJson(responseCreateAuth, "id");

        // авторизуемся за созданного пользователя
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(ENVIRONMENT + "user/login", authData);

        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");

        // удаляем его
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(ENVIRONMENT + "user/" + userId,
                        this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);
        Assertions.assertJsonByName(responseDeleteUser, "success", "!");

        // проверяем, что пользователь удален
        Response responseUserData = apiCoreRequests
                .makeGetRequest(ENVIRONMENT + "user/" + userId,
                        this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    @Description("This test registers a user, then authorizes as another user, tries to delete just created user and checks the user is still here")
    @DisplayName("Test authorized user delete another user negative")
    @Story("Negative tests")
    @Tags({@Tag("api"),@Tag("user")})
    @Owner("Ivan Pechenkin")
    public void testDeleteUserAuthAsAnotherUser() {
        // создаем нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                ENVIRONMENT + "user/", userData);

        this.userId = getStringFromJson(responseCreateAuth, "id");
        String defaultUsername = userData.get("username");

        // авторизуемся за другого пользователя
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "borisBritva11@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(ENVIRONMENT + "user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        // запрос на удаление c авторизацией под другим пользователем возвращает ошибку
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(ENVIRONMENT + "user/" + userId,
                        this.header, this.cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertJsonByName(responseDeleteUser, "error", "This user can only delete their own account.");

        // проверяем, что пользователь не удалился
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithNoAuth(ENVIRONMENT + "user/" + userId);

        Assertions.assertResponseCodeEquals(responseUserData, 200);
        Assertions.assertJsonByName(responseUserData, "username", defaultUsername);
    }
}



