import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.dto.CreateOrderRequest;
import ru.yandex.praktikum.steps.CreatingOrderSteps;
import ru.yandex.praktikum.steps.CreatingUserSteps;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

@Feature("Группа тестов для API создания заказа")
@DisplayName("Создание заказа")
public class CreatingOrderTest {
    CreatingOrderSteps orderSteps = new CreatingOrderSteps();
    String accessToken;
    CreatingUserSteps userSteps = new CreatingUserSteps();
    ValidatableResponse response;
    private String email;
    private String password;
    private String name;

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareData() {
        email = (RandomStringUtils.randomAlphanumeric(2, 10) + "@" + RandomStringUtils.randomAlphabetic(2, 8) + "." + "ru").toLowerCase();
        password = RandomStringUtils.randomAlphanumeric(10);
        name = RandomStringUtils.randomAlphabetic(5, 10);
        response = userSteps.createUser(email, password, name);
    }

    @Test
    @DisplayName("Создание заказа с валидными значениями ингредиентов и авторизацией")
    @Description("ОР - 200, валидное тело заказа ")
    public void shouldReturn200WithAuth() {
        accessToken = response.extract().path("accessToken");
        ValidatableResponse responseGetIngredients = orderSteps.getIngredients();
        List<String> ingredients = responseGetIngredients.extract().path("data._id");
        List<String> orderIngredients = new ArrayList<>();
        orderIngredients.add(ingredients.get(0));
        orderIngredients.add(ingredients.get(1));
        orderIngredients.add(ingredients.get(4));
        ValidatableResponse orderResponse = orderSteps.createOrder(new CreateOrderRequest(orderIngredients), accessToken);
        orderResponse.statusCode(SC_OK)
                .assertThat()
                .body("success", equalTo(true))
                .body("name", not(emptyOrNullString()))
                .body("order.number", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Создание заказа с валидными значениями ингредиентов и без авторизации")
    @Description("ОР - 401 Unauthorized")
    public void shouldReturn401WithoutAuth() {
        ValidatableResponse responseGetIngredients = orderSteps.getIngredients();
        List<String> ingredients = responseGetIngredients.extract().path("data._id");
        List<String> orderIngredients = new ArrayList<>();
        orderIngredients.add(ingredients.get(0));
        orderIngredients.add(ingredients.get(1));
        orderIngredients.add(ingredients.get(4));
        ValidatableResponse orderResponse =
                orderSteps.createOrder(new CreateOrderRequest(orderIngredients));
        orderResponse.statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов и с  авторизации")
    @Description("ОР - 400 Bad request")
    public void shouldReturn400WithoutIngredientsAndWithAuth() {
        accessToken = response.extract().path("accessToken");
        List<String> orderIngredients = new ArrayList<>();
        ValidatableResponse orderResponse =
                orderSteps.createOrder(new CreateOrderRequest(orderIngredients), accessToken);
        orderResponse.statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа c невалидными ингредиентами и с  авторизации")
    @Description("ОР - 500 Iternal server error")
    public void shouldReturn500WithInvalidHAshIngredientsAndWithAuth() {
        accessToken = response.extract().path("accessToken");
        List<String> orderIngredients = new ArrayList<>();
        orderIngredients.add("Kon`");
        ValidatableResponse orderResponse =
                orderSteps.createOrder(new CreateOrderRequest(orderIngredients), accessToken);
        orderResponse.statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    @Step("Удаление пользователя")
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userSteps.deleteUser(accessToken);
        }
    }
}
