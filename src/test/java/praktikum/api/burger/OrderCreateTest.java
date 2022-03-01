package praktikum.api.burger;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktickum.api.burger.IngredientsClient;
import praktickum.api.burger.OrderClient;
import praktickum.api.burger.UserClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrderCreateTest {
    OrderClient orderClient;
    UserClient userClient;
    IngredientsClient ingredientsClient;
    private String accessToken;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        ingredientsClient = new IngredientsClient();
    }

    @After
    public void tearDown() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        accessToken = login();
        Response response = orderClient.createOrder(null, accessToken);
        assertEquals("Некорректный статус код!", 400, response.statusCode());
        assertEquals("Запрос успешен!", false, response.path("success"));
        assertEquals("Некорректное сообщение!", "Ingredient ids must be provided", response.path("message"));
    }

    @Test
    @DisplayName("Создание заказа с неправильным ингредиентом")
    public void createOrderWithIncorrectIngredientTest() {
        accessToken = login();
        Response response = orderClient.createOrder(Arrays.asList("1213"), accessToken);
        assertEquals("Некорректный статус код!", 500, response.statusCode());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthorizationTest() {
        accessToken = login();
        Response responseIngredients = ingredientsClient.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        Response response = orderClient.createOrder(ingredients, accessToken);
        assertEquals("Некорректный статус код!", 200, response.statusCode());
        assertEquals("Безуспешный запрос!", true, response.path("success"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthorizationTest() {
        accessToken = login();
        Response responseIngredients = ingredientsClient.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        Response response = orderClient.createOrder(ingredients, "");
        assertEquals("Некорректный статус код!", 200, response.statusCode());
        assertEquals("Безуспешный запрос!", true, response.path("success"));
    }

    private String login() {
        String email = RandomStringUtils.randomAlphabetic(5) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        Response createResponse = userClient.create(email, password, username);
        String accessToken = createResponse.path("accessToken");
        userClient.login(email, password);
        return accessToken;
    }
}
