import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.steps.CreatingUserSteps;
import ru.yandex.praktikum.steps.LoginUserSteps;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.IsEqual.equalTo;

@Feature("Группа тестов для API авторизации пользователя")
@DisplayName("Создание пользователя с невалидными данными")
@RunWith(Parameterized.class)
public class LoginUserParameterizedTest {
    private static String name;
    private static String validEmail;
    private static String validPassword;
    private final CreatingUserSteps userSteps = new CreatingUserSteps();
    private final String email;
    private final String password;
    private final LoginUserSteps userLogin = new LoginUserSteps();
    private String accessToken;

    public LoginUserParameterizedTest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Step("Создание массива данных")
    @Parameterized.Parameters
    public static Object[][] getData() {
        validEmail = (RandomStringUtils.randomAlphanumeric(2, 10) + "@" + RandomStringUtils.randomAlphabetic(2, 8) + "." + "ru").toLowerCase();
        validPassword = RandomStringUtils.randomAlphanumeric(10);
        return new Object[][]{
                {RandomStringUtils.randomAlphanumeric(1, 2).toLowerCase() + validEmail, validPassword},
                {null, validPassword},
                {validEmail, validPassword + RandomStringUtils.randomAlphanumeric(1, 2)},
                {validEmail, null},
        };
    }

    @Before
    @Step("Создание пользователя")
    public void setUp() {
        name = RandomStringUtils.randomAlphabetic(5, 10);
        if (email != null && password != null && email.equals(validEmail) && password.equals(validPassword)) {
            ValidatableResponse response = userSteps.createUser(validEmail, validPassword, name);
            accessToken = response.extract().path("accessToken");
        }
    }

    @Test
    @DisplayName("Авторизация с невалидными значениями пользователя")
    @Description("ОР - success:false")
    public void shouldReturn401UnauthorizedWithIncorrectLoginOrPassword() {
        ValidatableResponse response = userLogin.loginUser(email, password);
        response
                .statusCode(SC_UNAUTHORIZED)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
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
