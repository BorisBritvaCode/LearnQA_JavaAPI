package homework;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testGetCookie {

    @Test
    public void getCookie(){

        Response responseGetCookie = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> cookies = responseGetCookie.getCookies();

        assertEquals(200, responseGetCookie.statusCode(), "Unexpected status code");
        assertTrue(cookies.containsKey("HomeWork"), "Response doesn't have 'HomeWork' cookie");
        assertEquals("hw_value1", cookies.get("HomeWork"), "Unexpected 'HomeWork' cookie value");
    }
}
