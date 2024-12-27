package homework;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testGetHeader {

    @Test
    public void getHeader(){

        Response responseGetHeaders = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers headers = responseGetHeaders.getHeaders();

        assertEquals(200, responseGetHeaders.statusCode(), "Unexpected status code");
        assertTrue(headers.hasHeaderWithName("x-secret-homework-header"),
                "Response doesn't have 'x-secret-homework-header' header");
        assertEquals("Some secret value", responseGetHeaders.getHeader("x-secret-homework-header"),
                "Unexpected 'x-secret-homework-header' header value");
    }
}
