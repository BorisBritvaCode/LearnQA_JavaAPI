package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import  io.qameta.allure.Description;
import  io.qameta.allure.Epic;
import  io.qameta.allure.Feature;
import  org.junit.jupiter.api.DisplayName;


@Epic("Change user details cases")
@Feature("User edit")
public class UserEditTest extends BaseTestCase {

    String cookie;
    String header;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test registers a user, then authorizes this user, changes his name and checks the name is changed")
    @DisplayName("Test edit just created user positive")
    public void testEditJustCreatedUser() {
        // создаем нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.jsonPath().getString("id");

        // авторизуемся за созданного пользователя
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        // меняем ему имя
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.header,
                this.cookie,
                editData);

        // проверяем, что имя изменилось
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.header,
                        this.cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Description("This test registers a user, then authorizes as another user, tries to change username fot just created user and checks the username is still the same")
    @DisplayName("Test edit user with no auth negative")
    public void testEditUserNotAuth() {
        // создаем нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.jsonPath().getString("id");
        String defaultUsername = userData.get("username");

        // запрос на редактирование без авторизации возвращает ошибку
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithNoAuth(
                "https://playground.learnqa.ru/api/user/" + userId,
                editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser, "error");
        Assertions.assertJsonByName(responseEditUser, "error", "Auth token not supplied");

        // username созданного пользователя не изменился
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithNoAuth("https://playground.learnqa.ru/api/user/" + userId);

       Assertions.assertJsonByName(responseUserData, "username", defaultUsername);
    }

    @Test
    @Description("This test registers a user, tries to change username with no auth and checks the name is still the same")
    @DisplayName("Test authorized user edits another user negative")
    public void testEditUserAuthAsAnotherUser() {
        // создаем нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.jsonPath().getString("id");
        String defaultUsername = userData.get("username");

        // авторизуемся за другого пользователя
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "borisBritva11@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        // запрос на редактирование c авторизацией под другим пользователем возвращает ошибку
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.header,
                this.cookie,
                editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser, "error");
        Assertions.assertJsonByName(responseEditUser, "error", "This user can only edit their own data.");

        // username созданного пользователя не изменился
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithNoAuth("https://playground.learnqa.ru/api/user/" + userId);

        Assertions.assertJsonByName(responseUserData, "username", defaultUsername);
    }

    @Test
    @Description("This test registers a user, then authorizes this user, tries to change email on invalid one and checks the email is still the same")
    @DisplayName("Test edit user with invalid email negative")
    public void testEditUserWithInvalidEmail() {
        // создаем нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.jsonPath().getString("id");
        String defaultEmail = userData.get("email");

        // авторизуемся за созданного пользователя
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        // запрос на редактирование c некорректным email возвращает ошибку
        String invalidEmail = "borisbritva1example.com"; // отсутствует символ @
        Map<String, String> editData = new HashMap<>();
        editData.put("email", invalidEmail);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.header,
                this.cookie,
                editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser, "error");
        Assertions.assertJsonByName(responseEditUser, "error", "Invalid email format");

        // email созданного пользователя не изменился
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.header,
                        this.cookie);

        Assertions.assertJsonByName(responseUserData, "email", defaultEmail);
    }

    @Test
    @Description("This test registers a user, then authorizes this user, tries to change name on too short one and checks the name is still the same")
    @DisplayName("Test edit user with too short name negative")
    public void testEditUserWithTooShortName() {
        // создаем нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.jsonPath().getString("id");
        String defaultFirstName = userData.get("firstName");

        // авторизуемся за созданного пользователя
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        // запрос на редактирование c именем в один символ возвращает ошибку
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "B");

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.header,
                this.cookie,
                editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonHasField(responseEditUser, "error");
        Assertions.assertJsonByName(responseEditUser, "error", "The value for field `firstName` is too short");

        // имя созданного пользователя не изменилось
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.header,
                        this.cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", defaultFirstName);
    }
}
