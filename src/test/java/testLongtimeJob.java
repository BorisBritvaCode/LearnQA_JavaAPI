import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.lang.Thread;

public class testLongtimeJob {

    @Test
    public void longtimeJob() throws InterruptedException {

        /* создаем задачу и если получили токен, считаем, что задача начала исполнение */
        JsonPath responseJobStart = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String jobToken = responseJobStart.get("token");
        Map<String, String> token = new HashMap<>();
        if (jobToken != null){
            token.put("token", jobToken);

            System.out.println("OK - Job has started");
        }

        int timeToComplete = responseJobStart.get("seconds");


        /* делаем один запрос с token ДО завершения задачи и убеждаемся, что возвращается правильный статус  */
        JsonPath responseJobNotReadyCheck = RestAssured
                .given()
                .queryParams(token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String jobStatusNotReady = responseJobNotReadyCheck.get("status");
        if (jobStatusNotReady.equals("Job is NOT ready")) {
            System.out.println("OK - Job is in progress...");
        }

        /* делаем паузу и ждем когда задача завершится  */
        Thread.sleep(timeToComplete * 1000L);


        /* делаем один запрос с token после того, как задача готова,
        * и убеждаемся, что возвращается правильный статус и поле result  */
        JsonPath responseJobReadyCheck = RestAssured
                .given()
                .queryParams(token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String jobStatusReady = responseJobReadyCheck.get("status");
        String result = responseJobReadyCheck.get("result");

        if (jobStatusReady.equals("Job is ready") && (result != null)) {
            System.out.println("OK - Job is ready");
            System.out.println("\nResult: " + result);
        }

    }
}
