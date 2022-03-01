package praktikum.api.burger;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktickum.api.burger.UserClient;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UserCreateTest {
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Проверяем создание пользователя")
    public void createUserSuccessTest() {
        Map<String, String> data = cred();
        Response response = userClient.create(data.get("email"), data.get("password"), data.get("username"));
        accessToken = response.path("accessToken");
        assertEquals("Некорректный статус код!", 200, response.statusCode());
    }

    @Test
    @DisplayName("Проверяем создание пользователя без обязательного поля")
    public void createUserWithoutRequiredFieldTest() {
        Map<String, String> data = cred();
        Response response = userClient.create("", data.get("password"), data.get("username"));
        assertEquals("Некорректный статус код!", 403, response.statusCode());
        assertEquals("Некорректное сообщение!", "Email, password and name are required fields", response.path("message"));
    }

    @Test
    @DisplayName("Проверяем создание уже существующего пользователя")
    public void createExistsUserTest() {
        Map<String, String> data = cred();
        userClient.create(data.get("email"), data.get("password"), data.get("username"));
        Response response = userClient.login(data.get("email"), data.get("password"));
        accessToken = response.path("accessToken");
        response = userClient.create(data.get("email"), data.get("password"), data.get("username"));

        assertEquals("Некорректный статус код!", 403, response.statusCode());
        assertEquals("Некорректное сообщение!", "User already exists", response.path("message"));
    }

    private Map<String, String> cred() {
        String email = RandomStringUtils.randomAlphabetic(5) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        Map<String, String> inputDataMap = new HashMap<>();
        inputDataMap.put("email", email);
        inputDataMap.put("password", password);
        inputDataMap.put("username", username);
        return inputDataMap;
    }
}
