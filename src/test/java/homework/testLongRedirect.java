package homework;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class testLongRedirect {

    @Test

    public void longRedirect() {

        /* Сначала выполняем запрос на апи эндпоинт */
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println("\nLocation: " + locationHeader);

        int redirectCount = 0;
        int statusCode = response.getStatusCode();

        /* Затем выполняем запросы по переходу на URL в цикле, пока не получим код 200.
        *  Также в конце цикла выводим URL для редиректа, который вернулся в ответе (если он есть) */
        while (statusCode != 200) {

              Response response2 = RestAssured
                      .given()
                      .redirects()
                      .follow(false)
                      .when()
                      .get(locationHeader)
                      .andReturn();

              locationHeader = response2.getHeader("Location");
              if (locationHeader != null) {
                  System.out.println("\nLocation: " + locationHeader);
              }

              statusCode = response2.getStatusCode();
              redirectCount++;

          }

        System.out.println("\nRedirects Count: " + redirectCount); // количество редиректов
    }
}