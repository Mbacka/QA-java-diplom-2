package praktikum.api.burger;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import praktickum.api.burger.IngredientsClient;
import praktickum.api.burger.OrderClient;
import praktickum.api.burger.UserClient;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class GetOrderTest {
    OrderClient orderClient;
    UserClient userClient;
    IngredientsClient ingredientsClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        ingredientsClient = new IngredientsClient();
    }

    @Test
    @DisplayName("Получить список заказов без авторизации")
    public void getOrderWithAuthorizationTest() {
        String accessToken = createAndLogin();
        Response responseIngredients = ingredientsClient.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        orderClient.createOrder(ingredients, accessToken);
        Response response = orderClient.getOrders(accessToken);
        assertEquals("Некорректный статус код!", 200, response.statusCode());
        assertEquals("Безуспешный запрос!", true, response.path("success"));
        assertThat("Orders is null", response.path("orders"), notNullValue());
    }

    @Test
    @DisplayName("Получить список заказов с авторизацией")
    public void getOrderWithoutAuthorizationTest() {
        String accessToken = createAndLogin();
        Response responseIngredients = ingredientsClient.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        orderClient.createOrder(ingredients, accessToken);
        Response response = orderClient.getOrders("");
        assertEquals("Некорректный статус код!", 401, response.statusCode());
        assertEquals("Запрос успешен!", false, response.path("success"));
        assertEquals("Некорректное сообщение!", "You should be authorised", response.path("message"));
    }

    private String createAndLogin(){
        String email = RandomStringUtils.randomAlphabetic(5) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        Response response = userClient.create(email, password, username);
        userClient.login(email, password);
        String accessToken = response.path("accessToken");
        return accessToken;
    }
}
