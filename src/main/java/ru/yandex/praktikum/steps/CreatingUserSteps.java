package ru.yandex.praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.dto.CreateUserRequest;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.StaticUrls.*;


public class CreatingUserSteps {
    @Step("Создаем пользователя")
    public ValidatableResponse createUser(String email, String password, String name) {
        CreateUserRequest user = new CreateUserRequest();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASEURL)
                .body(user)
                .when()
                .post(CREATEUSERHANDLER)
                .then();
    }

    @Step("Удаляем пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        RestAssured.defaultParser = Parser.JSON;
        return given()
                .contentType(ContentType.JSON)
                .baseUri(BASEURL)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete(DELETEUSERHANDLER)
                .then();
    }

}
