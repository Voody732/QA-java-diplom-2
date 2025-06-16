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

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

@Feature("Группа тестов для API создания пользователя")
@DisplayName("Создание пользователя")
public class CreatingUserTest {
    private final CreatingUserSteps userSteps = new CreatingUserSteps();
    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareData() {
        email = (RandomStringUtils.randomAlphanumeric(2, 10) + "@" + RandomStringUtils.randomAlphabetic(2, 8) + "." + "ru").toLowerCase();
        password = RandomStringUtils.randomAlphanumeric(10);
        name = RandomStringUtils.randomAlphabetic(5, 10);
    }

    @Test
    @Step("Создание пользователя с валидными значениями email, password, name")
    @Description("ОР - success:true")
    public void ShouldReturnSuccessTrueAfterCreateUserTest() {
        ValidatableResponse response = userSteps.createUser(email, password, name);
        response.statusCode(SC_OK)
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", not(emptyOrNullString()))
                .body("refreshToken", not(emptyOrNullString()));
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @Step("Создание уже существующего пользователя")
    @Description("ОР - success:false")
    public void ShouldReturnSuccessFalseAfterCreateAlreadyExistsUserTest() {
        ValidatableResponse response = userSteps.createUser(email, password, name);
        response
                .statusCode(SC_OK)
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", not(emptyOrNullString()))
                .body("refreshToken", not(emptyOrNullString()));
        response
                .statusCode(SC_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @Step("Создание пользователя без email")
    @Description("ОР - success:false")
    public void ShouldReturnForbiddenSuccessFalseAfterCreateUserWithoutEmailTest() {
        ValidatableResponse response = userSteps.createUser(this.email = null, password, name);
        response
                .statusCode(SC_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @Step("Создание пользователя без password")
    @Description("ОР - success:false")
    public void ShouldReturnForbiddenSuccessFalseAfterCreateUserWithoutPasswordTest() {
        ValidatableResponse response = userSteps.createUser(email, this.password = null, name);
        response
                .statusCode(SC_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @Step("Создание пользователя без name")
    @Description("ОР - success:false")
    public void ShouldReturnForbiddenSuccessFalseAfterCreateUserWithoutNameTest() {
        ValidatableResponse response = userSteps.createUser(email, password, this.name = null);
        response
                .statusCode(SC_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
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
