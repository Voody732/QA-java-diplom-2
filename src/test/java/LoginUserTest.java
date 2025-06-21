import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.steps.CreatingUserSteps;
import ru.yandex.praktikum.steps.LoginUserSteps;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.core.IsEqual.equalTo;

@Feature("Группа тестов для API авторизации пользователя")
@DisplayName("Авторизация пользователя")
public class LoginUserTest {
    private final CreatingUserSteps userSteps = new CreatingUserSteps();
    private final LoginUserSteps userLogin = new LoginUserSteps();
    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    @Step("Создание пользователя")
    public void setUp() {
        email = (RandomStringUtils.randomAlphanumeric(2, 10)
                + "@" + RandomStringUtils.randomAlphabetic(2, 8)
                + "." + "ru").toLowerCase();
        password = RandomStringUtils.randomAlphanumeric(10);
        name = RandomStringUtils.randomAlphabetic(5, 10);
        ValidatableResponse response = userSteps.createUser(email, password, name);
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Авторизация пользователя с валидными параметрами")
    @Description("ОР - success:true")
    public void shouldReturn200OkWithCorrectLoginOrPassword() {
        ValidatableResponse response = userLogin.loginUser(email, password);
        response
                .statusCode(SC_OK)
                .assertThat()
                .body("success", equalTo(true))
                .body("accessToken", not(emptyOrNullString()))
                .body("refreshToken", not(emptyOrNullString()))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
        accessToken = response.extract().path("accessToken");
    }

    @After
    @Step("Удаление пользователя")
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userSteps.deleteUser(accessToken);
        }
    }
}
