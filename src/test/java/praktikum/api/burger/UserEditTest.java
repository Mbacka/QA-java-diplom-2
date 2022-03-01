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

public class UserEditTest {
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
    @DisplayName("Изменяем пароль пользователя без авторизации")
    public void editUserPassWithoutAuthorizationTest() {
        Map<String, String> data = create();
        login(data.get("email"), data.get("password"));
        String newPassword = RandomStringUtils.randomAlphabetic(10);
        Response response = userClient.edit(data.get("email"), newPassword, data.get("name"), "");
        accessToken = data.get("accessToken");
        assertEquals("Некорректный статус код!", 401, response.statusCode());
        assertEquals("Запрос успешен!", false, response.path("success"));
        assertEquals("Некорректное сообщение!", "You should be authorised", response.path("message"));
    }

    @Test
    @DisplayName("Изменяем имя пользователя без авторизации")
    public void editUserUsernameWithoutAuthorizationTest() {
        Map<String, String> data = create();
        login(data.get("email"), data.get("password"));
        String newUsername = RandomStringUtils.randomAlphabetic(10);
        Response response = userClient.edit(data.get("email"), data.get("password"), newUsername, "");
        accessToken = data.get("accessToken");
        assertEquals("Некорректный статус код!", 401, response.statusCode());
        assertEquals("Запрос успешен!", false, response.path("success"));
        assertEquals("Некорректное сообщение!", "You should be authorised", response.path("message"));
    }

    @Test
    @DisplayName("Изменяем почту пользователя без авторизации")
    public void editUserEmailWithoutAuthorizationTest() {
        Map<String, String> data = create();
        login(data.get("email"), data.get("password"));
        String newEmail = RandomStringUtils.randomAlphabetic(5) + "@yandex.ru";
        Response response = userClient.edit(newEmail, data.get("password"), data.get("name"), "");
        accessToken = data.get("accessToken");
        assertEquals("Некорректный статус код!", 401, response.statusCode());
        assertEquals("Запрос успешен!", false, response.path("success"));
        assertEquals("Некорректное сообщение!", "You should be authorised", response.path("message"));
    }

    @Test
    @DisplayName("Изменяем пароль пользователя с авторизацией")
    public void editUserPassWithAuthorizationTest() {
        Map<String, String> data = create();
        login(data.get("email"), data.get("password"));
        String newPassword = RandomStringUtils.randomAlphabetic(10);
        Response response = userClient.edit(data.get("email"), newPassword, data.get("name"), data.get("accessToken"));
        accessToken = data.get("accessToken");
        assertEquals("Некорректный статус код!", 200, response.statusCode());
        assertEquals("Безуспешный запрос!", true, response.path("success"));
    }

    @Test
    @DisplayName("Изменяем имя пользователя с авторизацией")
    public void editUserUsernameWithAuthorizationTest() {
        Map<String, String> data = create();
        login(data.get("email"), data.get("password"));
        String newUsername = RandomStringUtils.randomAlphabetic(10);
        Response response = userClient.edit(data.get("email"), data.get("password"), newUsername, data.get("accessToken"));
        accessToken = data.get("accessToken");
        assertEquals("Некорректный статус код!", 200, response.statusCode());
        assertEquals("Безуспешный запрос!", true, response.path("success"));
    }

    @Test
    @DisplayName("Изменяем почту пользователя с авторизацией")
    public void editUserEmailWithAuthorizationTest() {
        Map<String, String> data = create();
        login(data.get("email"), data.get("password"));
        String newEmail = RandomStringUtils.randomAlphabetic(5) + "@yandex.ru";
        Response response = userClient.edit(newEmail, data.get("password"), data.get("name"), data.get("accessToken"));
        accessToken = data.get("accessToken");
        assertEquals("Некорректный статус код!", 200, response.statusCode());
        assertEquals("Безуспешный запрос!", true, response.path("success"));
    }

    @Test
    @DisplayName("Изменить почту с авторизацией на уже существующую")
    public void editUserAlreadyExistsEmailWithAuthorizationTest() {
        Map<String, String> data = create();
        String newEmail = RandomStringUtils.randomAlphabetic(5) + "@yandex.ru";
        userClient.create(newEmail, data.get("password"), data.get("name"));
        login(data.get("email"), data.get("password"));
        Response response = userClient.edit(newEmail, data.get("password"), data.get("name"), data.get("accessToken"));
        accessToken = data.get("accessToken");
        assertEquals("Некорректный статус код!", 403, response.statusCode());
        assertEquals("Безуспешный запрос!", false, response.path("success"));
        assertEquals("Некорректное сообщение!", "User with such email already exists", response.path("message"));
    }

    private Map<String, String> create() {
        String email = RandomStringUtils.randomAlphabetic(5) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        Response response = userClient.create(email, password, username);
        String accessToken = response.path("accessToken");
        Map<String, String> inputDataMap = new HashMap<>();
        inputDataMap.put("email", email);
        inputDataMap.put("password", password);
        inputDataMap.put("name", username);
        inputDataMap.put("accessToken", accessToken);
        return inputDataMap;
    }

    private void login(String email, String password) {
        userClient.login(email, password);
    }
}
