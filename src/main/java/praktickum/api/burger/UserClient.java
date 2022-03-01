package praktickum.api.burger;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;

public class UserClient extends RestAssuredClient {

    private static final String USER_PATH = "api/auth/";

    @Step("Создание пользователя")
    public Response create(String email, String password, String username) {
        JSONObject requestBodyJson = new JSONObject();
        String requestBody = requestBodyJson
                .put("email", email)
                .put("password", password)
                .put("name", username)
                .toString();
        Response response = given()
                .spec(getBaseSpec())
                .body(requestBody)
                .when()
                .post(USER_PATH + "register/");
        return response;
    }

    @Step("Авторизация пользователя")
    public Response login(String email, String password) {
        JSONObject requestBodyJson = new JSONObject();
        String requestBody = requestBodyJson
                .put("email", email)
                .put("password", password)
                .toString();
        Response response = given()
                .spec(getBaseSpec())
                .body(requestBody)
                .when()
                .post(USER_PATH + "login/");
        return response;
    }

    @Step("Изменение данных пользователя")
    public Response edit(String email, String password, String username, String token) {
        JSONObject requestBodyJson = new JSONObject();
        String requestBody = requestBodyJson
                .put("email", email)
                .put("password", password)
                .put("name", username)
                .toString();
        Response response = given()
                .header("Authorization", token)
                .spec(getBaseSpec())
                .body(requestBody)
                .when()
                .patch(USER_PATH + "user/");
        return response;
    }

    @Step("Удаление созданного пользователя")
    public void delete(String accessToken) {
        if (accessToken == null) {
            return;
        }
        given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(USER_PATH + "user/")
                .then()
                .statusCode(202)
                .log().status();
    }
}
