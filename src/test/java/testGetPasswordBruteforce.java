import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class testGetPasswordBruteforce {

    @Test
    public void getPasswordBruteforce() {

        String login = "super_admin";

        String[] passwords = {"1234", "12345", "123456", "1234567", "12345678", "123456789", "1234567890", "111111",
                 "welcome", "123123", "555555", "654321", "666666", "696969", "7777777", "888888", "000000",
                 "123qwe", "abc123", "aa123456", "1q2w3e4r", "1qaz2wsx", "qwerty123", "!@#$%^&*", "access", "admin",
                 "adobe123",  "ashley",  "azerty",  "bailey",  "baseball",  "batman",  "charlie",  "donald",
                 "dragon",  "flower",  "football",  "Football",  "freedom",  "hello", "hottie", "jesus", "iloveyou",
                 "letmein", "login", "lovely", "loveme", "master", "michael", "monkey", "mustang", "ninja",
                 "password", "password1", "passw0rd", "photoshop", "princess", "qazwsx", "qwerty", "qwertyuiop",
                 "shadow", "solo", "starwars", "sunshine", "superman", "trustno1", "121212", "whatever", "zaq1zaq1"
            };

        /* 1. Перебираем массив с паролями и получаем cookie авторизации через апи метод */
        for (String password : passwords) {

            Map<String, String> data = new HashMap<>();
            data.put("login", login);
            data.put("password", password);

            Response responseForGet = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie = responseForGet.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            if (responseCookie != null) {
                cookies.put("auth_cookie", responseCookie);
            }

            /* 2. Передаем полученный cookie во второй метод и если она валидная выводим сообщение об успехе и пароль,
            * иначе берем следующий пароль и повторяем цикл */
            Response responseForCheck = RestAssured
                    .given()
                    .body(data)
                    .cookies(cookies)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            String responseCheckCookie = responseForCheck.getBody().asString();

            if (responseCheckCookie.equals("You are authorized")) {
                System.out.println(responseCheckCookie);
                System.out.println("Your password: " + password);
                break;
            }

        }

    }
}