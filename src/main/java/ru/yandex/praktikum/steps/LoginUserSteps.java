package ru.yandex.praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.dto.CreateUserRequest;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.StaticUrls.BASEURL;
import static ru.yandex.praktikum.StaticUrls.LOGINUSERHANDLER;

public class LoginUserSteps {
    @Step("Логин пользователя")
    public ValidatableResponse loginUser(String email, String password){
        CreateUserRequest loginRequest = new CreateUserRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        return given()
                .baseUri(BASEURL)
                .contentType("application/json")
                .body(loginRequest)
                .when()
                .post(LOGINUSERHANDLER) // замените на актуальный путь
                .then();
    }
}
